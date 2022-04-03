package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.constant.enums.HomeTimeline;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.SaveWebDataResultDTO;
import com.github.learndifferent.mtm.dto.UserPublicWebInfoDTO;
import com.github.learndifferent.mtm.dto.WebDataAndTotalPagesDTO;
import com.github.learndifferent.mtm.dto.WebMoreInfoDTO;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.dto.WebsiteWithPrivacyDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.query.SaveNewWebDataRequest;
import com.github.learndifferent.mtm.query.WebDataFilterRequest;
import com.github.learndifferent.mtm.response.ResultVO;
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
     * Filter public website data by {@link WebDataFilterRequest}
     *
     * @param filterRequest Filter Public Website Data Request
     * @return Filtered Website Data
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
     * Find website data by ID
     *
     * @param webId ID of the bookmarked website data
     * @return {@link WebsiteDTO}
     */
    WebsiteDTO findWebsiteDataById(int webId);

    /**
     * Find website data with privacy settings by ID
     *
     * @param webId ID of the bookmarked website data
     * @return {@link WebsiteWithPrivacyDTO}
     */
    WebsiteWithPrivacyDTO findWebsiteDataWithPrivacyById(int webId);

    /**
     * Complete the website data and save it to database.
     * This implementation method is annotated with
     * {@link com.github.learndifferent.mtm.annotation.validation.website.marked.MarkCheck MarkCheck}
     * and {@link com.github.learndifferent.mtm.annotation.modify.webdata.WebsiteDataClean WebsiteDataClean}.
     *
     * @param webWithNoIdentity Website data that has no web ID, username and creation time,
     *                          which only contains title, url, image and description
     * @param userName          Username
     * @param isPublic          True if this is a public bookmark
     * @return true if success
     * @throws ServiceException The result code will be {@link ResultCode#ALREADY_MARKED},
     *                          {@link ResultCode#PERMISSION_DENIED} and {@link ResultCode#URL_MALFORMED}
     *                          if something goes wrong
     */
    boolean saveWebsiteData(WebWithNoIdentityDTO webWithNoIdentity, String userName, boolean isPublic);

    /**
     * Save New Website Data
     *
     * @param newWebsiteData URL, username, a boolean value named {@code isPublic} related to privacy settings
     *                       and a boolean value named {@code syncToElasticsearch} related to whether the data
     *                       will be synchronized to Elasticsearch or not
     * @return {@link SaveWebDataResultDTO} The result of saving website data
     */
    SaveWebDataResultDTO saveNewWebsiteData(SaveNewWebDataRequest newWebsiteData);

    /**
     * Get bookmarks (website data) and total pages for the current user on the home page
     *
     * @param currentUsername   username of the user that is currently logged in
     * @param homeTimeline      how to displays the stream of bookmarks on the home page
     * @param requestedUsername username of the user whose data is being requested
     *                          <p>{@code requestedUsername} is not is required</p>
     * @param pageInfo          pagination info
     * @return {@link WebDataAndTotalPagesDTO}
     */
    WebDataAndTotalPagesDTO getHomeTimeline(String currentUsername,
                                            HomeTimeline homeTimeline,
                                            String requestedUsername,
                                            PageInfoDTO pageInfo);

    /**
     * Get all public bookmarks.
     * If {@code includePrivate} is true, then include all private bookmarks too.
     *
     * @param username       username
     * @param from           from
     * @param size           size
     * @param includePrivate true if include private website data
     * @return A list of {@link WebsiteWithPrivacyDTO}
     */
    List<WebsiteWithPrivacyDTO> getBookmarksByUser(String username, Integer from, Integer size, boolean includePrivate);

    /**
     * Get paginated website data and total pages by username
     *
     * @param username username
     * @param pageInfo pagination info
     * @return {@link UserPublicWebInfoDTO}
     */
    UserPublicWebInfoDTO getUserPublicWebInfoDTO(String username, PageInfoDTO pageInfo);

    /**
     * Find website data by URL
     *
     * @param url URL
     * @return A list of {@link WebsiteDTO}
     */
    List<WebsiteDTO> findWebsitesDataByUrl(String url);

    /**
     * Delete bookmarked website and associated data by ID
     *
     * @param webId    ID of the bookmarked website data
     * @param userName username
     * @return true if success
     * @throws ServiceException {@link com.github.learndifferent.mtm.annotation.validation.website.permission.ModifyWebsitePermissionCheck
     *                          ModifyWebsitePermissionCheck} annotation will check the permissions and throw
     *                          an exception with the result code of {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}
     *                          if there is no permissions
     */
    boolean delWebsiteDataById(int webId, String userName);

    /**
     * Make the bookmarked website private if it's public
     * and make it public if it's private.
     *
     * @param webId    ID of the bookmarked website data
     * @param userName name of user who trying to change the privacy settings
     * @return success or failure
     * @throws ServiceException If the website data does not exist, the result code will be
     *                          {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}.
     *                          And {@link com.github.learndifferent.mtm.annotation.validation.website.permission.ModifyWebsitePermissionCheck}
     *                          will throw exception with {@link ResultCode#PERMISSION_DENIED}
     *                          if the user has no permission to change the website privacy settings.
     */
    boolean changeWebPrivacySettings(int webId, String userName);

    /**
     * Get website data by {@code webId} and {@code userName}
     *
     * @param webId    ID of the bookmarked website data
     * @param userName username
     * @return {@link WebsiteDTO} website data
     * @throws ServiceException If the user has no permission to get the website data,
     *                          or the website data doesn't exist, a {@link ServiceException}
     *                          will be thrown with the result code of
     *                          {@link ResultCode#PERMISSION_DENIED}
     *                          or {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}
     */
    WebsiteDTO getWebsiteDataByIdAndUsername(int webId, String userName);

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
