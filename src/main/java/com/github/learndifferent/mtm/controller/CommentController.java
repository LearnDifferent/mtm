package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.general.idempotency.IdempotencyCheck;
import com.github.learndifferent.mtm.constant.consist.ConstraintConstant;
import com.github.learndifferent.mtm.constant.consist.ErrorInfoConstant;
import com.github.learndifferent.mtm.constant.enums.Order;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.query.UpdateCommentRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.CommentService;
import com.github.learndifferent.mtm.utils.LoginUtils;
import com.github.learndifferent.mtm.vo.BookmarkCommentVO;
import com.github.learndifferent.mtm.vo.CommentVO;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Get, create, update and delete comments
 *
 * @author zhou
 * @date 2021/9/28
 */
@RestController
@RequestMapping("/comment")
@Validated
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * Get a comment
     *
     * @param id         ID of the comment.
     *                   <p>Return {@link ResultCode#FAILED} if {@code commentId} is null.</p>
     * @param bookmarkId ID of the bookmark
     * @return {@link ResultVO}<{@link CommentVO}> Return result code of {@link ResultCode#SUCCESS}
     * with the comment as data if the comment exists. If the comment does not exist, return the result code of
     * {@link ResultCode#FAILED}
     * @throws com.github.learndifferent.mtm.exception.ServiceException If the bookmark does not exist or the user
     *                                                                  does not have permissions to get the
     *                                                                  comments, {@link CommentService#getCommentByIds(Integer,
     *                                                                  long, long)} will throw an exception with
     *                                                                  the result code of {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}
     *                                                                  or {@link ResultCode#PERMISSION_DENIED}
     */
    @GetMapping
    public ResultVO<CommentVO> getCommentByIds(@RequestParam(value = "id", required = false) Integer id,
                                               @RequestParam("bookmarkId")
                                               @NotNull(message = ErrorInfoConstant.BOOKMARK_NOT_FOUND)
                                               @Positive(message = ErrorInfoConstant.BOOKMARK_NOT_FOUND)
                                                       Long bookmarkId) {
        long currentUserId = LoginUtils.getCurrentUserId();
        CommentVO comment = commentService.getCommentByIds(id, bookmarkId, currentUserId);
        return comment != null ? ResultCreator.okResult(comment) : ResultCreator.failResult();
    }

    /**
     * Get comment data of a bookmark
     *
     * @param bookmarkId       ID of the bookmark
     * @param replyToCommentId ID of the comment to reply
     *                         <p>
     *                         Null if this is not a reply
     *                         </p>
     * @param load             Amount of data to load
     * @param order            {@link Order#ASC} if ascending order, {@link Order#DESC} if descending order
     * @return Return a list of {@link BookmarkCommentVO} with the result code of {@link ResultCode#SUCCESS},
     * or an empty list with {@link ResultCode#NO_RESULTS_FOUND} if there is no comments of the bookmark
     * @throws com.github.learndifferent.mtm.exception.ServiceException If the bookmark does not exist or the user
     *                                                                  does not have permissions to get the
     *                                                                  comments, {@link CommentService#getBookmarkComments(long,
     *                                                                  Long, Integer, long, Order)}
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}
     *                                                                  or {@link ResultCode#PERMISSION_DENIED}
     */
    @GetMapping("/bookmark")
    public ResultVO<List<BookmarkCommentVO>> getComments(@RequestParam("id")
                                                             @NotNull(message = ErrorInfoConstant.BOOKMARK_NOT_FOUND)
                                                             @Positive(message = ErrorInfoConstant.BOOKMARK_NOT_FOUND)
                                                                     Long bookmarkId,
                                                         @RequestParam(value = "replyToCommentId", required = false)
                                                                     Long replyToCommentId,
                                                         @RequestParam("load") Integer load,
                                                         @RequestParam("order") Order order) {
        long currentUserId = LoginUtils.getCurrentUserId();
        List<BookmarkCommentVO> comments = commentService.getBookmarkComments(
                bookmarkId, replyToCommentId, load, currentUserId, order);

        ResultCode code = CollectionUtils.isEmpty(comments) ? ResultCode.NO_RESULTS_FOUND
                : ResultCode.SUCCESS;
        return ResultCreator.result(code, comments);
    }

    /**
     * Get the number of comments (exclude replies) of a bookmark
     *
     * @param bookmarkId ID of the bookmark
     * @return number of comments of the bookmarked website
     */
    @GetMapping("/bookmark/{id}")
    public ResultVO<Integer> countComment(@PathVariable("id")
                                          @NotNull(message = ErrorInfoConstant.BOOKMARK_NOT_FOUND)
                                          @Positive(message = ErrorInfoConstant.BOOKMARK_NOT_FOUND)
                                                  Integer bookmarkId) {
        int number = commentService.countCommentByBookmarkId(bookmarkId);
        return ResultCreator.okResult(number);
    }

    /**
     * Create a comment and send a notification to the user who is about to receive it
     *
     * @param comment          Comment
     * @param bookmarkId       ID of the bookmark
     * @param replyToCommentId ID of the comment to reply
     *                         <p>
     *                         Null if this is not a reply
     *                         </p>
     * @return {@link ResultCode#SUCCESS} if success. {@link ResultCode#FAILED} if failure.
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link CommentService#addCommentAndSendNotification(String,
     *                                                                  long, long, Long)} will throw an
     *                                                                  exception with the result code of {@link
     *                                                                  ResultCode#PERMISSION_DENIED}
     *                                                                  if the user has no permissions to comment on
     *                                                                  this bookmark.
     *                                                                  <p>
     *                                                                  It will throw an exception with
     *                                                                  {@link ResultCode#COMMENT_EXISTS} if the
     *                                                                  comment
     *                                                                  exists .
     *                                                                  </p>
     *                                                                  <p>
     *                                                                  If the bookmark does not exist, then the result
     *                                                                  code will be {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}.
     *                                                                  </p>
     *                                                                  <p>
     *                                                                  If the comment is empty or too long, the result
     *                                                                  code will be {@link ResultCode#COMMENT_EMPTY}
     *                                                                  or {@link ResultCode#COMMENT_TOO_LONG}.
     *                                                                  </p>
     *                                                                  <p>
     *                                                                  If the comment is a reply to another comment,
     *                                                                  and the "another comment" does not exist, then
     *                                                                  the result code will be {@link ResultCode#COMMENT_NOT_EXISTS}
     *                                                                  </p>
     *                                                                  <p>
     *                                                                  If it fails to add to the edit history, the
     *                                                                  result code will be {@link ResultCode#UPDATE_FAILED}
     *                                                                  </p>
     */
    @GetMapping("/create")
    @IdempotencyCheck
    public ResultVO<ResultCode> createComment(@RequestParam("comment")
                                              @NotBlank(message = ErrorInfoConstant.COMMENT_EMPTY)
                                              @Length(max = ConstraintConstant.COMMENT_MAX_LENGTH,
                                                      message = "Comment should not be longer than {max} characters")
                                                      String comment,
                                              @RequestParam("bookmarkId")
                                              @NotNull(message = ErrorInfoConstant.BOOKMARK_NOT_FOUND)
                                              @Positive(message = ErrorInfoConstant.BOOKMARK_NOT_FOUND)
                                                      Long bookmarkId,
                                              @RequestParam(value = "replyToCommentId",
                                                            required = false)
                                                      Long replyToCommentId) {
        long currentUserId = LoginUtils.getCurrentUserId();
        boolean success = commentService.addCommentAndSendNotification(
                comment, bookmarkId, currentUserId, replyToCommentId);
        return success ? ResultCreator.okResult() : ResultCreator.failResult();
    }

    /**
     * Edit a comment
     *
     * @param commentInfo Request body containing the comment information to update
     * @return {@link ResultCode#SUCCESS} if success. {@link ResultCode#FAILED} if failure.
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link CommentService#editComment(UpdateCommentRequest,
     *                                                                  String)} will throw exceptions with the
     *                                                                  result codes of {@link ResultCode#COMMENT_NOT_EXISTS}
     *                                                                  or {@link ResultCode#PERMISSION_DENIED}
     *                                                                  if the comment does not exist or the user has
     *                                                                  no permissions to update the comment.
     *                                                                  <p>
     *                                                                  It will also throw an exception if the
     *                                                                  comment existed with {@link ResultCode#COMMENT_EXISTS}.
     *                                                                  </p>
     *                                                                  <p>
     *                                                                  If the bookmark does not exist, then the result
     *                                                                  code will be {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}.
     *                                                                  </p>
     *                                                                  <p>
     *                                                                  If the comment is empty or too long, the result
     *                                                                  code will be {@link ResultCode#COMMENT_EMPTY}
     *                                                                  and {@link ResultCode#COMMENT_TOO_LONG}.
     *                                                                  </p>
     *                                                                  <p>
     *                                                                  If it fails to add to the edit history, the
     *                                                                  result code will be {@link ResultCode#UPDATE_FAILED}
     *                                                                  </p>
     */
    @PostMapping
    @IdempotencyCheck
    public ResultVO<ResultCode> updateComment(@RequestBody @Validated UpdateCommentRequest commentInfo) {
        String currentUsername = LoginUtils.getCurrentUsername();
        boolean success = commentService.editComment(commentInfo, currentUsername);
        return success ? ResultCreator.okResult() : ResultCreator.failResult();
    }

    /**
     * Delete a comment
     *
     * @param id ID of the comment
     * @return {@link ResultCode#SUCCESS} if success. {@link ResultCode#FAILED} if failure.
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link CommentService#deleteCommentById(Integer,
     *                                                                  String)}
     *                                                                  will throw exceptions with the
     *                                                                  result codes of {@link ResultCode#COMMENT_NOT_EXISTS}
     *                                                                  or {@link ResultCode#PERMISSION_DENIED}
     *                                                                  if the comment does not exist or the user has
     *                                                                  no permissions to delete the comment
     */
    @DeleteMapping
    @IdempotencyCheck
    public ResultVO<ResultCode> deleteComment(@RequestParam("id")
                                              @NotNull(message = ErrorInfoConstant.COMMENT_NOT_FOUND)
                                              @Positive(message = ErrorInfoConstant.COMMENT_NOT_FOUND)
                                                      Integer id) {
        String currentUsername = LoginUtils.getCurrentUsername();
        boolean success = commentService.deleteCommentById(id, currentUsername);
        return success ? ResultCreator.okResult() : ResultCreator.failResult();
    }
}