package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.dto.CommentOfWebsiteDTO;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.CommentService;
import java.util.List;
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
     * Get comments of the website
     *
     * @param webId Website ID
     * @param load  Amount of data to load
     * @return {@link ResultVO}<{@link List}<{@link CommentOfWebsiteDTO}>> It will return a list of comments. If the
     * website does not exist or there is no comment of the website then it will return an empty list. The result code
     * will be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#SUCCESS}
     */
    @GetMapping
    public ResultVO<List<CommentOfWebsiteDTO>> getCommentsByWebId(@RequestParam("webId") Integer webId,
                                                                  @RequestParam("load") Integer load) {
        List<CommentOfWebsiteDTO> comments = commentService.getCommentsByWebId(webId, load);
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
}
