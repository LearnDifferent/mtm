package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.general.idempotency.IdempotencyCheck;
import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.annotation.validation.user.role.admin.AdminValidation;
import com.github.learndifferent.mtm.constant.consist.ConstraintConstant;
import com.github.learndifferent.mtm.constant.consist.ErrorInfoConstant;
import com.github.learndifferent.mtm.constant.enums.AccessPrivilege;
import com.github.learndifferent.mtm.constant.enums.AddDataMode;
import com.github.learndifferent.mtm.constant.enums.PageInfoParam;
import com.github.learndifferent.mtm.constant.enums.Privacy;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.query.BasicWebDataRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.BookmarkService;
import com.github.learndifferent.mtm.vo.BookmarkVO;
import com.github.learndifferent.mtm.vo.BookmarkingResultVO;
import com.github.learndifferent.mtm.vo.BookmarksAndTotalPagesVO;
import com.github.learndifferent.mtm.vo.VisitedBookmarkVO;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
@Validated
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Autowired
    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    /**
     * Bookmark a new web page
     *
     * @param url     URL of the web page to bookmark
     * @param privacy {@link Privacy#PUBLIC} if this is a public bookmark and
     *                {@link Privacy#PRIVATE} if this is private
     * @param mode    {@link AddDataMode#ADD_TO_DATABASE} if the data should only be added to the database.
     *                <p>
     *                {@link AddDataMode#ADD_TO_DATABASE_AND_ELASTICSEARCH} if the data should
     *                be added to the database and ElasticSearch
     *                </p>
     *                <p>
     *                Note that this value will be ignored if this is a private bookmark
     *                because only public data can be added to Elasticsearch
     *                </p>
     * @return The result of bookmarking a new web page
     * @throws ServiceException Exception will be thrown with the result code of {@link ResultCode#URL_MALFORMED},
     *                          {@link ResultCode#URL_ACCESS_DENIED} and {@link ResultCode#CONNECTION_ERROR}
     *                          when an error occurred during an IO operation
     */
    @GetMapping
    @IdempotencyCheck
    public BookmarkingResultVO bookmark(@RequestParam("url")
                                        @URL(message = ErrorInfoConstant.URL_INVALID)
                                        @NotBlank(message = ErrorInfoConstant.URL_INVALID)
                                                String url,
                                        @RequestParam("privacy") Privacy privacy,
                                        @RequestParam("mode") AddDataMode mode) {
        String currentUsername = getCurrentUsername();
        return bookmarkService.bookmark(url, currentUsername, privacy, mode);
    }

    /**
     * Add a website to the bookmarks
     *
     * @param basicData Request body that contains title, URL, image and description
     * @return {@link ResultCode#SUCCESS} if success. {@link ResultCode#FAILED} if failure.
     * @throws ServiceException throw exceptions with the result code of {@link ResultCode#ALREADY_SAVED},
     *                          {@link ResultCode#PERMISSION_DENIED} and {@link ResultCode#URL_MALFORMED}
     *                          if something goes wrong
     */
    @PostMapping
    @IdempotencyCheck
    public ResultVO<ResultCode> addToBookmark(@RequestBody @Validated BasicWebDataRequest basicData) {
        String currentUsername = getCurrentUsername();
        boolean success = bookmarkService.addToBookmark(basicData, currentUsername);
        return success ? ResultCreator.okResult() : ResultCreator.failResult();
    }

    /**
     * Delete a bookmark
     *
     * @param id ID of the bookmark
     * @return {@link ResultCode#DELETE_SUCCESS} if success. {@link ResultCode#DELETE_FAILED}
     * @throws ServiceException {@link BookmarkService#deleteBookmark(Integer, String)} will throw
     *                          an exception if the user currently logged in does not have permission to delete,
     *                          the result code will be {@link ResultCode#PERMISSION_DENIED}
     */
    @DeleteMapping
    @IdempotencyCheck
    public ResultVO<ResultCode> deleteBookmark(@RequestParam("id")
                                               @NotNull(message = ErrorInfoConstant.BOOKMARK_NOT_FOUND)
                                               @Positive(message = ErrorInfoConstant.BOOKMARK_NOT_FOUND)
                                                       Integer id) {
        String currentUsername = getCurrentUsername();
        boolean success = bookmarkService.deleteBookmark(id, currentUsername);
        return success ? ResultCreator.result(ResultCode.DELETE_SUCCESS)
                : ResultCreator.result(ResultCode.DELETE_FAILED);
    }

    /**
     * Make the bookmark private if it's public
     * and make it public if it's private.
     *
     * @param id ID of the bookmark
     * @return {@link ResultCode#SUCCESS} if success. {@link ResultCode#UPDATE_FAILED} if failure.
     * @throws ServiceException {@link BookmarkService#changePrivacySettings(Integer, String)}
     *                          will throw an exception with {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}
     *                          if the bookmark does not exist and with {@link ResultCode#PERMISSION_DENIED}
     *                          if the user currently logged in has no permission to change the bookmark privacy
     *                          settings
     */
    @GetMapping("/privacy")
    @IdempotencyCheck
    public ResultVO<ResultCode> changePrivacySettings(@RequestParam("id")
                                                      @NotNull(message = ErrorInfoConstant.BOOKMARK_NOT_FOUND)
                                                      @Positive(message = ErrorInfoConstant.BOOKMARK_NOT_FOUND)
                                                              Integer id) {
        String currentUsername = getCurrentUsername();
        boolean success = bookmarkService.changePrivacySettings(id, currentUsername);
        return success ? ResultCreator.okResult() : ResultCreator.result(ResultCode.UPDATE_FAILED);
    }

    /**
     * Get a bookmark
     *
     * @param id ID of the bookmark
     * @return {@link BookmarkVO the bookmark}
     * @throws ServiceException If the user currently logged in has no permission to get the bookmark,
     *                          or the bookmark doesn't exist, a {@link ServiceException}
     *                          will be thrown with the result code of {@link ResultCode#PERMISSION_DENIED}
     *                          or {@link ResultCode#WEBSITE_DATA_NOT_EXISTS}
     */
    @GetMapping("/get")
    public BookmarkVO getBookmark(@RequestParam("id")
                                  @NotNull(message = ErrorInfoConstant.BOOKMARK_NOT_FOUND)
                                  @Positive(message = ErrorInfoConstant.BOOKMARK_NOT_FOUND)
                                          Integer id) {
        String currentUsername = getCurrentUsername();
        return bookmarkService.getBookmark(id, currentUsername);
    }

    /**
     * Get paginated bookmarks of the user currently logged in
     *
     * @param pageInfo pagination information
     * @return paginated bookmarks of the user currently logged in and the total pages
     */
    @GetMapping("/get/user")
    public BookmarksAndTotalPagesVO getCurrentUserBookmarks(@PageInfo(size = 8, paramName = PageInfoParam.CURRENT_PAGE)
                                                                    PageInfoDTO pageInfo) {
        String currentUsername = getCurrentUsername();
        return bookmarkService.getUserBookmarks(currentUsername, pageInfo, AccessPrivilege.ALL);
    }

    /**
     * Get paginated public bookmarks of a user
     *
     * @param username username of the user whose public bookmarks is being requested
     * @param pageInfo pagination info
     * @return paginated public bookmarks of the user and the total pages
     */
    @GetMapping("/get/user/{username}")
    public BookmarksAndTotalPagesVO getUserPublicBookmarks(@PathVariable("username")
                                                           @NotBlank(message = ErrorInfoConstant.USER_NOT_FOUND)
                                                           @Length(min = ConstraintConstant.USERNAME_MIN_LENGTH,
                                                                   max = ConstraintConstant.USERNAME_MAX_LENGTH,
                                                                   message = ErrorInfoConstant.USERNAME_LENGTH)
                                                                   String username,
                                                           @PageInfo(size = 8, paramName = PageInfoParam.CURRENT_PAGE)
                                                                   PageInfoDTO pageInfo) {
        return bookmarkService.getUserBookmarks(username, pageInfo, AccessPrivilege.LIMITED);
    }

    /**
     * Get visited bookmarks
     *
     * @param pageInfo Pagination information
     * @return visited bookmarks
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link AdminValidation} annotation
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  if the user is not admin
     */
    @GetMapping("/visited-bookmarks")
    @AdminValidation
    public List<VisitedBookmarkVO> getVisitedBookmarks(
            @PageInfo(size = 20, paramName = PageInfoParam.CURRENT_PAGE) PageInfoDTO pageInfo) {
        return bookmarkService.getVisitedBookmarks(pageInfo);
    }

    private String getCurrentUsername() {
        return StpUtil.getLoginIdAsString();
    }
}