package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.general.idempotency.IdempotencyCheck;
import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.constant.consist.ConstraintConstant;
import com.github.learndifferent.mtm.constant.consist.ErrorInfoConstant;
import com.github.learndifferent.mtm.constant.enums.PageInfoParam;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.PopularTagDTO;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.TagService;
import com.github.learndifferent.mtm.utils.LoginUtils;
import com.github.learndifferent.mtm.vo.BookmarkVO;
import com.github.learndifferent.mtm.vo.SearchByTagResultVO;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
@Validated
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * Apply a tag
     *
     * @param bookmarkId ID of the bookmark that the user currently logged in wants to apply the tag to
     * @param tagName    the tag to apply
     * @return Return the tag if applied successfully, or empty string if failed to apply
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link TagService#applyTag(String, Integer,
     *                                                                  String)}
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link ResultCode#WEBSITE_DATA_NOT_EXISTS} if
     *                                                                  the bookmark does not exist or the {@code
     *                                                                  bookmarkId} is null.
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
    @IdempotencyCheck
    public ResultVO<String> applyTag(@RequestParam("bookmarkId")
                                     @Positive(message = ErrorInfoConstant.BOOKMARK_NOT_FOUND)
                                             Integer bookmarkId,
                                     @RequestParam("tag")
                                     @NotBlank(message = ErrorInfoConstant.TAG_EMPTY)
                                     @Length(min = ConstraintConstant.TAG_MIN_LENGTH,
                                             max = ConstraintConstant.TAG_MAX_LENGTH,
                                             message = ErrorInfoConstant.TAG_LENGTH)
                                             String tagName) {
        String currentUsername = getCurrentUsername();
        String tag = tagService.applyTag(currentUsername, bookmarkId, tagName);
        return ResultCreator.okResult(tag);
    }

    /**
     * Get tags by the ID of the bookmark
     * <p>
     * Get all tags if the parameter {@code bookmarkId} is missing.
     * </p>
     *
     * @param bookmarkId ID of the bookmark
     * @param pageInfo   pagination information
     * @return paginated tags
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link TagService#getTags(Integer, PageInfoDTO)}
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link ResultCode#NO_RESULTS_FOUND}
     *                                                                  if the bookmarked website data is NOT
     *                                                                  associated with any tags
     */
    @GetMapping
    public ResultVO<List<String>> getTags(@RequestParam(value = "bookmarkId", required = false)
                                          @Positive(message = ErrorInfoConstant.BOOKMARK_NOT_FOUND)
                                                  Integer bookmarkId,
                                          @PageInfo(paramName = PageInfoParam.CURRENT_PAGE, size = 100)
                                                  PageInfoDTO pageInfo) {
        List<String> tags = tagService.getTags(bookmarkId, pageInfo);
        return ResultCreator.okResult(tags);
    }

    /**
     * Get a tag of a bookmark, or return empty string if the bookmark has no tags
     *
     * @param bookmarkId ID of the bookmark
     * @return a tag of the bookmark, or empty string if the bookmark has no tags
     */
    @GetMapping("/one")
    public ResultVO<String> getTag(@RequestParam(value = "bookmarkId") Integer bookmarkId) {
        String tag = tagService.getTagOrReturnEmpty(bookmarkId);
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
    public List<BookmarkVO> getBookmarksByTagName(@RequestParam("tagName")
                                                  @NotBlank(message = ErrorInfoConstant.TAG_EMPTY)
                                                  @Length(min = ConstraintConstant.TAG_MIN_LENGTH,
                                                          max = ConstraintConstant.TAG_MAX_LENGTH,
                                                          message = ErrorInfoConstant.TAG_LENGTH)
                                                          String tagName,
                                                  @PageInfo(paramName = PageInfoParam.CURRENT_PAGE, size = 10)
                                                          PageInfoDTO pageInfo) {
        String currentUsername = getCurrentUsername();
        return tagService.getBookmarksByUsernameAndTag(currentUsername, tagName, pageInfo);
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
     * Get popular tags.
     *
     * @param pageInfo pagination information
     * @return a list of paginated tags, which are the tags of public bookmarks or the tags of
     * bookmarks that owns by the current user, that appear more than once.
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link TagService#getPopularTags(long,
     *                                                                  PageInfoDTO)} will throw an exception
     *                                                                  with the result code of {@link ResultCode#NO_RESULTS_FOUND}
     *                                                                  if no results found
     */
    @GetMapping("/popular")
    public ResultVO<List<PopularTagDTO>> getPopularTags(@PageInfo(paramName = PageInfoParam.CURRENT_PAGE, size = 100)
                                                                PageInfoDTO pageInfo) {
        long currentUserId = LoginUtils.getCurrentUserId();
        List<PopularTagDTO> popularTags = tagService.getPopularTags(currentUserId, pageInfo);
        return ResultCreator.okResult(popularTags);
    }

    /**
     * Delete a tag
     *
     * @param bookmarkId ID of the bookmarked website data that the tag applied to
     * @param tagName    name of the tag to be deleted
     * @return {@link ResultCreator#okResult()} if success.
     * {@link ResultCreator#defaultFailResult()} if failure or the tag does not exist.
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link TagService#deleteTag(String, Integer,
     *                                                                  String)}
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link ResultCode#WEBSITE_DATA_NOT_EXISTS} if
     *                                                                  the bookmarked website data does not exist or
     *                                                                  the {@code bookmarkId} is null.
     *                                                                  <p>
     *                                                                  If the user has no permission to delete the
     *                                                                  tag of this bookmark, the result code will be
     *                                                                  {@link ResultCode#PERMISSION_DENIED}
     *                                                                  </p>
     */
    @DeleteMapping
    @IdempotencyCheck
    public ResultVO<ResultCode> deleteTag(@RequestParam("bookmarkId") Integer bookmarkId,
                                          @RequestParam("tagName") String tagName) {
        String currentUsername = getCurrentUsername();
        boolean success = tagService.deleteTag(currentUsername, bookmarkId, tagName);
        return success ? ResultCreator.okResult() : ResultCreator.defaultFailResult();
    }

    private String getCurrentUsername() {
        return LoginUtils.getCurrentUsername();
    }
}