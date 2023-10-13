package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.annotation.validation.website.permission.ModifyBookmarkPermissionCheck;
import com.github.learndifferent.mtm.constant.enums.AccessPrivilege;
import com.github.learndifferent.mtm.constant.enums.AddDataMode;
import com.github.learndifferent.mtm.constant.enums.HomeTimeline;
import com.github.learndifferent.mtm.constant.enums.Order;
import com.github.learndifferent.mtm.constant.enums.OrderField;
import com.github.learndifferent.mtm.constant.enums.Privacy;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.query.BasicWebDataRequest;
import com.github.learndifferent.mtm.query.UsernamesRequest;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.vo.BookmarkVO;
import com.github.learndifferent.mtm.vo.BookmarkingResultVO;
import com.github.learndifferent.mtm.vo.BookmarksAndTotalPagesVO;
import com.github.learndifferent.mtm.vo.PopularBookmarksVO;
import com.github.learndifferent.mtm.vo.VisitedBookmarkVO;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Bookmark Service
 *
 * @author zhou
 * @date 2021/09/05
 */
public interface BookmarkService {

    /**
     * Filter public bookmarked sites
     * <p>
     * {@code fromTimestamp} and {@code toTimestamp} is used to query a specific range of time.
     * <li>It will not query a range of time if both of them are null.</li>
     * <li>It will set the null value to the current time if one of them is null</li>
     * <li>It will swap the two values if necessary.</li>
     * </p>
     *
     * @param usernames     request body that contains usernames
     *                      <p>null or empty if select all users</p>
     * @param load          amount of data to load
     * @param fromTimestamp filter by time
     * @param toTimestamp   filter by time
     * @param orderField    order by the field
     * @param order         {@link Order#ASC} if ascending order, {@link Order#DESC} if descending order
     * @return filtered bookmarked websites
     */
    List<BookmarkVO> filterPublicBookmarks(UsernamesRequest usernames,
                                           Integer load,
                                           String fromTimestamp,
                                           String toTimestamp,
                                           OrderField orderField,
                                           Order order);

    /**
     * Bookmark a new web page
     *
     * @param url           URL of the web page to bookmark
     * @param currentUserId User ID of the user who is bookmarking
     * @param privacy       {@link Privacy#PUBLIC} if this is a public bookmark and
     *                      {@link Privacy#PRIVATE} if this is private
     * @param mode          {@link AddDataMode#ADD_TO_DATABASE} if the data should only be added to the database.
     *                      <p>
     *                      {@link AddDataMode#ADD_TO_DATABASE_AND_ELASTICSEARCH} if the data should
     *                      be added to the database and ElasticSearch
     *                      </p>
     *                      <p>
     *                      Note that this value will be ignored if this is a private bookmark
     *                      because only public data can be added to Elasticsearch
     *                      </p>
     * @return {@link ResultVO}<{@link BookmarkingResultVO}> The result of bookmarking a new web page
     * @throws ServiceException Exception will be thrown with the result code of {@link ResultCode#URL_MALFORMED},
     *                          {@link ResultCode#URL_ACCESS_DENIED} and {@link ResultCode#CONNECTION_ERROR}
     *                          when an error occurred during an IO operation
     */
    BookmarkingResultVO bookmark(String url, long currentUserId, Privacy privacy, AddDataMode mode);

    /**
     * Add a website to the bookmarks
     *
     * @param data   Basic website data that contains title, URL, image and description
     * @param userId ID of the user who is bookmarking
     * @return true if success
     * @throws ServiceException throw exceptions with the result code of {@link ResultCode#ALREADY_SAVED},
     *                          {@link ResultCode#PERMISSION_DENIED} and {@link ResultCode#URL_MALFORMED}
     *                          if something goes wrong.
     */
    boolean addToBookmark(BasicWebDataRequest data, long userId);

    /**
     * Get bookmarked websites and total pages for the current user on the home page
     *
     * @param currentUserId   user ID of the user that is currently logged in
     * @param homeTimeline    how to display the stream of bookmarks on the home page
     * @param requestedUserId user ID of the user whose data is being requested
     *                        <p>{@code requestedUsername} is not is required</p>
     * @param pageInfo        pagination info
     * @return {@link BookmarksAndTotalPagesVO}
     */
    BookmarksAndTotalPagesVO getHomeTimeline(long currentUserId,
                                             HomeTimeline homeTimeline,
                                             Long requestedUserId,
                                             PageInfoDTO pageInfo);

    /**
     * Get popular bookmarks and total pages
     *
     * @param pageInfo pagination information
     * @return popular bookmarks and total pages
     */
    PopularBookmarksVO getPopularBookmarksAndTotalPages(PageInfoDTO pageInfo);

