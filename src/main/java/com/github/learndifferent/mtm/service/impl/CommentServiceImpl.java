package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.ActionType;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.BookmarkId;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.Comment;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.CommentId;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.DataType;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.ReplyToCommentId;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.UserId;
import com.github.learndifferent.mtm.annotation.validation.comment.modify.ModifyCommentCheck;
import com.github.learndifferent.mtm.constant.enums.Order;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.CommentHistoryDTO;
import com.github.learndifferent.mtm.entity.CommentDO;
import com.github.learndifferent.mtm.entity.CommentHistoryDO;
import com.github.learndifferent.mtm.manager.NotificationManager;
import com.github.learndifferent.mtm.mapper.CommentHistoryMapper;
import com.github.learndifferent.mtm.mapper.CommentMapper;
import com.github.learndifferent.mtm.query.UpdateCommentRequest;
import com.github.learndifferent.mtm.service.CommentService;
import com.github.learndifferent.mtm.utils.ApplicationContextUtils;
import com.github.learndifferent.mtm.utils.BeanUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.learndifferent.mtm.vo.BookmarkCommentVO;
import com.github.learndifferent.mtm.vo.CommentHistoryVO;
import com.github.learndifferent.mtm.vo.CommentVO;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Comment service
 *
 * @author zhou
 * @date 2021/9/28
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final CommentHistoryMapper commentHistoryMapper;
    private final NotificationManager notificationManager;

    @Override
    @AccessPermissionCheck(dataType = DataType.COMMENT, actionType = ActionType.READ)
    public CommentVO getCommentByIds(Integer id,
                                     @BookmarkId long bookmarkId,
                                     @UserId long userId) {
        log.info("Get comment. Comment ID: {}, User ID: {}, Bookmark ID: {}", id, userId, bookmarkId);
        return Optional.ofNullable(id)
                // get the comment VO if comment ID is not null
                .map(this::getCommentByCommentIdAndBookmarkIdAndReturnCommentVO)
                // return null to signify an empty comment when the ID is null
                .orElse(null);
    }

    private CommentVO getCommentByCommentIdAndBookmarkIdAndReturnCommentVO(Integer id) {
        CommentDO commentDO = commentMapper.getCommentById(id);
        CommentVO comment = BeanUtils.convert(commentDO, CommentVO.class);
        List<CommentHistoryVO> history = getHistory(id);
        comment.setHistory(history);
        return comment;
    }

    @Override
    @AccessPermissionCheck(dataType = DataType.COMMENT, actionType = ActionType.READ)
    public List<BookmarkCommentVO> getBookmarkComments(@BookmarkId long bookmarkId,
                                                       Long replyToCommentId,
                                                       Integer load,
                                                       @UserId long userId,
                                                       Order order) {
        return commentMapper
                .getBookmarkComments(bookmarkId, replyToCommentId, load, order.isDesc())
                .stream()
                // convert to BookmarkCommentVO
                .map(commentDO -> BeanUtils.convert(commentDO, BookmarkCommentVO.class))
                // update the comment
                .peek(this::updateBookmarkComment)
                // collect comments
                .collect(Collectors.toList());
    }

    private void updateBookmarkComment(BookmarkCommentVO comment) {
        // Get a count of the replies from this comment (comment id won't be null)
        int id = comment.getId();
        int repliesCount = commentMapper.countRepliesFromComment(id);
        comment.setRepliesCount(repliesCount);
        // Get the edit history of the comment
        List<CommentHistoryVO> history = getHistory(id);
        comment.setHistory(history);
    }

    /**
     * Get edit history of the comment
     *
     * @param commentId ID of the comment
     * @return Return edit history of the comment if the comment has been edited.
     * Otherwise, return an empty list.
     */
    private List<CommentHistoryVO> getHistory(Integer commentId) {
        List<CommentHistoryDO> history = commentHistoryMapper.getHistory(commentId);
        List<CommentHistoryVO> result = BeanUtils.convertList(history, CommentHistoryVO.class);

        return CollectionUtils.isEmpty(result) ? Collections.emptyList()
                : Collections.unmodifiableList(result);
    }

    @Override
    @ModifyCommentCheck
    public boolean deleteCommentById(@CommentId Integer id, @Username String username) {
        // commentId will not be null after checking by @ModifyCommentCheck
        return commentMapper.deleteCommentById(id);
    }

    @Override
    @AccessPermissionCheck(dataType = DataType.COMMENT, actionType = ActionType.CREATE)
    public boolean addCommentAndSendNotification(@Comment String comment,
                                                 @BookmarkId long bookmarkId,
                                                 @UserId long userId,
                                                 @ReplyToCommentId Long replyToCommentId) {
        CommentDO commentDO = CommentDO.builder()
                .comment(comment)
                .bookmarkId(bookmarkId)
                .userId(userId)
                .replyToCommentId(replyToCommentId)
                .creationTime(Instant.now())
                .build();

        // this insert method uses generated keys, which means it'll set the ID to the CommentDO
        boolean success = commentMapper.addComment(commentDO);
        if (success) {
            recordHistoryAndSendNotification(commentDO);
        }

        return success;
    }

    private void recordHistoryAndSendNotification(CommentDO commentDO) {
        // add history
        Long commentId = commentDO.getId();
        String comment = commentDO.getComment();
        Instant creationTime = commentDO.getCreationTime();
        CommentHistoryDTO history = CommentHistoryDTO.of(commentId, comment, creationTime);
        addHistory(history);

        // send notification
        notificationManager.sendReplyNotification(commentDO);
    }

    @Override
    public boolean editComment(UpdateCommentRequest commentInfo, long userId) {
        long id = commentInfo.getId();
        String comment = commentInfo.getComment();
        long bookmarkId = commentInfo.getBookmarkId();

        CommentServiceImpl commentServiceImpl =
                ApplicationContextUtils.getBean(CommentServiceImpl.class);
        return commentServiceImpl.editComment(id, comment, userId, bookmarkId);
    }

    @AccessPermissionCheck(dataType = DataType.COMMENT, actionType = ActionType.UPDATE)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean editComment(@CommentId Long id,
                               @Comment String comment,
                               @UserId Long userId,
                               @BookmarkId Long bookmarkId) {
        log.info("Editing comment {}. New Comment: {}, User ID: {}, Bookmark ID: {}", id, comment, userId, bookmarkId);
        boolean success = commentMapper.updateComment(id, comment);
        if (success) {
            log.info("Edited comment {}. Comment: {}, User ID: {}, Bookmark ID: {}", id, comment, userId, bookmarkId);
            CommentHistoryDTO history = CommentHistoryDTO.of(id, comment);
            addHistory(history);
        }
        return success;
    }

    private void addHistory(CommentHistoryDTO history) {
        boolean notSuccess = !commentHistoryMapper.addHistory(history);
        ThrowExceptionUtils.throwIfTrue(notSuccess, ResultCode.UPDATE_FAILED);
    }

    @Override
    @Cacheable(value = "comment:count", key = "#bookmarkId")
    public int countCommentByBookmarkId(Integer bookmarkId) {
        return Optional.ofNullable(bookmarkId)
                .map(commentMapper::countCommentByBookmarkId)
                .orElse(0);
    }
}