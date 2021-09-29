package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.annotation.validation.comment.AddCommentCheck;
import com.github.learndifferent.mtm.dto.CommentOfWebsiteDTO;
import com.github.learndifferent.mtm.entity.CommentDO;
import java.util.List;

/**
 * Comment Service
 *
 * @author zhou
 * @date 2021/9/28
 */
public interface CommentService {

    /**
     * Get a comment by id
     *
     * @param commentId comment id
     * @return the comment
     */
    CommentDO getCommentById(int commentId);

    /**
     * Gets comment by web id, username and comment
     *
     * @param comment  comment
     * @param webId    web id
     * @param username username
     * @return {@link CommentDO} comment data object
     */
    CommentDO getCommentByWebIdAndUsernameAndComment(String comment, int webId, String username);

    /**
     * Get comments by web id
     *
     * @param webId Web ID
     * @param load  Amount of data to load
     * @return the comments of the website
     */
    List<CommentOfWebsiteDTO> getCommentsByWebId(Integer webId, Integer load);

    /**
     * Gets comments by username
     *
     * @param username username
     * @return the comments
     */
    List<CommentDO> getCommentsByUsername(String username);

    /**
     * Delete a comment by id
     *
     * @param commentId comment id
     * @return success or failure
     */
    boolean deleteCommentById(int commentId);

    /**
     * Add a comment
     *
     * @param comment  comment
     * @param webId    web id
     * @param username username
     * @return success or failure
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link AddCommentCheck}
     *                                                                  annotation will throw an exception if the
     *                                                                  username is not the current user's name with
     *                                                                  the result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  and throw an exception if the comment existed
     *                                                                  with {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_EXISTS}.
     *                                                                  If the website does not exist, then the result
     *                                                                  code will be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#WEBSITE_DATA_NOT_EXISTS}.
     *                                                                  If the comment is empty or too long, the result
     *                                                                  code will be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_EMPTY}
     *                                                                  and {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_TOO_LONG}
     */
    boolean addComment(String comment, int webId, String username);

    /**
     * Update a comment
     *
     * @param comment comment
     * @return success or failure
     */
    boolean updateComment(CommentDO comment);
}
