package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.constant.enums.HomeTimeline;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.BookmarksAndTotalPagesDTO;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.WebMoreInfoDTO;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.dto.WebsiteWithPrivacyDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.query.WebDataFilterRequest;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.vo.BookmarkResultVO;
import com.github.learndifferent.mtm.vo.PopularBookmarksVO;
import com.github.learndifferent.mtm.vo.UserBookmarksVO;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * WebsiteService
 *
 * @author zhou
 * @date 2021/09/05
 */
public interface WebsiteService {

    /**
     * Filter public bookmarked websites
     *
     * @param filterRequest Filter Public Website Data Request
     * @return filtered bookmarked websites
     */
    List<WebsiteDTO> findPublicWebDataByFilter(WebDataFilterRequest filterRequest);

    /**
     * Count number of the user's bookmarks
     *
     * @param userName       username of the user
     * @param includePrivate true if including the private bookmarks
     * @return number of the user's bookmarks
     */
    int countUserPost(String userName, boolean includePrivate);

    /**
     * Find the bookmark by ID
     *
     * @param webId ID of the bookmarked website data
     * @return {@link WebsiteDTO}
     */
    WebsiteDTO findWebsiteDataById(int webId);

    /**
     * Find the bookmark with privacy settings by ID
     *
     * @param webId ID of the bookmarked website data
     * @return {@link WebsiteWithPrivacyDTO}
     */
    WebsiteWithPrivacyDTO findWebsiteDataWithPrivacyById(int webId);

    /**
     * Complete the website data and add it to bookmarks
     *
     * @param webWithNoIdentity Website data that has no web ID, username and creation time,
     *                          which only contains title, url, image and description
     * @param userName          Username of the user who is bookmarking
     * @param isPublic          True if this is a public bookmark
     * @return true if success
     * @throws ServiceException This implementation method is annotated with
     *                          {@link com.github.learndifferent.mtm.annotation.validation.website.bookmarked.BookmarkCheck
     *                          BookmarkCheck} annotation
     *                          and {@link com.github.learndifferent.mtm.annotation.modify.webdata.WebsiteDataClean
     *                          WebsiteDataClean} annotation, which will throw exceptions with
     *                          the result code will of {@link ResultCode#ALREADY_SAVED},
     *                          {@link ResultCode#PERMISSION_DENIED} and {@link ResultCode#URL_MALFORMED}
     *                          if something goes wrong.
     */
    boolean bookmarkWithExistingData(WebWithNoIdentityDTO webWithNoIdentity, String userName, boolean isPublic);

    /**
     * Bookmark a new web page
     *
     * @param url      URL of the web page to bookmark
     * @param username Username of the user who is bookmarking
     * @param isPublic True or null if this is a public bookmark
     * @param beInEs   True or null if the data should be added to Elasticsearch
     * @return {@link ResultVO}<{@link BookmarkResultVO}> The result of bookmarking a new web page
     * @throws ServiceException Exception will be thrown with the result code of {@link ResultCode#URL_MALFORMED},
     *                          {@link ResultCode#URL_ACCESS_DENIED} and {@link ResultCode#CONNECTION_ERROR}
     *                          when an error occurred during an IO operation
     */
    BookmarkResultVO bookmark(String url, String username, Boolean isPublic, Boolean beInEs);

    /**
     * Get bookmarked websites and total pages for the current user on the home page
     *
     * @param currentUsername   username of the user that is currently logged in
     * @param homeTimeline      how to displays the stream of bookmarks on the home page
     * @param requestedUsername username of the user whose data is being requested
     *                          <p>{@code requestedUsername} is not is required</p>
     * @param pageInfo          pagination info
     * @return {@link BookmarksAndTotalPagesDTO}
     */
    BookmarksAndTotalPagesDTO getHomeTimeline(String currentUsername,
                                              HomeTimeline homeTimeline,
                                              String requestedUsername,
                                              PageInfoDTO pageInfo);

