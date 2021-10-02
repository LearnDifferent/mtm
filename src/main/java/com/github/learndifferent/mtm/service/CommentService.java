package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.dto.CommentDTO;
import com.github.learndifferent.mtm.dto.CommentOfWebsiteDTO;
import com.github.learndifferent.mtm.entity.CommentDO;
import com.github.learndifferent.mtm.query.UpdateCommentRequest;
import java.util.List;

/**
 * Comment Service Interface
 *
 * @author zhou
 * @date 2021/9/28
 */
public interface CommentService {

    /**
     * Get a comment by id
     *
     * @param commentId comment id
     * @return the comment (null if comment does not exist)
     */
    CommentDTO getCommentById(Integer commentId);

    /**
     * Gets comment by web id, username and comment
     *
     * @param comment  comment
     * @param webId    web id
     * @param username username
     * @return {@link CommentDO} comment data object
     */
    CommentDTO getCommentByWebIdAndUsernameAndComment(String comment, int webId, String username);

    /**
     * Get comments by {@code webId} and {@code replyToCommentId}, and count the replies
     *
     * @param webId            Web ID
     * @param replyToCommentId Reply to another comment (null if it's not a reply)
     * @param load             Amount of data to load
     * @param username         User's name who trying to get comments
     * @param isDesc           True if descending order
     * @return the comments of the website
     * @throws com.github.learndifferent.mtm.exception.ServiceException If the website does not exist or the user
     *                                                                  does not have permissions to get the website's
     *                                                                  comments, {@link com.github.learndifferent.mtm.annotation.validation.comment.get.GetCommentsCheck}
     *                                                                  annotation will throw an exception with the
     *                                                                  result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#WEBSITE_DATA_NOT_EXISTS}
     *                                                                  or {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     */
    List<CommentOfWebsiteDTO> getCommentsByWebReplyIdAndCountReplies(Integer webId,
                                                                     Integer replyToCommentId,
                                                                     Integer load,
                                                                     String username,
                                                                     Boolean isDesc);

    /**
     * Gets comments by username
     *
     * @param username username
     * @return the comments
     */
    List<CommentDTO> getCommentsByUsername(String username);

    /**
     * Delete a comment by id
     *
     * @param commentId comment id
     * @param username  username
     * @return success or failure
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link com.github.learndifferent.mtm.annotation.validation.comment.modify.ModifyCommentCheck}
     *                                                                  annotation will throw exceptions with the
     *                                                                  result codes of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_NOT_EXISTS}
     *                                                                  or {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  if the comment does not exist or the user has
     *                                                                  no permissions to delete the comment
     */
    boolean deleteCommentById(int commentId, String username);

    /**
     * Add a comment
     *
     * @param comment          comment
     * @param webId            web id
     * @param username         username
     * @param replyToCommentId reply to another comment (null if it's not a reply)
     * @return success or failure
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link com.github.learndifferent.mtm.annotation.validation.comment.add.AddCommentCheck}
     *                                                                  annotation will throw an exception
     *                                                                  with the result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  if the username is not the current user's name
     *                                                                  or the user has no permissions to comment on
     *                                                                  this website. It will throw an exception if the
     *                                                                  comment existed with {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_EXISTS}.
     *                                                                  If the website does not exist, then the result
     *                                                                  code will be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#WEBSITE_DATA_NOT_EXISTS}.
     *                                                                  If the comment is empty or too long, the result
     *                                                                  code will be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_EMPTY}
     *                                                                  and {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_TOO_LONG}.
     *                                                                  If the comment is a reply to another comment,
     *                                                                  and the "another comment" does not exist, then
     *                                                                  the  reult code will be {@link
     *                                                                  com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_NOT_EXISTS}
     */
    boolean addComment(String comment, int webId, String username, Integer replyToCommentId);

    /**
     * Update a comment
     *
     * @param commentInfo Comment ID, Comment, Username and Website ID
     * @return success or failure
     * @throws com.github.learndifferent.mtm.exception.ServiceException This method will call an annotated method and
     *                                                                  its {@link com.github.learndifferent.mtm.annotation.validation.comment.modify.ModifyCommentCheck}
     *                                                                  annotation will throw exceptions with the
     *                                                                  result codes of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_NOT_EXISTS}
     *                                                                  or {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  if the comment does not exist or the user has
     *                                                                  no permissions to update the comment.
     *                                                                  {@link com.github.learndifferent.mtm.annotation.validation.comment.add.AddCommentCheck}
     *                                                                  annotation will throw an exception
     *                                                                  with the result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  if the username is not the current user's name
     *                                                                  or the user has no permissions to comment on
     *                                                                  this website. It will throw an exception if the
     *                                                                  comment existed with {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_EXISTS}.
     *                                                                  If the website does not exist, then the result
     *                                                                  code will be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#WEBSITE_DATA_NOT_EXISTS}.
     *                                                                  If the comment is empty or too long, the result
     *                                                                  code will be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_EMPTY}
     *                                                                  and {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_TOO_LONG}
     */
    boolean updateComment(UpdateCommentRequest commentInfo);
}
