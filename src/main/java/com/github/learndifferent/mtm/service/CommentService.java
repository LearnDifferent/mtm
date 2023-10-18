package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.constant.enums.Order;
import com.github.learndifferent.mtm.query.UpdateCommentRequest;
import com.github.learndifferent.mtm.vo.BookmarkCommentVO;
import com.github.learndifferent.mtm.vo.CommentVO;
import java.util.List;

/**
 * Comment Service Interface
 *
 * @author zhou
 * @date 2021/9/28
 */
public interface CommentService {

    /**
     * Get a comment by IDs
     *
     * @param id         ID of the comment (Return null if {@code id} is null)
     * @param bookmarkId ID of the bookmark
     * @param userId     ID of the user who is trying to get the comment
     * @return the comment (null if comment does not exist)
     * @throws com.github.learndifferent.mtm.exception.ServiceException If the bookmark does not exist or the user
     *                                                                  does not have permissions to get the website's
     *                                                                  comments, {@link com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck
     *                                                                  AccessPermissionCheck} annotation will throw an
     *                                                                  exception with the
     *                                                                  result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#WEBSITE_DATA_NOT_EXISTS
     *                                                                  WEBSITE_DATA_NOT_EXISTS}
     *                                                                  or {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED
     *                                                                  PERMISSION_DENIED}
     */
    CommentVO getCommentByIds(Long id, long bookmarkId, long userId);

    /**
     * Get comments of a bookmark
     *
     * @param bookmarkId       ID of the bookmark
     * @param replyToCommentId ID of the comment to reply
     *                         <p>
     *                         null if this is not a reply
     *                         </p>
     * @param load             Amount of data to load
     * @param userId           ID of the user who is trying to get comments
     * @param order            {@link Order#ASC} if ascending order, {@link Order#DESC} if descending order
     * @return Return a list of {@link BookmarkCommentVO} or an empty list if there is no comment of the bookmark
     * @throws com.github.learndifferent.mtm.exception.ServiceException If the bookmark does not exist or the user
     *                                                                  does not have permissions to get the website's
     *                                                                  comments, {@link com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck
     *                                                                  AccessPermissionCheck} annotation will throw an
     *                                                                  exception with the
     *                                                                  result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#WEBSITE_DATA_NOT_EXISTS
     *                                                                  WEBSITE_DATA_NOT_EXISTS}
     *                                                                  or {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED
     *                                                                  PERMISSION_DENIED}
     */
    List<BookmarkCommentVO> getBookmarkComments(long bookmarkId,
                                                Long replyToCommentId,
                                                Integer load,
                                                long userId,
                                                Order order);

    /**
     * Delete a comment by id
     *
     * @param id     ID of the comment
     * @param userId ID user ID of the user who is trying to delete the comment
     * @return true if success
     * @throws com.github.learndifferent.mtm.exception.ServiceException This will throw
     *                                                                  exceptions with the
     *                                                                  result codes of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_NOT_EXISTS
     *                                                                  COMMENT_NOT_EXISTS} or {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED
     *                                                                  PERMISSION_DENIED} if the comment does not
     *                                                                  exist or the user has no permissions to delete
     *                                                                  the comment
     */
    boolean deleteCommentById(long id, long userId);

    /**
     * Add a comment and send a notification to the user who is about to receive it
     *
     * @param comment          comment
     * @param bookmarkId       ID of the bookmark
     * @param userId           User ID of the user who is trying to add the comment
     * @param username         Username of the user who is trying to add the comment
     * @param replyToCommentId ID of the comment to reply
     *                         <p>
     *                         null if this is not a reply
     *                         </p>
     * @return true if success
     * @throws com.github.learndifferent.mtm.exception.ServiceException This will throw an
     *                                                                  exception with the result code of {@link
     *                                                                  com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED
     *                                                                  PERMISSION_DENIED}
     *                                                                  if the user has no permissions to comment on
     *                                                                  this bookmark.
     *                                                                  <p>
     *                                                                  It will throw an exception with
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_EXISTS
     *                                                                  COMMENT_EXISTS} if the comment exists .
     *                                                                  </p>
     *                                                                  <p>
     *                                                                  If the bookmark does not exist, then the result
     *                                                                  code will be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#WEBSITE_DATA_NOT_EXISTS
     *                                                                  WEBSITE_DATA_NOT_EXISTS}.
     *                                                                  </p>
     *                                                                  <p>
     *                                                                  If the comment is empty or too long, the result
     *                                                                  code will be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_EMPTY
     *                                                                  COMMENT_EMPTY}
     *                                                                  or {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_TOO_LONG
     *                                                                  COMMENT_TOO_LONG}.
     *                                                                  </p>
     *                                                                  <p>
     *                                                                  If the comment is a reply to another comment,
     *                                                                  and the "another comment" does not exist, then
     *                                                                  the result code will be {@link
     *                                                                  com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_NOT_EXISTS
     *                                                                  COMMENT_NOT_EXISTS}
     *                                                                  </p>
     *                                                                  <p>
     *                                                                  If it fails to update the comment history, the
     *                                                                  result code will be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#UPDATE_FAILED}
     *                                                                  </p>
     */
    boolean addCommentAndSendNotification(String comment,
                                          long bookmarkId,
                                          long userId,
                                          String username,
                                          Long replyToCommentId);

    /**
     * Edit a comment
     *
     * @param commentInfo comment information to update
     * @param userId      user ID of the user who is trying to update the comment
     * @return true if success
     * @throws com.github.learndifferent.mtm.exception.ServiceException This method will throw exceptions with the
     *                                                                  result codes of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_NOT_EXISTS}
     *                                                                  or {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  if the comment does not exist or the user has
     *                                                                  no permissions to update the comment.
     *                                                                  And also will throw an exception
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
     *                                                                  If it fails to update the edit history, the
     *                                                                  result code will be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#UPDATE_FAILED}
     */
    boolean editComment(UpdateCommentRequest commentInfo, long userId);

    /**
     * Get the number of comments (exclude replies) of a bookmarked website
     * and the result will be stored in the cache
     *
     * @param bookmarkId ID of the bookmark
     * @return number of comments of the bookmarked website
     */
    long countCommentByBookmarkId(Long bookmarkId);
}