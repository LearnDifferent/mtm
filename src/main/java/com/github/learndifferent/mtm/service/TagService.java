package com.github.learndifferent.mtm.service;

/**
 * Tag Service
 *
 * @author zhou
 * @date 2022/3/31
 */
public interface TagService {

    /**
     * Apply a tag
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
}
