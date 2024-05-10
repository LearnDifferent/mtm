package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.DataAccessType;
import com.github.learndifferent.mtm.constant.consist.ErrorInfoConstant;
import com.github.learndifferent.mtm.constant.enums.PageInfoParam;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.UserLoginInfoDTO;
import com.github.learndifferent.mtm.entity.SysLog;
import com.github.learndifferent.mtm.entity.SysMenu;
import com.github.learndifferent.mtm.query.SysMenuRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.SystemLogService;
import com.github.learndifferent.mtm.service.SystemMenuService;
import com.github.learndifferent.mtm.utils.LoginUtils;
import com.github.learndifferent.mtm.validationgroup.OnCreation;
import com.github.learndifferent.mtm.validationgroup.OnUpdate;
import java.util.List;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * System Controller
 *
 * @author zhou
 * @date 2022/4/11
 */
@RestController
@RequestMapping("/system")
@Validated
@RequiredArgsConstructor
public class SystemController {

    private final SystemLogService logService;
    private final SystemMenuService systemMenuService;

    /**
     * Get all system logs
     *
     * @return all system logs
     */
    @GetMapping("/menus")
    @AccessPermissionCheck(dataAccessType = DataAccessType.IS_ADMIN)
    public ResultVO<List<SysMenu>> getAllSystemMenus() {
        List<SysMenu> allMenus = systemMenuService.getAllMenus();
        return ResultCreator.okResult(allMenus);
    }

    /**
     * Get all system logs by user role
     *
     * @return all system logs
     */
    @GetMapping("/menus/role")
    public ResultVO<List<SysMenu>> getSystemMenusByRole() {
        long currentUserId = LoginUtils.getCurrentUserId();
        List<SysMenu> allMenus = systemMenuService.getAllMenus(currentUserId);
        return ResultCreator.okResult(allMenus);
    }

    /**
     * Get a menu by ID
     *
     * @param id menu ID
     * @return menu
     * @throws com.github.learndifferent.mtm.exception.ServiceException if the menu is not found or the user does not
     *                                                                  have permission to access it
     */
    @GetMapping("/menu")
    public ResultVO<SysMenu> getSystemMenu(@RequestParam("id")
                                           @Positive(message = ErrorInfoConstant.MENU_ID_NOT_POSITIVE) long id) {
        UserLoginInfoDTO userInfo = LoginUtils.getCurrentUserInfo();
        SysMenu menu = systemMenuService.getMenu(id, userInfo);
        return ResultCreator.okResult(menu);
    }

    /**
     * Create a menu
     *
     * @param menu menu information
     */
    @PostMapping("/menu")
    public void createMenu(@RequestBody @Validated(OnCreation.class) SysMenuRequest menu) {
        UserLoginInfoDTO userInfo = LoginUtils.getCurrentUserInfo();
        systemMenuService.addMenu(menu, userInfo);
    }

    /**
     * Update a menu
     *
     * @param menu menu information
     */
    @PutMapping("/menu")
    public void updateMenu(@RequestBody @Validated(OnUpdate.class) SysMenuRequest menu) {
        systemMenuService.updateMenu(menu);
    }

    /**
     * Delete a menu
     *
     * @param id menu ID
     */
    @DeleteMapping("/menu")
    public void deleteMenu(@RequestParam("id")
                           @Positive(message = ErrorInfoConstant.MENU_ID_NOT_POSITIVE) long id) {
        UserLoginInfoDTO userInfo = LoginUtils.getCurrentUserInfo();
        systemMenuService.deleteMenu(id, userInfo);
    }

    /**
     * Get system logs from cache and database
     *
     * @param pageInfo pagination information
     * @return system logs
     * @throws com.github.learndifferent.mtm.exception.ServiceException This will throw an exception with the result
     *                                                                  code of
     *                                                                  {@link ResultCode#PERMISSION_DENIED}
     *                                                                  if the user is not admin
     */
    @GetMapping("/logs")
    @AccessPermissionCheck(dataAccessType = DataAccessType.IS_ADMIN)
    public ResultVO<List<SysLog>> getSystemLogs(
            @PageInfo(size = 20, paramName = PageInfoParam.CURRENT_PAGE) PageInfoDTO pageInfo) {

        List<SysLog> logs = logService.getSystemLogs(pageInfo);
        return ResultCreator.okResult(logs);
    }

    /**
     * Get system logs from database directly
     *
     * @param pageInfo pagination information
     * @return system logs
     * @throws com.github.learndifferent.mtm.exception.ServiceException This
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link ResultCode#PERMISSION_DENIED}
     *                                                                  if the user is not admin
     */
    @GetMapping("/logs/no-cache")
    @AccessPermissionCheck(dataAccessType = DataAccessType.IS_ADMIN)
    public ResultVO<List<SysLog>> getSystemLogsFromDatabaseDirectly(
            @PageInfo(size = 20, paramName = PageInfoParam.CURRENT_PAGE) PageInfoDTO pageInfo) {

        List<SysLog> logs = logService.getSystemLogsFromDatabaseDirectly(pageInfo);
        return ResultCreator.okResult(logs);
    }
}
