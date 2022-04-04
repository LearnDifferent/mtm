package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.BookmarkCommentDTO;
import com.github.learndifferent.mtm.dto.CommentDTO;
import com.github.learndifferent.mtm.query.UpdateCommentRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.CommentService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
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
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {this.commentService = commentService;}

    /**
     * Get comment data of a bookmark
     *
     * @param webId            ID of the bookmarked website data
     * @param replyToCommentId Reply to another comment (null if it's not a reply)
     * @param load             Amount of data to load
     * @param isDesc           True if descending order
     * @return Return a list of {@link BookmarkCommentDTO} with the result code of {@link ResultCode#SUCCESS},
     * or an empty list with {@link ResultCode#NO_RESULTS_FOUND} if there is no comments of the bookmark
     * @throws com.github.learndifferent.mtm.exception.ServiceException If the bookmark does not exist or the user
     *                                                                  does not have permissions to get the
     *                                                                  comments, {@link CommentService#getBookmarkComments(Integer,
     *                                                                  Integer, Integer, String, Boolean)}
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}
     *                                                                  or {@link ResultCode#PERMISSION_DENIED}
     */
    @GetMapping
    public ResultVO<List<BookmarkCommentDTO>> getComments(@RequestParam("webId") Integer webId,
                                                          @RequestParam(value = "replyToCommentId", required = false)
                                                                  Integer replyToCommentId,
                                                          @RequestParam("load") Integer load,
                                                          @RequestParam(value = "isDesc", required = false, defaultValue = "true")
                                                                  Boolean isDesc) {
        String currentUsername = getCurrentUsername();
        List<BookmarkCommentDTO> comments = commentService.getBookmarkComments(
                webId, replyToCommentId, load, currentUsername, isDesc);

        ResultCode code = CollectionUtils.isEmpty(comments) ? ResultCode.NO_RESULTS_FOUND
                : ResultCode.SUCCESS;
        return ResultCreator.result(code, comments);
    }

    /**
     * Get a comment
     *
     * @param commentId ID of the comment
     * @return {@link ResultVO}<{@link CommentDTO}> Return result code of {@link ResultCode#SUCCESS}
     * with the comment as data if the comment exists. If the comment does not exist, return the result code of
     * {@link ResultCode#FAILED}
     */
    @GetMapping("/get")
    public ResultVO<CommentDTO> getCommentById(@RequestParam(value = "commentId", required = false) Integer commentId) {
        CommentDTO comment = commentService.getCommentById(commentId);
        return comment != null ? ResultCreator.okResult(comment) : ResultCreator.failResult();
    }

    /**
     * Create a comment and send a notification to the user who is about to receive it
     *
     * @param comment          Comment
     * @param webId            ID of the bookmarked website data
     * @param replyToCommentId Reply to another comment (null if it's not a reply)
     * @return {@link ResultVO}<{@link Boolean}> True if success
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link CommentService#addCommentAndSendNotification(String,
     *                                                                  Integer, String, Integer)} will throw an
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
     */
    @GetMapping("/create")
    public ResultVO<Boolean> createComment(@RequestParam("comment") String comment,
                                           @RequestParam("webId") Integer webId,
                                           @RequestParam(value = "replyToCommentId",
                                                         required = false) Integer replyToCommentId) {
        String currentUsername = getCurrentUsername();
        boolean success = commentService.addCommentAndSendNotification(
                comment, webId, currentUsername, replyToCommentId);
        return success ? ResultCreator.okResult() : ResultCreator.failResult();
    }

    /**
     * Update a comment
     *
     * @param commentInfo Request body containing the comment information to update
     * @return {@link ResultVO}<{@link Boolean}> true if success
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link CommentService#updateComment(UpdateCommentRequest,
     *                                                                  String)} will throw exceptions with the
     *                                                                  result codes of {@link ResultCode#COMMENT_NOT_EXISTS}
     *                                                                  or {@link ResultCode#PERMISSION_DENIED}
     *                                                                  if the comment does not exist or the user has
     *                                                                  no permissions to update the comment.
     *                                                                  It will also throw an exception if the
     *                                                                  comment existed with {@link ResultCode#COMMENT_EXISTS}.
     *                                                                  If the website does not exist, then the result
     *                                                                  code will be {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}.
     *                                                                  If the comment is empty or too long, the result
     *                                                                  code will be {@link ResultCode#COMMENT_EMPTY}
     *                                                                  and {@link ResultCode#COMMENT_TOO_LONG}
     */
    @PostMapping
    public ResultVO<Boolean> updateComment(@RequestBody UpdateCommentRequest commentInfo) {
        String currentUsername = getCurrentUsername();
        boolean success = commentService.updateComment(commentInfo, currentUsername);
        return success ? ResultCreator.okResult() : ResultCreator.failResult();
    }

    /**
     * Delete a comment
     *
     * @param commentId ID of the comment
     * @return success or failure
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link CommentService#deleteCommentById(Integer,
     *                                                                  String)}
     *                                                                  will throw exceptions with the
     *                                                                  result codes of {@link ResultCode#COMMENT_NOT_EXISTS}
     *                                                                  or {@link ResultCode#PERMISSION_DENIED}
     *                                                                  if the comment does not exist or the user has
     *                                                                  no permissions to delete the comment
     */
    @DeleteMapping
    public ResultVO<Boolean> deleteComment(@RequestParam("commentId") Integer commentId) {
        String currentUsername = getCurrentUsername();
        boolean success = commentService.deleteCommentById(commentId, currentUsername);
        return success ? ResultCreator.okResult() : ResultCreator.failResult();
    }

    /**
     * Get the number of comments (exclude replies) of a bookmarked website
     *
     * @param webId ID of the bookmarked website data
     * @return number of comments of the bookmarked website
     */
    @GetMapping("/get/{webId}")
    public ResultVO<Integer> countComment(@PathVariable("webId") Integer webId) {
        int number = commentService.countCommentByWebId(webId);
        return ResultCreator.okResult(number);
    }

    private String getCurrentUsername() {
        return StpUtil.getLoginIdAsString();
    }
}