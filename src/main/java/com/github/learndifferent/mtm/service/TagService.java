package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.PopularTagDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
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
     * This will delete the tag (the key is "tag:a") of the bookmarked site stored in the cache
     * if no exception is thrown.
     * </p>
     *
     * @param username username of the user who wants to apply the tag
     * @param webId    ID of the bookmarked website data that the user wants to apply the tag to
     * @param tag      the tag to apply
     * @return True if success
     * @throws com.github.learndifferent.mtm.exception.ServiceException This method is annotated with
     *                                                                  {@link com.github.learndifferent.mtm.annotation.validation.website.permission.ModifyWebsitePermissionCheck
     *                                                                  ModifyWebsitePermissionCheck} annotation, so it
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#WEBSITE_DATA_NOT_EXISTS
     *                                                                  WEBSITE_DATA_NOT_EXISTS} if the bookmarked
     *                                                                  website data does not exist or the {@code
     *                                                                  webId} is null.
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
    boolean applyTag(String username, Integer webId, String tag);

    /**
     * Get tags by Username and Web ID.
     * <p>
     * Get all tags if the ID is null and store the result in cache for 10 seconds by
     * {@link com.github.learndifferent.mtm.service.impl.TagServiceImpl#getTags(Integer, int, int)
     * a method in TagServiceImpl}.
     * </p>
     *
     * @param username username of the user who request the tags
     * @param webId    ID of the bookmarked website data
     * @param pageInfo pagination information
     * @return tags
     * @throws com.github.learndifferent.mtm.exception.ServiceException an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED
     *                                                                  PERMISSION_DENIED} will be thrown if the
     *                                                                  website data is private and the user is not the
     *                                                                  owner of the bookmarked website data.
     *                                                                  <p>
     *                                                                  If the bookmarked website data is NOT
     *                                                                  associated with any tags, the result code will
     *                                                                  be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#NO_RESULTS_FOUND
     *                                                                  NO_RESULTS_FOUND}.
     *                                                                  </p>
     */
    List<String> getTags(String username, Integer webId, PageInfoDTO pageInfo);

    /**
     * Get a tag of a bookmarked site, or return empty string if the user can't get the tag.
     * <p>
     * The result will be stored in the cache forever as the tag (the key is "tag:a") of the bookmarked site
     * by {@link com.github.learndifferent.mtm.service.impl.TagServiceImpl#getTagOrReturnEmptyCaching(Integer)
     * a method in TagServiceImpl} if the user can get the tag.
     * </p>
     *
     * @param username username of the user who is requesting the first tag
     * @param webId    ID of the bookmarked website data
     * @return the first tag, or return empty string if the user can't get the tag
     */
    String getTagOrReturnEmpty(String username, Integer webId);

    /**
     * Search bookmarks by a certain tag.
     * <p>
     * If some bookmarks is not public and the user who sent the request
     * is not the owner, then those bookmarks will not be shown.
     * </p>
     *
     * @param username username of the user who sent the request
     * @param tagName  name of the tag to search for
     * @param pageInfo a PageInfoDTO object that contains the page number and page size
     * @return paginated bookmarks associated with the chosen tag
     * @throws com.github.learndifferent.mtm.exception.ServiceException This will throw an exception
     *                                                                  with the result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#NO_RESULTS_FOUND
     *                                                                  NO_RESULTS_FOUND} if no results found
     */
    List<WebsiteDTO> getBookmarksByUsernameAndTag(String username, String tagName, PageInfoDTO pageInfo);

    /**
     * Delete a tag.
     * <p>
     * This will delete the tag (the key is "tag:a") of the bookmarked site stored in the cache
     * if no exception is thrown.
     * </p>
     *
     * @param username username of the user who is deleting the tag
     * @param webId    ID of the bookmarked website data that the tag applied to
     * @param tagName  name of the tag to be deleted
     * @return True if success. False if failure or the tag does not exist.
     * @throws com.github.learndifferent.mtm.exception.ServiceException This method is annotated with
     *                                                                  {@link com.github.learndifferent.mtm.annotation.validation.website.permission.ModifyWebsitePermissionCheck
     *                                                                  ModifyWebsitePermissionCheck} annotation, so it
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#WEBSITE_DATA_NOT_EXISTS
     *                                                                  WEBSITE_DATA_NOT_EXISTS} if the bookmarked
     *                                                                  website data does not exist or the {@code
     *                                                                  webId} is null.
     *                                                                  <p>
     *                                                                  And if the user has no permission to delete the
     *                                                                  tag of this bookmark, the result code will be
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED
     *                                                                  PERMISSION_DENIED}
     *                                                                  </p>
     */
    boolean deleteTag(String username, Integer webId, String tagName);

    /**
     * Get popular tags.
     * <p>
     * The result will be cached for 10 seconds if no exception is thrown.
     * </p>
     *
     * @param pageInfo pagination information
     * @return a list of paginated popular tags
     * @throws com.github.learndifferent.mtm.exception.ServiceException This will throw an exception
     *                                                                  with the result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#NO_RESULTS_FOUND
     *                                                                  NO_RESULTS_FOUND} if no results found
     */
    List<PopularTagDTO> getPopularTags(PageInfoDTO pageInfo);
}
