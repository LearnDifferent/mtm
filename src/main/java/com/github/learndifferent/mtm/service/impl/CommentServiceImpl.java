package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.annotation.common.Comment;
import com.github.learndifferent.mtm.annotation.common.CommentId;
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
    public CommentDO getCommentById(int commentId) {
        return commentMapper.getCommentById(commentId);
    }

    @Override
    public CommentDO getCommentByWebIdAndUsernameAndComment(String comment, int webId, String username) {
        return commentMapper.getCommentByWebIdAndUsernameAndComment(comment, webId, username);
    }

    @Override
    @GetCommentsCheck
    public List<CommentOfWebsiteDTO> getCommentsByWebId(@WebId Integer webId,
                                                        Integer load,
                                                        @Username String username,
                                                        Boolean isDesc) {
        List<CommentDO> comments = commentMapper.getCommentsByWebId(webId, load, isDesc);
        return DozerUtils.convertList(comments, CommentOfWebsiteDTO.class);
    }

    @Override
    public List<CommentDO> getCommentsByUsername(String username) {
        return commentMapper.getCommentsByUsername(username);
    }

    @Override
    @ModifyCommentCheck
    public boolean deleteCommentById(@CommentId int commentId, @Username String username) {
        return commentMapper.deleteCommentById(commentId);
    }

    @Override
    @AddCommentCheck
    public boolean addComment(@Comment String comment, @WebId int webId, @Username String username) {

        CommentDTO commentDTO = CommentDTO.builder()
                .comment(comment).webId(webId).username(username)
                .creationTime(new Date())
                .build();

        return commentMapper.addComment(commentDTO);
    }

    @Override
    public boolean updateComment(UpdateCommentRequest commentInfo) {
        Integer commentId = commentInfo.getCommentId();
        String comment = commentInfo.getComment();
        String username = commentInfo.getUsername();
        Integer webId = commentInfo.getWebId();
        Date creationTime = commentInfo.getCreationTime();
        CommentServiceImpl commentServiceImpl =
                ApplicationContextUtils.getBean(CommentServiceImpl.class);
        return commentServiceImpl.updateComment(commentId, comment, username, webId, creationTime);
    }

    /**
     * 更新的评论
     *
     * @param commentId    评论id
     * @param comment      评论
     * @param username     用户名
     * @param webId        网页 ID
     * @param creationTime 创建时间
     * @return boolean 是否成功
     */
    @AddCommentCheck
    @ModifyCommentCheck
    public boolean updateComment(@CommentId Integer commentId,
                                 @Comment String comment,
                                 @Username String username,
                                 @WebId Integer webId,
                                 Date creationTime) {
        return commentMapper.updateComment(commentId, comment, username, webId, creationTime);
    }
}