    /**
     * Get popular bookmarks and total pages
     *
     * @param pageInfo pagination information
     * @return popular bookmarks and total pages
     */
    PopularBookmarksVO getPopularBookmarksAndTotalPages(PageInfoDTO pageInfo);

    /**
     * Get all public bookmarks, and if {@code includePrivate} is true, then include all private bookmarks too.
     *
     * @param username       username
     * @param from           from
     * @param size           size
     * @param includePrivate true if include private website data
     * @return A list of {@link WebsiteWithPrivacyDTO}
     */
    List<WebsiteWithPrivacyDTO> getBookmarksByUser(String username, Integer from, Integer size, boolean includePrivate);

    /**
     * Get paginated public bookmarks of a user
     *
     * @param username username of the user whose public bookmarks is being requested
     * @param pageInfo pagination info
     * @return {@link UserBookmarksVO}
     */
    UserBookmarksVO getUserPublicBookmarks(String username, PageInfoDTO pageInfo);

    /**
     * Find a bookmarked website by URL
     *
     * @param url URL
     * @return A list of {@link WebsiteDTO}
     */
    List<WebsiteDTO> findWebsitesDataByUrl(String url);

    /**
     * Delete a bookmarked website and its associated data
     *
     * @param webId    ID of the bookmarked website data
     * @param userName username of the user who is deleting the bookmark
     * @return true if success
     * @throws ServiceException {@link com.github.learndifferent.mtm.annotation.validation.website.permission.ModifyWebsitePermissionCheck
     *                          ModifyWebsitePermissionCheck} annotation will check the permissions and throw
     *                          an exception with the result code of {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}
     *                          if there is no permissions
     */
    boolean deleteBookmark(Integer webId, String userName);

    /**
     * Make the bookmarked website private if it's public
     * and make it public if it's private.
     *
     * @param webId    ID of the bookmarked website data
     * @param userName name of user who trying to change the privacy settings
     * @return success or failure
     * @throws ServiceException If the website data does not exist, the result code will be
     *                          {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}.
     *                          And {@link com.github.learndifferent.mtm.annotation.validation.website.permission.ModifyWebsitePermissionCheck
     *                          ModifyWebsitePermissionCheck} annotation
     *                          will throw exception with {@link ResultCode#PERMISSION_DENIED}
     *                          if the user has no permission to change the website privacy settings.
     */
    boolean changePrivacySettings(Integer webId, String userName);

    /**
     * Get a bookmark
     *
     * @param webId    ID of the bookmarked website data
     * @param userName username of the user
     * @return bookmark
     * @throws ServiceException If the user has no permission to get the bookmark,
     *                          or the bookmark doesn't exist, a {@link ServiceException}
     *                          will be thrown with the result code of
     *                          {@link ResultCode#PERMISSION_DENIED}
     *                          or {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}
     */
    WebsiteDTO getBookmark(int webId, String userName);

    /**
     * Get additional information of a bookmarked website
     *
     * @param webId ID of the bookmarked website data
     * @return additional information of the bookmarked website
     */
    WebMoreInfoDTO getAdditionalInfo(int webId);

    /**
     * Export requested user's bookmarks to a HTML file.
     * If the requested user is not current user, only the public bookmarks
     * will be exported.
     *
     * @param username        username of the requested user
     * @param currentUsername username of the user that is currently logged in
     * @param response        response
     * @throws ServiceException Connection exception with the result code of {@link ResultCode#CONNECTION_ERROR}
     */
    void exportWebsDataByUserToHtmlFile(String username,
                                        String currentUsername,
                                        HttpServletResponse response);

    /**
     * Import website data from html file and return ResultVO as result.
     *
     * @param htmlFile html file
     * @param username username
     * @return {@link ResultVO}<{@link String}>
     */
    ResultVO<String> importWebsDataFromHtmlFile(MultipartFile htmlFile, String username);
}
