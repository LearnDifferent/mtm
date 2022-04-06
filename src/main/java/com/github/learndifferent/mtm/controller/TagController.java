package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.constant.enums.PageInfoParam;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.PopularTagDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.TagService;
import com.github.learndifferent.mtm.vo.SearchByTagResultVO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
     * @param webId   ID of the bookmarked website data that the user currently logged in wants to apply the tag to
     * @param tagName the tag to apply
     * @return Return the tag if applied successfully, or empty string if failed to apply
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
    public ResultVO<String> applyTag(@RequestParam("webId") Integer webId, @RequestParam("tag") String tagName) {
        String currentUsername = getCurrentUsername();
        String tag = tagService.applyTag(currentUsername, webId, tagName);
        return ResultCreator.okResult(tag);
    }

    /**
     * Get tags by the ID of the bookmarked website data.
     * <p>
     * Get all tags if the parameter {@code webId} is missing.
     * </p>
     *
     * @param webId    ID of the bookmarked website data
     * @param pageInfo pagination information
     * @return paginated tags
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link TagService#getTags(Integer, PageInfoDTO)}
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link ResultCode#NO_RESULTS_FOUND}
     *                                                                  if the bookmarked website data is NOT
     *                                                                  associated with any tags
     */
    @GetMapping
    public ResultVO<List<String>> getTags(@RequestParam(value = "webId", required = false) Integer webId,
                                          @PageInfo(paramName = PageInfoParam.CURRENT_PAGE, size = 100)
                                                  PageInfoDTO pageInfo) {
        List<String> tags = tagService.getTags(webId, pageInfo);
        return ResultCreator.okResult(tags);
    }

    /**
     * Get a tag of a bookmark, or return empty string if the bookmark has no tags
     *
     * @param webId ID of the bookmarked website data
     * @return a tag of the bookmark, or empty string if the bookmark has no tags
     */
    @GetMapping("/one")
    public ResultVO<String> getTag(@RequestParam(value = "webId") Integer webId) {
        String tag = tagService.getTagOrReturnEmpty(webId);
        return ResultCreator.okResult(tag);
    }

    /**
     * Search bookmarks by a certain tag.
     * <p>
     * If some bookmarks is not public and the user currently logged in
     * is not the owner, then those bookmarks will not be shown.
     * </p>
     *
     * @param tagName  name of the tag to search for
     * @param pageInfo pagination information
     * @return paginated bookmarks associated with the chosen tag
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link TagService#getBookmarksByUsernameAndTag(String,
     *                                                                  String, PageInfoDTO)}
     *                                                                  will throw an exception
     *                                                                  with the result code of {@link ResultCode#NO_RESULTS_FOUND}
     *                                                                  if no results found
     */
    @GetMapping("/search")
    public ResultVO<List<WebsiteDTO>> getBookmarksByTagName(@RequestParam("tagName") String tagName,
                                                            @PageInfo(paramName = PageInfoParam.CURRENT_PAGE, size = 10)
                                                                    PageInfoDTO pageInfo) {
        String currentUsername = getCurrentUsername();
        List<WebsiteDTO> bookmarks =
                tagService.getBookmarksByUsernameAndTag(currentUsername, tagName, pageInfo);
        return ResultCreator.okResult(bookmarks);
    }

    /**
     * Search bookmarks by a certain tag and get total pages.
     * <p>
     * If some bookmarks is not public and the user currently logged in
     * is not the owner, then those bookmarks will not be shown.
     * </p>
     *
     * @param tagName  name of the tag to search for
     * @param pageInfo pagination information
     * @return paginated bookmarks associated with the chosen tag and total pages
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link TagService#getBookmarksByUsernameAndTag(String,
     *                                                                  String, PageInfoDTO)}
     *                                                                  will throw an exception
     *                                                                  with the result code of {@link ResultCode#NO_RESULTS_FOUND}
     *                                                                  if no results found
     */
    @GetMapping("/search/{tagName}")
    public SearchByTagResultVO searchBookmarksByTagName(@PathVariable("tagName") String tagName,
                                                        @PageInfo(paramName = PageInfoParam.CURRENT_PAGE, size = 10)
                                                                PageInfoDTO pageInfo) {
        String currentUsername = getCurrentUsername();
        return tagService.getSearchResultsByUsernameAndTag(currentUsername, tagName, pageInfo);
    }

    /**
     * Get popular tags
     *
     * @param pageInfo pagination information
     * @return a list of paginated popular tags
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link TagService#getPopularTags(PageInfoDTO)}
     *                                                                  will throw an exception
     *                                                                  with the result code of {@link ResultCode#NO_RESULTS_FOUND}
     *                                                                  if no results found
     */
    @GetMapping("/popular")
    public ResultVO<List<PopularTagDTO>> getPopularTags(@PageInfo(paramName = PageInfoParam.CURRENT_PAGE, size = 100)
                                                                PageInfoDTO pageInfo) {
        List<PopularTagDTO> popularTags = tagService.getPopularTags(pageInfo);
        return ResultCreator.okResult(popularTags);
    }

    /**
     * Delete a tag
     *
     * @param webId   ID of the bookmarked website data that the tag applied to
     * @param tagName name of the tag to be deleted
     * @return {@link ResultCreator#okResult()} if success.
     * {@link ResultCreator#defaultFailResult()} if failure or the tag does not exist.
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link TagService#deleteTag(String, Integer,
     *                                                                  String)}
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link ResultCode#WEBSITE_DATA_NOT_EXISTS} if
     *                                                                  the bookmarked website data does not exist or
     *                                                                  the {@code webId} is null.
     *                                                                  <p>
     *                                                                  If the user has no permission to delete the
     *                                                                  tag of this bookmark, the result code will be
     *                                                                  {@link ResultCode#PERMISSION_DENIED}
     *                                                                  </p>
     */
    @DeleteMapping
    public ResultVO<ResultCode> deleteTag(@RequestParam("webId") Integer webId,
                                          @RequestParam("tagName") String tagName) {
        String currentUsername = getCurrentUsername();
        boolean success = tagService.deleteTag(currentUsername, webId, tagName);
        return success ? ResultCreator.okResult() : ResultCreator.defaultFailResult();
    }

    private String getCurrentUsername() {
        return StpUtil.getLoginIdAsString();
    }
}