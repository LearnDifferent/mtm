package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.constant.enums.UserRole;
import com.github.learndifferent.mtm.dto.UserLoginInfoDTO;
import com.github.learndifferent.mtm.entity.SysMenu;
import com.github.learndifferent.mtm.query.SysMenuRequest;
import java.util.List;

/**
 * System Menu Service
 *
 * @author zhou
 * @date 2024/4/15
 */
public interface SystemMenuService {

    /**
     * Get all Menus
     *
     * @return All Menus
     */
    List<SysMenu> getAllMenus();

    /**
     * Get all Menus for a specific role by user ID
     *
     * @param userId User ID
     * @return All Menus for the specified role
     */
    List<SysMenu> getAllMenus(long userId);

    /**
     * Get a menu by ID
     *
     * @param id       menu ID
     * @param userInfo user information
     * @return menu
     * @throws com.github.learndifferent.mtm.exception.ServiceException if the menu is not found or the user does not
     *                                                                  have permission to access it
     */
    SysMenu getMenu(long id, UserLoginInfoDTO userInfo);

    /**
     * Add a menu
     *
     * @param sysMenuRequest menu to add
     * @param creatorId      creator's ID
     */
    void addMenu(SysMenuRequest sysMenuRequest, long creatorId);

    /**
     * Update a menu
     *
     * @param sysMenuRequest menu to update
     */
    void updateMenu(SysMenuRequest sysMenuRequest);

    /**
     * Delete a menu
     *
     * @param id       menu ID
     * @param userInfo user information
     */
    void deleteMenu(long id, UserLoginInfoDTO userInfo);
}