    /**
     * Get paginated public bookmarks of a user
     * <p>
     * Include all private bookmarks if {@code shouldIncludePrivate} is true
     * </p>
     *
     * @param userId    user ID of the user whose bookmarks are being requested
     * @param pageInfo  pagination info
     * @param privilege {@link AccessPrivilege#LIMITED} if only public data can be accessed.
     *                  {@link AccessPrivilege#ALL} if public and private data can be accessed.
     * @return {@link BookmarksAndTotalPagesVO}
     */
    BookmarksAndTotalPagesVO getUserBookmarks(long userId, PageInfoDTO pageInfo, AccessPrivilege privilege);

    /**
     * Delete a bookmarked website and its associated data
     *
     * @param id     ID of the bookmark
     * @param userId user ID of the user who is deleting the bookmark
     * @return true if success
     * @throws ServiceException {@link com.github.learndifferent.mtm.annotation.validation.ModificationPermissionCheck
     *                          ModificationPermissionCheck} annotation will check the permissions and throw
     *                          an exception with the result code of {@link ResultCode#PERMISSION_DENIED}
     *                          if there is no permissions
     */
    boolean deleteBookmark(Long id, Long userId);

    /**
     * Make the bookmarked website private if it's public
     * and make it public if it's private.
     *
     * @param id       ID of the bookmark
     * @param userName name of user who trying to change the privacy settings
     * @return success or failure
     * @throws ServiceException If the bookmark does not exist, the result code will be
     *                          {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}.
     *                          And {@link ModifyBookmarkPermissionCheck
     *                          ModifyBookmarkPermissionCheck} annotation
     *                          will throw exception with {@link ResultCode#PERMISSION_DENIED}
     *                          if the user has no permission to change the bookmark privacy settings.
     */
    boolean changePrivacySettings(Integer id, String userName);

    /**
     * Get a bookmark
     *
     * @param id     ID of the bookmark
     * @param userId User ID of the user
     * @return bookmark
     * @throws ServiceException If the user has no permission to get the bookmark,
     *                          or the bookmark doesn't exist, a {@link ServiceException}
     *                          will be thrown with the result code of
     *                          {@link ResultCode#PERMISSION_DENIED}
     *                          or {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}
     */
    BookmarkVO getBookmark(int id, long userId);

    /**
     * Check if the bookmark exists and if the user has permission to access it
     *
     * @param id       ID of the bookmark
     * @param username username of the user who is trying to access the bookmark
     * @throws ServiceException throw an exception with the result code of {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}
     *                          if the bookmark dose not exist, or {@link ResultCode#PERMISSION_DENIED}
     *                          if the user has no permission
     */
    void checkBookmarkExistsAndUserPermission(int id, String username);

    /**
     * Export user's bookmarks to HTML file.
     * <p>
     * Export bookmarks belonging to the user that is currently logged in
     * if the {@code requestedUserId} is null.
     * </p>
     * <p>
     * Only the public bookmarks will be exported
     * if the user whose data is being exported is not
     * the user that is currently logged in.
     * </p>
     *
     * @param requestedUserId user ID of the user whose data is being exported
     * @param currentUserId   user ID of the user that is currently logged in
     * @param response        response
     * @throws ServiceException Connection exception with the result code of {@link ResultCode#CONNECTION_ERROR}
     */
    void exportBookmarksToHtmlFile(Long requestedUserId, long currentUserId, HttpServletResponse response);

    /**
     * Import bookmarks from HTML file
     *
     * @param htmlFile a file that contains the bookmarks in HTML format
     * @param userId   ID of the user who is importing the bookmarks
     * @return the message of the result
     * @throws ServiceException Throw an exception with the result code of {@link ResultCode#HTML_FILE_NO_BOOKMARKS}
     *                          if it's not a valid HTML file that contains bookmarks
     */
    String importBookmarksFromHtmlFile(MultipartFile htmlFile, long userId);

    /**
     * Get visited bookmarks from database
     * <li>
     * The data in database may not be newest.
     * Use {@link ViewCounterService#updateViewsAndReturnFailKeys()} to import data into database
     * if the user wants the newest data).
     * </li>
     * <li>
     * The result will be stored in cache.
     * </li>
     * <li>
     * The cache will be deleted when the data in database
     * is updated by {@link ViewCounterService#updateViewsAndReturnFailKeys()}.
     * </li>
     *
     * @param pageInfo pagination information
     * @return visited bookmarks
     */
    List<VisitedBookmarkVO> getVisitedBookmarks(PageInfoDTO pageInfo);
}