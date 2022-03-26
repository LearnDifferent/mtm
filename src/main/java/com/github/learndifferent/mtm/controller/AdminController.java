package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.annotation.validation.user.role.admin.AdminValidation;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.dto.VisitedBookmarksDTO;
import com.github.learndifferent.mtm.entity.SysLog;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.SystemLogService;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.service.ViewCounterService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin Page Controller
 *
 * @author zhou
 * @date 2021/09/05
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final SystemLogService logService;
    private final ViewCounterService viewCounterService;

    @Autowired
    public AdminController(UserService userService,
                           SystemLogService logService,
                           ViewCounterService viewCounterService) {
        this.userService = userService;
        this.logService = logService;
        this.viewCounterService = viewCounterService;
    }

    /**
     * Check whether the current user is an admin
     *
     * @return {@link ResultCreator#okResult()} if the current user is admin
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link AdminValidation} annotation
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  if the user is not admin
     */
    @GetMapping
    @AdminValidation
    public ResultVO<?> checkAdmin() {
        return ResultCreator.okResult();
    }

    /**
     * Get system logs
     *
     * @param pageInfo pagination info
     * @return system logs
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link AdminValidation} annotation
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  if the user is not admin
     */
    @GetMapping("/logs")
    @AdminValidation
    public ResultVO<List<SysLog>> getSystemLogs(@PageInfo(size = 20) PageInfoDTO pageInfo) {
        List<SysLog> logs = logService.getSystemLogs(pageInfo);
        return ResultCreator.okResult(logs);
    }

    /**
     * Get users
     *
     * @param pageInfo pagination info
     * @return users
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link AdminValidation} annotation
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  if the user is not admin
     */
    @GetMapping("/users")
    @AdminValidation
    public ResultVO<List<UserDTO>> getUsers(@PageInfo(size = 20) PageInfoDTO pageInfo) {
        List<UserDTO> users = userService.getUsers(pageInfo);
        return ResultCreator.okResult(users);
    }

    /**
     * Get visited bookmarks from database
     *
     * @param pageInfo pagination info
     * @return visited bookmarks
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link AdminValidation} annotation
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  if the user is not admin
     */
    @GetMapping("/visited-bookmarks")
    @AdminValidation
    public ResultVO<List<VisitedBookmarksDTO>> getVisitedBookmarks(@PageInfo(size = 20) PageInfoDTO pageInfo) {
        List<VisitedBookmarksDTO> bookmarks = viewCounterService.getVisitedBookmarks(pageInfo);
        return ResultCreator.okResult(bookmarks);
    }
}
