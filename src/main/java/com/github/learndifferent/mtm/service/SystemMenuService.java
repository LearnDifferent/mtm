package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.constant.enums.UserRole;
import com.github.learndifferent.mtm.entity.SysMenu;
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
     * Get all Menus for a specific role
     *
     * @param role User Role
     * @return All Menus for the specified role
     */
    List<SysMenu> getAllMenus(UserRole role);
}
