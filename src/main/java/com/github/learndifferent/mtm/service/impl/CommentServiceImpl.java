package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.annotation.common.Comment;
import com.github.learndifferent.mtm.annotation.common.CommentId;
import com.github.learndifferent.mtm.annotation.common.ReplyToCommentId;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.BookmarkId;
import com.github.learndifferent.mtm.annotation.validation.comment.add.AddCommentCheck;
import com.github.learndifferent.mtm.annotation.validation.comment.get.GetCommentsCheck;
import com.github.learndifferent.mtm.annotation.validation.comment.modify.ModifyCommentCheck;
import com.github.learndifferent.mtm.constant.enums.Order;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.CommentHistoryDTO;
import com.github.learndifferent.mtm.entity.CommentDO;
import com.github.learndifferent.mtm.entity.CommentHistoryDO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.manager.NotificationManager;
import com.github.learndifferent.mtm.mapper.CommentHistoryMapper;
import com.github.learndifferent.mtm.mapper.CommentMapper;
import com.github.learndifferent.mtm.query.UpdateCommentRequest;
import com.github.learndifferent.mtm.service.CommentService;
import com.github.learndifferent.mtm.utils.ApplicationContextUtils;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.vo.BookmarkCommentVO;
import com.github.learndifferent.mtm.vo.CommentHistoryVO;
import com.github.learndifferent.mtm.vo.CommentVO;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final CommentHistoryMapper commentHistoryMapper;
    private final NotificationManager notificationManager;

    @Autowired
    public CommentServiceImpl(CommentMapper commentMapper,
                              CommentHistoryMapper commentHistoryMapper,
                              NotificationManager notificationManager) {
        this.commentMapper = commentMapper;
        this.commentHistoryMapper = commentHistoryMapper;
        this.notificationManager = notificationManager;
    }

    @Override
    @GetCommentsCheck
    public CommentVO getCommentById(Integer commentId,
                                    @BookmarkId Integer bookmarkId,
                                    @Username String username) {
        if (commentId == null) {
            return null;
        }
        CommentDO commentDO = commentMapper.getCommentById(commentId);
        CommentVO comment = DozerUtils.convert(commentDO, CommentVO.class);
        List<CommentHistoryVO> history = getHistory(commentId);
        comment.setHistory(history);
        return comment;
    }

    @Override
    @GetCommentsCheck
    public List<BookmarkCommentVO> getBookmarkComments(@BookmarkId Integer bookmarkId,
                                                       Integer replyToCommentId,
                                                       Integer load,
                                                       @Username String username,
                                                       Order order) {
        List<CommentDO> commentList = commentMapper.getBookmarkComments(
                bookmarkId, replyToCommentId, load, order.isDesc());
        List<BookmarkCommentVO> comments = DozerUtils.convertList(commentList, BookmarkCommentVO.class);

        comments.forEach(comment -> {
            // Get a count of the replies from this comment (comment id won't be null)
            int id = comment.getCommentId();
            int repliesCount = commentMapper.countRepliesFromComment(id);
            comment.setRepliesCount(repliesCount);
            // Get the edit history of the comment
            List<CommentHistoryVO> history = getHistory(id);
            comment.setHistory(history);
        });
        return Collections.unmodifiableList(comments);
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
        List<CommentHistoryVO> result = DozerUtils.convertList(history, CommentHistoryVO.class);
        result = result.size() > 1 ? result : Collections.emptyList();
        return Collections.unmodifiableList(result);
    }

    @Override
    @ModifyCommentCheck
    public boolean deleteCommentById(@CommentId Integer commentId, @Username String username) {
        // commentId will not be null after checking by @ModifyCommentCheck
        return commentMapper.deleteCommentById(commentId);
    }

    @Override
    @AddCommentCheck
    public boolean addCommentAndSendNotification(@Comment String comment,
                                                 @BookmarkId Integer bookmarkId,
                                                 @Username String username,
                                                 @ReplyToCommentId Integer replyToCommentId) {
        // webId will not be null after checking by @AddCommentCheck
        CommentDO commentDO = CommentDO.builder()
                .comment(comment).bookmarkId(bookmarkId).username(username)
                .replyToCommentId(replyToCommentId)
                .creationTime(Instant.now())
                .build();

        // this add method is using generated keys, which means it'll set the ID to the CommentDO
        boolean success = commentMapper.addComment(commentDO);
        if (success) {
            recordHistoryAndSendNotification(commentDO);
        }

        return success;
    }

    private void recordHistoryAndSendNotification(CommentDO commentDO) {
        // add history
        Integer commentId = commentDO.getCommentId();
        String comment = commentDO.getComment();
        Instant creationTime = commentDO.getCreationTime();
        CommentHistoryDTO history = CommentHistoryDTO.of(commentId, comment, creationTime);
        addHistory(history);

        // send notification
        notificationManager.sendReplyNotification(commentDO);
    }

    @Override
    public boolean editComment(UpdateCommentRequest commentInfo, String username) {
        Integer commentId = commentInfo.getCommentId();
        String comment = commentInfo.getComment();
        Integer bookmarkId = commentInfo.getWebId();

        CommentServiceImpl commentServiceImpl =
                ApplicationContextUtils.getBean(CommentServiceImpl.class);
        return commentServiceImpl.editComment(commentId, comment, username, bookmarkId);
    }

    @AddCommentCheck
    @ModifyCommentCheck
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean editComment(@CommentId Integer commentId,
                               @Comment String comment,
                               @Username String username,
                               @BookmarkId Integer webId) {
        // commentId will not be null after checking by @ModifyCommentCheck
        boolean success = commentMapper.updateComment(commentId, comment);
        if (success) {
            CommentHistoryDTO history = CommentHistoryDTO.of(commentId, comment);
            addHistory(history);
        }
        return success;
    }

    private void addHistory(CommentHistoryDTO history) {
        boolean notSuccess = !commentHistoryMapper.addHistory(history);
        if (notSuccess) {
            throw new ServiceException(ResultCode.UPDATE_FAILED);
        }
    }

    @Override
    @Cacheable(value = "comment:count", key = "#bookmarkId")
    public int countCommentByBookmarkId(Integer bookmarkId) {
        if (bookmarkId == null) {
            return 0;
        }
        return commentMapper.countCommentByBookmarkId(bookmarkId);
    }
}