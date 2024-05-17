package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.BookmarkId;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.Comment;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.CommentId;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.DataAccessType;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.ReplyToCommentId;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.UserId;
import com.github.learndifferent.mtm.constant.enums.Order;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.CommentDTO;
import com.github.learndifferent.mtm.dto.CommentHistoryDTO;
import com.github.learndifferent.mtm.entity.CommentDO;
import com.github.learndifferent.mtm.entity.CommentHistoryDO;
import com.github.learndifferent.mtm.manager.NotificationManager;
import com.github.learndifferent.mtm.mapper.CommentHistoryMapper;
import com.github.learndifferent.mtm.mapper.CommentMapper;
import com.github.learndifferent.mtm.query.UpdateCommentRequest;
import com.github.learndifferent.mtm.service.CommentService;
import com.github.learndifferent.mtm.service.IdGeneratorService;
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
    private final IdGeneratorService idGeneratorService;

    @Override
    @AccessPermissionCheck(dataAccessType = DataAccessType.COMMENT_READ)
    public CommentVO getCommentByIds(Long id,
                                     @BookmarkId long bookmarkId,
                                     @UserId long userId) {
        log.info("[CommentService] Getting comment. Comment ID: {}, User ID: {}, Bookmark ID: {}",
                id, userId, bookmarkId);
        CommentVO comment = Optional.ofNullable(id)
                // get the comment VO if comment ID is not null
                .map(this::getCommentByCommentIdAndBookmarkIdAndReturnCommentVO)
                // return null to signify an empty comment when the ID is null
                .orElse(null);
        log.info("[CommentService] Get comment. Comment ID: {}, User ID: {}, Bookmark ID: {}, Comment: {}",
                id, userId, bookmarkId, comment);
        return comment;
    }

    private CommentVO getCommentByCommentIdAndBookmarkIdAndReturnCommentVO(long id) {
        CommentDTO commentDTO = commentMapper.getCommentById(id);
        CommentVO comment = BeanUtils.convert(commentDTO, CommentVO.class);
        List<CommentHistoryVO> history = getHistory(id);
        comment.setHistory(history);
        return comment;
    }

    @Override
    @AccessPermissionCheck(dataAccessType = DataAccessType.COMMENT_READ)
    public List<BookmarkCommentVO> getBookmarkComments(@BookmarkId long bookmarkId,
                                                       Long replyToCommentId,
                                                       Integer load,
                                                       @UserId long userId,
                                                       Order order) {
        log.info(
                "[CommentService] Get Bookmark (ID: {}) Comment, User ID: {}, Reply to comment ID: {}, Load: {}, Order: {}",
                bookmarkId, userId, replyToCommentId, load, order);

        List<BookmarkCommentVO> result = commentMapper
                .getBookmarkComments(bookmarkId, replyToCommentId, load, order.isDesc())
                .stream()
                // convert to BookmarkCommentVO
                .map(commentDTO -> BeanUtils.convert(commentDTO, BookmarkCommentVO.class))
                // update the comment
                .peek(this::updateBookmarkComment)
                // collect comments
                .collect(Collectors.toList());

        log.info(
                "[CommentService] Bookmark (ID: {}) Comment, User ID: {}, Reply to comment ID: {}, Load: {}, Order: {}, Result: {}",
                bookmarkId, userId, replyToCommentId, load, order, result);
        return result;
    }

    private void updateBookmarkComment(BookmarkCommentVO comment) {
        // Get a count of the replies from this comment (comment id won't be null)
        long id = comment.getId();
        long repliesCount = commentMapper.countRepliesFromComment(id);
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
    private List<CommentHistoryVO> getHistory(long commentId) {
        log.info("[CommentService] Get history of the comment: {}", commentId);
        List<CommentHistoryDO> history = commentHistoryMapper.getHistory(commentId);
        List<CommentHistoryVO> result = BeanUtils.convertList(history, CommentHistoryVO.class);
        log.info("[CommentService] History of the comment is: {}", result);

        return CollectionUtils.isEmpty(result) ? Collections.emptyList()
                : Collections.unmodifiableList(result);
    }

    @Override
    @AccessPermissionCheck(dataAccessType = DataAccessType.COMMENT_DELETE)
    public boolean deleteCommentById(@CommentId long id, @UserId long userId) {
        log.info("[CommentService] Deleting comment. Comment ID: {}, User ID: {}", id, userId);
        boolean result = commentMapper.deleteCommentById(id);
        log.info("[CommentService] Deleted comment. Comment ID: {}, User ID: {}, Result: {}", id, userId, result);
        return result;
    }

    @Override
    @AccessPermissionCheck(dataAccessType = DataAccessType.COMMENT_CREATE)
    public boolean addCommentAndSendNotification(@Comment String comment,
                                                 @BookmarkId long bookmarkId,
                                                 @UserId long userId,
                                                 @ReplyToCommentId Long replyToCommentId) {
        long id = idGeneratorService.generateId();
        CommentDO commentDO = CommentDO.builder()
                .id(id)
                .comment(comment)
                .bookmarkId(bookmarkId)
                .userId(userId)
                .replyToCommentId(replyToCommentId)
                .creationTime(Instant.now())
                .build();

        log.info("[CommentService] Adding comment. Comment ID: {}, Comment: {}, User ID: {}, Bookmark ID: {}",
                id, comment, userId, bookmarkId);
        boolean success = commentMapper.addComment(commentDO);

        if (success) {
            log.info("[CommentService] Comment added. Comment ID: {}, Comment: {}, User ID: {}, Bookmark ID: {}",
                    id, comment, userId, bookmarkId);
            recordHistoryAndSendNotification(commentDO);
            log.info(
                    "[CommentService] Comment history and notification sent. Comment ID: {}, Comment: {}, User ID: {}, Bookmark ID: {}",
                    id, comment, userId, bookmarkId);
        } else {
            log.info(
                    "[CommentService] Failed to add comment. Comment ID: {}, Comment: {}, User ID: {}, Bookmark ID: {}",
                    id, comment, userId, bookmarkId);
        }

        return success;
    }

    private void recordHistoryAndSendNotification(CommentDO commentDO) {
        // add history
        Long commentId = commentDO.getId();
        String comment = commentDO.getComment();
        Instant creationTime = commentDO.getCreationTime();
        long id = idGeneratorService.generateId();

        log.info("[CommentService] Adding comment history. ID: {}, Comment ID: {}, Comment: {}, Creation Time: {}",
                id, commentId, comment, creationTime);
        CommentHistoryDTO history = CommentHistoryDTO.of(id, commentId, comment, creationTime);
        addHistory(history);
        log.info("[CommentService] Comment history added. ID: {}, Comment ID: {}, Comment: {}, Creation Time: {}",
                id, commentId, comment, creationTime);

        // send notification
        log.info("[CommentService] Sending comment notification. Comment ID: {}, Comment: {}, Creation Time: {}",
                commentId, comment, creationTime);
        notificationManager.sendReplyNotification(commentDO);
        log.info("[CommentService] Comment notification sent. Comment ID: {}, Comment: {}, Creation Time: {}",
                commentId, comment, creationTime);
    }

    @Override
    public boolean editComment(UpdateCommentRequest commentInfo, long userId) {
        long id = commentInfo.getId();
        String comment = commentInfo.getComment();
        long bookmarkId = commentInfo.getBookmarkId();
        log.info("[CommentService] Editing comment BEGIN. ID: {}, New Comment: {}, User ID: {}, Bookmark ID: {}",
                id, comment, userId, bookmarkId);

        CommentServiceImpl commentServiceImpl =
                ApplicationContextUtils.getBean(CommentServiceImpl.class);
        boolean success = commentServiceImpl.editComment(id, comment, userId, bookmarkId);
        log.info("[CommentService] Edited comment END. Success: {}, ID: {}, Comment: {}, User ID: {}, Bookmark ID: {}",
                success, id, comment, userId, bookmarkId);

        return success;
    }

    @AccessPermissionCheck(dataAccessType = DataAccessType.COMMENT_UPDATE)
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean editComment(@CommentId Long id,
                               @Comment String comment,
                               @UserId Long userId,
                               @BookmarkId Long bookmarkId) {
        log.info("[CommentService] Editing comment {}. New Comment: {}, User ID: {}, Bookmark ID: {}", id, comment,
                userId, bookmarkId);
        boolean success = commentMapper.updateComment(id, comment);
        if (success) {
            log.info("[CommentService] Edited comment {}. Comment: {}, User ID: {}, Bookmark ID: {}", id, comment,
                    userId, bookmarkId);
            long commentHistoryId = idGeneratorService.generateId();
            CommentHistoryDTO history = CommentHistoryDTO.of(commentHistoryId, id, comment);
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
    public long countCommentByBookmarkId(Long bookmarkId) {
        log.info("[CommentService] Counting comments by bookmark ID: {}", bookmarkId);
        long result = Optional.ofNullable(bookmarkId)
                .map(commentMapper::countCommentByBookmarkId)
                .orElse(0L);
        log.info("[CommentService] Counted comments by bookmark ID: {}. Result: {}",
                bookmarkId, result);
        return result;
    }
}
