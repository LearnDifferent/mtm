package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.annotation.common.Comment;
import com.github.learndifferent.mtm.annotation.common.CommentId;
import com.github.learndifferent.mtm.annotation.common.ReplyToCommentId;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.WebId;
import com.github.learndifferent.mtm.annotation.validation.comment.add.AddCommentCheck;
import com.github.learndifferent.mtm.annotation.validation.comment.get.GetCommentsCheck;
import com.github.learndifferent.mtm.annotation.validation.comment.modify.ModifyCommentCheck;
import com.github.learndifferent.mtm.dto.CommentDTO;
import com.github.learndifferent.mtm.dto.CommentOfWebsiteDTO;
import com.github.learndifferent.mtm.entity.CommentDO;
import com.github.learndifferent.mtm.mapper.CommentMapper;
import com.github.learndifferent.mtm.query.UpdateCommentRequest;
import com.github.learndifferent.mtm.service.CommentService;
import com.github.learndifferent.mtm.utils.ApplicationContextUtils;
import com.github.learndifferent.mtm.utils.DozerUtils;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public CommentServiceImpl(CommentMapper commentMapper) {this.commentMapper = commentMapper;}

    @Override
    public CommentDTO getCommentById(Integer commentId) {
        if (commentId == null) {
            return null;
        }
        CommentDO comment = commentMapper.getCommentById(commentId);
        return DozerUtils.convert(comment, CommentDTO.class);
    }

    @Override
    public CommentDTO getCommentByWebIdAndUsernameAndComment(String comment, int webId, String username) {
        CommentDO commentDO = commentMapper.getCommentByWebIdAndUsernameAndComment(comment, webId, username);
        return DozerUtils.convert(commentDO, CommentDTO.class);
    }

    @Override
    @GetCommentsCheck
    public List<CommentOfWebsiteDTO> getCommentsByWebReplyIdAndCountReplies(@WebId Integer webId,
                                                                            Integer replyToCommentId,
                                                                            Integer load,
                                                                            @Username String username,
                                                                            Boolean isDesc) {
        List<CommentDO> commentList = commentMapper.getCommentsByWebAndReplyCommentId(
                webId, replyToCommentId, load, isDesc);
        List<CommentOfWebsiteDTO> comments = DozerUtils.convertList(commentList, CommentOfWebsiteDTO.class);

        comments.forEach(comment -> {
            // Get a count of the replies from this comment (comment id won't be null)
            int countRepliesFromCommentId = comment.getCommentId();
            int repliesCount = commentMapper.countRepliesFromComment(countRepliesFromCommentId);
            comment.setRepliesCount(repliesCount);
        });

        return comments;
    }

    @Override
    public List<CommentDTO> getCommentsByUsername(String username) {
        List<CommentDO> comments = commentMapper.getCommentsByUsername(username);
        return DozerUtils.convertList(comments, CommentDTO.class);
    }

    @Override
    @ModifyCommentCheck
    public boolean deleteCommentById(@CommentId int commentId, @Username String username) {
        return commentMapper.deleteCommentById(commentId);
    }

    @Override
    @AddCommentCheck
    public boolean addComment(@Comment String comment, @WebId int webId, @Username String username,
                              @ReplyToCommentId Integer replyToCommentId) {

        CommentDO commentDO = CommentDO.builder()
                .comment(comment).webId(webId).username(username)
                .replyToCommentId(replyToCommentId)
                .creationTime(new Date())
                .build();

        return commentMapper.addComment(commentDO);
    }

    @Override
    public boolean updateComment(UpdateCommentRequest commentInfo) {
        Integer commentId = commentInfo.getCommentId();
        String comment = commentInfo.getComment();
        String username = commentInfo.getUsername();
        Integer webId = commentInfo.getWebId();
        CommentServiceImpl commentServiceImpl =
                ApplicationContextUtils.getBean(CommentServiceImpl.class);
        return commentServiceImpl.updateComment(commentId, comment, username, webId);
    }

    /**
     * 更新评论
     *
     * @param commentId 评论 id：用于识别评论
     * @param comment   评论内容：更新的内容
     * @param username  用户名：用于检验
     * @param webId     网页 ID：用于检验
     * @return boolean 是否成功
     */
    @AddCommentCheck
    @ModifyCommentCheck
    public boolean updateComment(@CommentId Integer commentId,
                                 @Comment String comment,
                                 @Username String username,
                                 @WebId Integer webId) {
        return commentMapper.updateComment(commentId, comment);
    }
}
