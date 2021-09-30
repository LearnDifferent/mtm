package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.dto.CommentOfWebsiteDTO;
import com.github.learndifferent.mtm.query.UpdateCommentRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.CommentService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
     * Get comments of the website
     *
     * @param webId    Website ID
     * @param load     Amount of data to load
     * @param username User's name who trying to get comments
     * @param isDesc   True if descending order
     * @return {@link ResultVO}<{@link List}<{@link CommentOfWebsiteDTO}>> It will return a list of comments.
     * If there is no comment of the website then it will return an empty list. The result code
     * will be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#SUCCESS}
     * @throws com.github.learndifferent.mtm.exception.ServiceException If the website does not exist or the user
     *                                                                  does not have permissions to get the website's
     *                                                                  comments, {@link  CommentService#getCommentsByWebId(Integer,
     *                                                                  Integer, String, Boolean)}
     *                                                                  will throw an exception with
     *                                                                  the result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#WEBSITE_DATA_NOT_EXISTS}
     *                                                                  or {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     */
    @GetMapping
    public ResultVO<List<CommentOfWebsiteDTO>> getCommentsByWebId(@RequestParam("webId") Integer webId,
                                                                  @RequestParam("load") Integer load,
                                                                  @RequestParam("username") String username,
                                                                  @RequestParam(value = "isDesc", required = false, defaultValue = "true")
                                                                          Boolean isDesc) {
        List<CommentOfWebsiteDTO> comments = commentService.getCommentsByWebId(webId, load, username, isDesc);
        return ResultCreator.okResult(comments);
    }

    /**
     * Create a comment
     *
     * @param comment  Comment
     * @param webId    Website ID
     * @param username Username
     * @return {@link ResultVO}<{@link Boolean}> success of failure
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link CommentService#addComment(String,
     *                                                                  int, String)}
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  if the username is not the current user's name
     *                                                                  or the user has no permissions to comment on
     *                                                                  this website.
     *                                                                  It will throw an exception if the comment
     *                                                                  existed with {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_EXISTS}.
     *                                                                  If the website does not exist, then the result
     *                                                                  code will be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#WEBSITE_DATA_NOT_EXISTS}.
     *                                                                  If the comment is empty or too long, the result
     *                                                                  code will be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_EMPTY}
     *                                                                  and {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_TOO_LONG}
     */
    @GetMapping("/create")
    public ResultVO<Boolean> createComment(@RequestParam("comment") String comment,
                                           @RequestParam("webId") int webId,
                                           @RequestParam("username") String username) {
        boolean success = commentService.addComment(comment, webId, username);
        return success ? ResultCreator.okResult() : ResultCreator.failResult();
    }

    /**
     * Update a comment
     *
     * @param commentInfo Comment ID, Comment, Username, Creation time and Website ID
     * @return {@link ResultVO}<{@link Boolean}> success or failure
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link CommentService#updateComment(UpdateCommentRequest)}
     *                                                                  will throw exceptions with the
     *                                                                  result codes of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_NOT_EXISTS}
     *                                                                  or {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  if the comment does not exist or the user has
     *                                                                  no permissions to update the comment.
     *                                                                  It will also throw an exception if the
     *                                                                  comment existed with {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_EXISTS}.
     *                                                                  If the website does not exist, then the result
     *                                                                  code will be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#WEBSITE_DATA_NOT_EXISTS}.
     *                                                                  If the comment is empty or too long, the result
     *                                                                  code will be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_EMPTY}
     *                                                                  and {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_TOO_LONG}
     */
    @PostMapping
    public ResultVO<Boolean> updateComment(@RequestBody UpdateCommentRequest commentInfo) {
        boolean success = commentService.updateComment(commentInfo);
        return success ? ResultCreator.okResult() : ResultCreator.failResult();
    }

    /**
     * Delete a comment
     *
     * @param commentId comment id
     * @param username  username
     * @return success or failure
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link CommentService#deleteCommentById(int,
     *                                                                  String)}
     *                                                                  will throw exceptions with the
     *                                                                  result codes of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_NOT_EXISTS}
     *                                                                  or {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  if the comment does not exist or the user has
     *                                                                  no permissions to delete the comment
     */
    @DeleteMapping
    public ResultVO<Boolean> deleteComment(@RequestParam("commentId") int commentId,
                                           @RequestParam("username") String username) {
        boolean success = commentService.deleteCommentById(commentId, username);
        return success ? ResultCreator.okResult() : ResultCreator.failResult();
    }
}
