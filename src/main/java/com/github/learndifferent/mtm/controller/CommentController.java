package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
     * Create a comment
     *
     * @param comment  Comment
     * @param webId    Website ID
     * @param username Username
     * @return {@link ResultVO}<{@link Boolean}> success of failure
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link CommentService#addComment(String,
     *                                                                  int, String)}
     *                                                                  will throw an exception if the username is not
     *                                                                  the current user's name with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  and throw an exception if the comment existed
     *                                                                  with {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_EXISTS}.
     *                                                                  If the website does not exist, then the result
     *                                                                  code will be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#WEBSITE_DATA_NOT_EXISTS}.
     *                                                                  If the comment is empty or too long, the result
     *                                                                  code will be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_EMPTY}
     *                                                                  and {@link com.github.learndifferent.mtm.constant.enums.ResultCode#COMMENT_TOO_LONG}
     */
    @GetMapping
    public ResultVO<Boolean> createComment(@RequestParam("comment") String comment,
                                           @RequestParam("webId") int webId,
                                           @RequestParam("username") String username) {
        boolean success = commentService.addComment(comment, webId, username);
        return success ? ResultCreator.okResult() : ResultCreator.failResult();
    }
}
