package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.ShowPattern;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.SaveWebDataResultDTO;
import com.github.learndifferent.mtm.dto.UserPublicWebInfoDTO;
import com.github.learndifferent.mtm.dto.WebDataAndTotalPagesDTO;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.dto.WebWithPrivacyCommentCountDTO;
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
     * @return Filtered Paginated Website Data
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
     * @param webId ID
     * @return {@link WebsiteDTO}
     */
    WebsiteDTO findWebsiteDataById(int webId);

    /**
     * Find website data with privacy settings by ID
     *
     * @param webId ID
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
     * Get website data and total pages
     *
     * @param currentUsername   username of the user that is currently logged in
     * @param showPattern       pattern of the website data to be shown
     * @param requestedUsername username of the user whose data is being requested
     *                          <p>{@code requestedUsername} is not is required</p>
     * @param pageInfo          pagination info
     * @return {@link WebDataAndTotalPagesDTO}
     */
    WebDataAndTotalPagesDTO getWebDataInfo(String currentUsername,
                                           ShowPattern showPattern,
                                           String requestedUsername,
                                           PageInfoDTO pageInfo);

    /**
     * Get all public website data and the count of their comments, of user the with name of {@code username}.
     * If {@code includePrivate} is true, then include all private website data too.
     *
     * @param username       username
     * @param from           from
     * @param size           size
     * @param includePrivate true if include private website data
     * @return A list of {@link WebWithPrivacyCommentCountDTO}
     */
    List<WebWithPrivacyCommentCountDTO> getWebsDataAndCommentCountByUser(String username,
                                                                         Integer from,
                                                                         Integer size,
                                                                         boolean includePrivate);

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
     * Delete website data by ID
     *
     * @param webId    ID
     * @param userName username
     * @return true if success
     * @throws ServiceException {@link com.github.learndifferent.mtm.annotation.validation.website.permission.ModifyWebsitePermissionCheck
     *                          ModifyWebsitePermissionCheck} annotation will check the permissions and throw
     *                          an exception with the result code of {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}
     *                          if there is no permissions
     */
    boolean delWebsiteDataById(int webId, String userName);

    /**
     * Change the saved website privacy settings.
     * If the website is public, then make it private.
     * If the website is private, then make it public.
     *
     * @param webId    web id
     * @param userName name of user who trying to change the privacy settings
     * @return success or failure
     * @throws ServiceException If the website data does not exists, the result code will be
     *                          {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}.
     *                          And {@link com.github.learndifferent.mtm.annotation.validation.website.permission.ModifyWebsitePermissionCheck}
     *                          will throw exception with {@link ResultCode#PERMISSION_DENIED}
     *                          if the user has no permission to change the website privacy settings.
     */
    boolean changeWebPrivacySettings(int webId, String userName);

    /**
     * Get website data by {@code webId} and {@code userName}
     *
     * @param webId    web id
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
