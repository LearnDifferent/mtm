package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.annotation.common.Comment;
import com.github.learndifferent.mtm.annotation.common.CommentId;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.WebId;
import com.github.learndifferent.mtm.annotation.validation.comment.add.AddCommentCheck;
import com.github.learndifferent.mtm.annotation.validation.comment.delete.DeleteCommentCheck;
import com.github.learndifferent.mtm.annotation.validation.comment.get.GetCommentsCheck;
import com.github.learndifferent.mtm.dto.CommentDTO;
import com.github.learndifferent.mtm.dto.CommentOfWebsiteDTO;
import com.github.learndifferent.mtm.entity.CommentDO;
import com.github.learndifferent.mtm.mapper.CommentMapper;
import com.github.learndifferent.mtm.service.CommentService;
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
    public List<CommentOfWebsiteDTO> getCommentsByWebId(@WebId Integer webId, Integer load, @Username String username) {
        List<CommentDO> comments = commentMapper.getCommentsByWebId(webId, load);
        return DozerUtils.convertList(comments, CommentOfWebsiteDTO.class);
    }

    @Override
    public List<CommentDO> getCommentsByUsername(String username) {
        return commentMapper.getCommentsByUsername(username);
    }

    @Override
    @DeleteCommentCheck
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
    public boolean updateComment(CommentDO comment) {
        return commentMapper.updateComment(comment);
    }
}
