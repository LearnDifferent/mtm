package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.constant.enums.PageInfoParam;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.WebMoreInfoDTO;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.query.ExistingDataRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.vo.BookmarkResultVO;
import com.github.learndifferent.mtm.vo.UserBookmarksVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Bookmark Controller
 *
 * @author zhou
 * @date 2021/09/05
 */

@RestController
@RequestMapping("/bookmark")
public class BookmarkController {

    private final WebsiteService websiteService;

    @Autowired
    public BookmarkController(WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

    /**
     * Bookmark a new web page
     *
     * @param url      URL of the web page to bookmark
     * @param isPublic true or null if this is a public bookmark
     * @param beInEs   true or null if the data should be added to Elasticsearch
     * @return The result of bookmarking a new web page
     * @throws ServiceException Exception will be thrown with the result code of {@link ResultCode#URL_MALFORMED},
     *                          {@link ResultCode#URL_ACCESS_DENIED} and {@link ResultCode#CONNECTION_ERROR}
     *                          when an error occurred during an IO operation
     */
    @GetMapping
    public BookmarkResultVO bookmark(@RequestParam("url") String url,
                                     @RequestParam("isPublic") Boolean isPublic,
                                     @RequestParam("beInEs") Boolean beInEs) {
        String currentUsername = getCurrentUsername();
        return websiteService.bookmark(url, currentUsername, isPublic, beInEs);
    }

    /**
     * Bookmark a web page with existing data
     *
     * @param request Request body of existing public website data that
     *                has no web id, username and creation time,
     *                which only contains title, url, image and description.
     * @return {@link ResultCode#SUCCESS} if success. {@link ResultCode#FAILED} if failure.
     * @throws ServiceException {@link WebsiteService#bookmarkWithExistingData(WebWithNoIdentityDTO, String, boolean)}
     *                          will verify and throw exceptions if something goes wrong.
     *                          The Result Codes are: {@link ResultCode#ALREADY_SAVED}, {@link
     *                          ResultCode#PERMISSION_DENIED} and {@link ResultCode#URL_MALFORMED}
     */
    @PostMapping
    public ResultVO<ResultCode> bookmarkWithExistingData(@RequestBody ExistingDataRequest request) {
        WebWithNoIdentityDTO website = DozerUtils.convert(request, WebWithNoIdentityDTO.class);
        String currentUsername = getCurrentUsername();
        boolean success = websiteService.bookmarkWithExistingData(website, currentUsername, true);
        return success ? ResultCreator.okResult() : ResultCreator.failResult();
    }

    /**
     * Delete a bookmarked website
     *
     * @param webId ID of the bookmarked website data
     * @return {@link ResultCode#DELETE_SUCCESS} if success. {@link ResultCode#DELETE_FAILED}
     * @throws ServiceException {@link WebsiteService#deleteBookmark(Integer, String)} will throw
     *                          an exception if the user currently logged in does not have permission to delete,
     *                          the result code will be {@link ResultCode#PERMISSION_DENIED}
     */
    @DeleteMapping
    public ResultVO<ResultCode> deleteBookmark(@RequestParam("webId") Integer webId) {
        String currentUsername = getCurrentUsername();
        boolean success = websiteService.deleteBookmark(webId, currentUsername);
        return success ? ResultCreator.result(ResultCode.DELETE_SUCCESS)
                : ResultCreator.result(ResultCode.DELETE_FAILED);
    }

    /**
     * Make the bookmarked website private if it's public
     * and make it public if it's private.
     *
     * @param webId ID of the bookmarked website data
     * @return {@link ResultCode#SUCCESS} if success. {@link ResultCode#UPDATE_FAILED} if failure.
     * @throws ServiceException {@link WebsiteService#changePrivacySettings(Integer, String)}
     *                          will throw an exception with {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}
     *                          if the bookmark does not exist and with {@link ResultCode#PERMISSION_DENIED}
     *                          if the user currently logged in has no permission to change the website privacy settings
     */
    @GetMapping("/privacy")
    public ResultVO<ResultCode> changePrivacySettings(@RequestParam("webId") Integer webId) {
        String currentUsername = getCurrentUsername();
        boolean success = websiteService.changePrivacySettings(webId, currentUsername);
        return success ? ResultCreator.okResult() : ResultCreator.result(ResultCode.UPDATE_FAILED);
    }

    /**
     * Get a bookmark
     *
     * @param webId ID of the bookmarked website data
     * @return {@link ResultVO}<{@link WebsiteDTO the bookmark}>
     * @throws ServiceException If the user currently logged in has no permission to get the bookmark,
     *                          or the bookmark doesn't exist, a {@link ServiceException}
     *                          will be thrown with the result code of {@link ResultCode#PERMISSION_DENIED}
     *                          or {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}
     */
    @GetMapping("/get")
    public ResultVO<WebsiteDTO> getBookmark(@RequestParam("webId") Integer webId) {
        String currentUsername = getCurrentUsername();
        WebsiteDTO web = websiteService.getBookmark(webId, currentUsername);
        return ResultCreator.okResult(web);
    }

    /**
     * Get paginated public bookmarks of a user
     *
     * @param username username of the user whose public bookmarks is being requested
     * @param pageInfo pagination info
     * @return {@link UserBookmarksVO paginated public bookmarks of the user and the total pages}
     */
    @GetMapping("/get/{username}")
    public UserBookmarksVO getUserPublicBookmarks(@PathVariable("username") String username,
                                                  @PageInfo(size = 8, paramName = PageInfoParam.CURRENT_PAGE)
                                                          PageInfoDTO pageInfo) {
        return websiteService.getUserPublicBookmarks(username, pageInfo);
    }

    /**
     * Get additional information of the bookmarked website
     *
     * @param webId ID of the bookmarked website data
     * @return additional information of the bookmarked website
     */
    @GetMapping("/additional")
    public ResultVO<WebMoreInfoDTO> getAdditionalInfo(@RequestParam("webId") Integer webId) {
        WebMoreInfoDTO info = websiteService.getAdditionalInfo(webId);
        return ResultCreator.okResult(info);
    }

    private String getCurrentUsername() {
        return StpUtil.getLoginIdAsString();
    }
}