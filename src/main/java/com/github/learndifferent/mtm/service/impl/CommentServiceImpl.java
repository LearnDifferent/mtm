package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.annotation.common.Comment;
import com.github.learndifferent.mtm.annotation.common.CommentId;
import com.github.learndifferent.mtm.annotation.common.ReplyToCommentId;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.WebId;
import com.github.learndifferent.mtm.annotation.validation.comment.add.AddCommentCheck;
import com.github.learndifferent.mtm.annotation.validation.comment.get.GetCommentsCheck;
import com.github.learndifferent.mtm.annotation.validation.comment.modify.ModifyCommentCheck;
import com.github.learndifferent.mtm.dto.BookmarkCommentDTO;
import com.github.learndifferent.mtm.dto.CommentDTO;
import com.github.learndifferent.mtm.entity.CommentDO;
import com.github.learndifferent.mtm.manager.NotificationManager;
import com.github.learndifferent.mtm.mapper.CommentMapper;
import com.github.learndifferent.mtm.query.UpdateCommentRequest;
import com.github.learndifferent.mtm.service.CommentService;
import com.github.learndifferent.mtm.utils.ApplicationContextUtils;
import com.github.learndifferent.mtm.utils.DozerUtils;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Comment service
 *
 * @author zhou
 * @date 2021/9/28
 */
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final NotificationManager notificationManager;

    @Autowired
    public CommentServiceImpl(CommentMapper commentMapper, NotificationManager notificationManager) {
        this.commentMapper = commentMapper;
        this.notificationManager = notificationManager;
    }

    @Override
    public CommentDTO getCommentById(Integer commentId) {
        if (commentId == null) {
            return null;
        }
        CommentDO comment = commentMapper.getCommentById(commentId);
        return DozerUtils.convert(comment, CommentDTO.class);
    }

    @Override
    public CommentDTO getComment(String comment, int webId, String username) {
        CommentDO commentDO = commentMapper.getCommentByWebIdAndUsernameAndComment(comment, webId, username);
        return DozerUtils.convert(commentDO, CommentDTO.class);
    }

    @Override
    @GetCommentsCheck
    public List<BookmarkCommentDTO> getBookmarkComments(@WebId Integer webId,
                                                        Integer replyToCommentId,
                                                        Integer load,
                                                        @Username String username,
                                                        Boolean isDesc) {
        List<CommentDO> commentList = commentMapper.getCommentsByWebAndReplyCommentId(
                webId, replyToCommentId, load, isDesc);
        List<BookmarkCommentDTO> comments = DozerUtils.convertList(commentList, BookmarkCommentDTO.class);

        comments.forEach(comment -> {
            // Get a count of the replies from this comment (comment id won't be null)
            int countRepliesFromCommentId = comment.getCommentId();
            int repliesCount = commentMapper.countRepliesFromComment(countRepliesFromCommentId);
            comment.setRepliesCount(repliesCount);
        });

        return comments;
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
                                                 @WebId Integer webId,
                                                 @Username String username,
                                                 @ReplyToCommentId Integer replyToCommentId) {
        // webId will not be null after checking by @AddCommentCheck
        CommentDO commentDO = CommentDO.builder()
                .comment(comment).webId(webId).username(username)
                .replyToCommentId(replyToCommentId)
                .creationTime(Instant.now())
                .build();

        // this method will set the ID to the CommentDO automatically
        boolean success = commentMapper.addCommentAndGetId(commentDO);
        if (success) {
            // send notification
            notificationManager.sendReplyNotification(commentDO);
        }

        return success;
    }

    @Override
    public boolean updateComment(UpdateCommentRequest commentInfo, String username) {
        Integer commentId = commentInfo.getCommentId();
        String comment = commentInfo.getComment();
        Integer webId = commentInfo.getWebId();

        CommentServiceImpl commentServiceImpl =
                ApplicationContextUtils.getBean(CommentServiceImpl.class);
        return commentServiceImpl.updateComment(commentId, comment, username, webId);
    }

    @AddCommentCheck
    @ModifyCommentCheck
    public boolean updateComment(@CommentId Integer commentId,
                                 @Comment String comment,
                                 @Username String username,
                                 @WebId Integer webId) {
        return commentMapper.updateComment(commentId, comment);
    }

    @Override
    @Cacheable(value = "comment:count", key = "#webId")
    public int countCommentByWebId(Integer webId) {
        if (webId == null) {
            return 0;
        }
        return commentMapper.countCommentByWebId(webId);
    }
}
