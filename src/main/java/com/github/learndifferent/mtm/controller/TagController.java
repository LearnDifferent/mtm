package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Tag Controller
 *
 * @author zhou
 * @date 2022/3/31
 */
@RestController
@RequestMapping("/tag")
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * Apply a tag
     *
     * @param webId ID of the bookmarked website data that the user wants to apply the tag to
     * @param tag   the tag to apply
     * @return {@link ResultCreator#okResult()} if success. {@link ResultCreator#defaultFailResult()} if failure.
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link TagService#applyTag(String, Integer,
     *                                                                  String)}
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link ResultCode#WEBSITE_DATA_NOT_EXISTS} if
     *                                                                  the bookmarked website data does not exist or
     *                                                                  the {@code webId} is null.
     *                                                                  <p>
     *                                                                  If the user has no permission to apply the
     *                                                                  tag to this bookmark, the result code will be
     *                                                                  {@link ResultCode#PERMISSION_DENIED}
     *                                                                  </p>
     *                                                                  <p>
     *                                                                  It will also verify the length of a tag and
     *                                                                  throw an
     *                                                                  exception with the result code of {@link
     *                                                                  ResultCode#TAG_TOO_LONG} if the tag exceeds the
     *                                                                  maximum length.
     *                                                                  </p>
     *                                                                  <p>
     *                                                                  An exception with the result
     *                                                                  code of {@link ResultCode#TAG_NOT_EXISTS}
     *                                                                  will be thrown if the tag does not exist, and
     *                                                                  with result code of {@link ResultCode#TAG_EXISTS}
     *                                                                  if the tag has already been applied.
     *                                                                  </p>
     */
    @GetMapping("/apply")
    public ResultVO<ResultCode> applyTag(@RequestParam("webId") Integer webId, @RequestParam("tag") String tag) {
        String currentUsername = StpUtil.getLoginIdAsString();
        boolean success = tagService.applyTag(currentUsername, webId, tag);
        return success ? ResultCreator.okResult() : ResultCreator.defaultFailResult();
    }
}