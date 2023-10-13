package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.annotation.validation.ModificationPermissionCheck;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.PopularTagDTO;
import com.github.learndifferent.mtm.vo.BookmarkVO;
import com.github.learndifferent.mtm.vo.SearchByTagResultVO;
import java.util.List;

/**
 * Tag Service
 *
 * @author zhou
 * @date 2022/3/31
 */
public interface TagService {

    /**
     * Apply a tag.
     * <p>
     * This will also update the tag (prefix of the key is "tag:a") of the bookmarked site
     * stored in the cache if no exception is thrown and the result is not empty.
     * </p>
     *
     * @param username   username of the user who wants to apply the tag
     * @param bookmarkId ID of the bookmark that the user wants to apply the tag to
     * @param tagName    the tag to apply
     * @return Return the tag if applied successfully, or empty string if failed to apply
     * @throws com.github.learndifferent.mtm.exception.ServiceException This method is annotated with
     *                                                                  {@link ModificationPermissionCheck
     *                                                                  ModificationPermissionCheck} annotation, so
     *                                                                  it will throw an exception with the result code
     *                                                                  of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#WEBSITE_DATA_NOT_EXISTS
     *                                                                  WEBSITE_DATA_NOT_EXISTS} if the bookmarked
     *                                                                  website data does not exist or the {@code
     *                                                                  bookmarkId} is null.
     *                                                                  <p>
     *                                                                  And if the user has no permission to apply the
     *                                                                  tag to this bookmark, the result code will be
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED
     *                                                                  PERMISSION_DENIED}
     *                                                                  </p>
     *                                                                  <p>
     *                                                                  This method is also annotated with
     *                                                                  {@link com.github.learndifferent.mtm.annotation.validation.tag.TagCheck
     *                                                                  TagCheck} annotation, which will verify
     *                                                                  the length of a tag and throw an exception with
     *                                                                  the result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#TAG_TOO_LONG
     *                                                                  TAG_TOO_LONG} if the tag exceeds the maximum
     *                                                                  length.
     *                                                                  </p>
     *                                                                  <p>
     *                                                                  It will also throw an exception with the result
     *                                                                  code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#TAG_NOT_EXISTS
     *                                                                  TAG_NOT_EXISTS} if the tag does not exist, and
     *                                                                  with result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#TAG_EXISTS
     *                                                                  TAG_EXISTS} if the tag has already been
     *                                                                  applied.
     *                                                                  </p>
     */
    String applyTag(String username, Integer bookmarkId, String tagName);

    /**
     * Get tags by the ID of the bookmark
     * <p>
     * Get all tags if the ID is null and store the result in cache for 10 seconds.
     * </p>
     * <li>
     * Note that every user can get tags without permissions.
     * </li>
     *
     * @param bookmarkId ID of the bookmark
     * @param pageInfo   pagination information
     * @return tags
     * @throws com.github.learndifferent.mtm.exception.ServiceException This will throw an exception with the result
     *                                                                  code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#NO_RESULTS_FOUND}
     *                                                                  if the bookmarked website data is NOT
     *                                                                  associated with any tags
     */
    List<String> getTags(Integer bookmarkId, PageInfoDTO pageInfo);

    /**
     * Get a tag of the bookmark
     * <p>
     * The result will be stored in the cache
     * as the tag (prefix of the key is "tag:a") of the bookmarked site
     * </p>
     * <li>
     * Note that every user can get tags without permissions.
     * </li>
     *
     * @param bookmarkId ID of the bookmark
     * @return a tag, or return empty string if there is no tag
     */
    String getTagOrReturnEmpty(Integer bookmarkId);

    /**
     * Search bookmarks by a certain tag.
     * <p>
     * If some bookmarks is not public and the user who sent the request
     * is not the owner, then those bookmarks will not be shown.
     * </p>
     *
     * @param username username of the user who sent the request
     * @param tagName  name of the tag to search for
     * @param pageInfo pagination information
     * @return paginated bookmarks associated with the chosen tag
     * @throws com.github.learndifferent.mtm.exception.ServiceException This will throw an exception
     *                                                                  with the result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#NO_RESULTS_FOUND
     *                                                                  NO_RESULTS_FOUND} if no results found
     */
    List<BookmarkVO> getBookmarksByUsernameAndTag(String username, String tagName, PageInfoDTO pageInfo);

    /**
     * Search bookmarks by a certain tag and get total pages.
     * <p>
     * If some bookmarks is not public and the user who sent the request
     * is not the owner, then those bookmarks will not be shown.
     * </p>
     *
     * @param username username of the user who sent the request
     * @param tagName  name of the tag to search for
     * @param pageInfo pagination information
     * @return paginated bookmarks associated with the chosen tag and total pages
     * @throws com.github.learndifferent.mtm.exception.ServiceException This will throw an exception
     *                                                                  with the result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#NO_RESULTS_FOUND
     *                                                                  NO_RESULTS_FOUND} if no results found
     */
    SearchByTagResultVO getSearchResultsByUsernameAndTag(String username, String tagName, PageInfoDTO pageInfo);

    /**
     * Delete a tag.
     * <p>
     * This will delete the tag (prefix of the key is "tag:a") of the bookmark
     * stored in the cache if no exception is thrown.
     * </p>
     *
     * @param userId     User ID of the user who is deleting the tag
     * @param bookmarkId ID of the bookmark that the tag applied to
     * @param tagName    name of the tag to be deleted
     * @return True if success. False if failure or the tag does not exist.
     * @throws com.github.learndifferent.mtm.exception.ServiceException This method is annotated with
     *                                                                  {@link ModificationPermissionCheck
     *                                                                  ModificationPermissionCheck} annotation, so
     *                                                                  it
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#WEBSITE_DATA_NOT_EXISTS
     *                                                                  WEBSITE_DATA_NOT_EXISTS} if the bookmarked
     *                                                                  website data does not exist or the {@code
     *                                                                  bookmarkId} is null.
     *                                                                  <p>
     *                                                                  And if the user has no permission to delete the
     *                                                                  tag of this bookmark, the result code will be
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED
     *                                                                  PERMISSION_DENIED}
     *                                                                  </p>
     */
    boolean deleteTag(long userId, long bookmarkId, String tagName);

    /**
     * Get popular tags.
     * <p>
     * The result will be cached for 10 seconds if no exception is thrown.
     * </p>
     *
     * @param userId   user ID of the user who is searching for popular tags
     * @param pageInfo pagination information
     * @return a list of paginated tags, which are the tags of public bookmarks or the tags of
     * bookmarks that owns by the user who is searching for popular tags, that appear more than once.
     * @throws com.github.learndifferent.mtm.exception.ServiceException This will throw an exception
     *                                                                  with the result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#NO_RESULTS_FOUND
     *                                                                  NO_RESULTS_FOUND} if no results found
     */
    List<PopularTagDTO> getPopularTags(long userId, PageInfoDTO pageInfo);
}
