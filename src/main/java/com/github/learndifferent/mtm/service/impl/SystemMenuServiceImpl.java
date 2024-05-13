package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.UserRole;
import com.github.learndifferent.mtm.dto.SysMenuDTO;
import com.github.learndifferent.mtm.dto.UserLoginInfoDTO;
import com.github.learndifferent.mtm.entity.SysMenu;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.mapper.SystemMenuMapper;
import com.github.learndifferent.mtm.query.SysMenuRequest;
import com.github.learndifferent.mtm.service.SystemMenuService;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.utils.ApplicationContextUtils;
import com.github.learndifferent.mtm.utils.BeanUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

/**
 * System Menu Service
 *
 * @author zhou
 * @date 2024/4/15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SystemMenuServiceImpl implements SystemMenuService {

    private final SystemMenuMapper systemMenuMapper;
    private final UserService userService;

    @Cacheable("menu:all")
    public List<SysMenu> getAllMenusFromDatabase() {
        return systemMenuMapper.getAllMenus();
    }

    private SystemMenuServiceImpl getCurrentBean() {
        return ApplicationContextUtils.getBean(SystemMenuServiceImpl.class);
    }

    @Override
    public List<SysMenu> getAllMenus() {
        List<SysMenu> menus = getCurrentBean().getAllMenusFromDatabase();
        return buildMenuTree(menus);
    }

    private List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        Map<Long, SysMenu> menuMap = menus.stream()
                // Map: ID -> SysMenu
                .collect(Collectors.toMap(SysMenu::getId, Function.identity()));

        // Top Level Menus
        List<SysMenu> rootMenus = new ArrayList<>();

        menus.forEach(menu -> updateRootMenus(rootMenus, menuMap, menu));

        return rootMenus;
    }

    private void updateRootMenus(List<SysMenu> rootMenus, Map<Long, SysMenu> menuMap, SysMenu menu) {
        if (checkIfRootMenu(menu)) {
            rootMenus.add(menu);
        } else {
            setNotRootMenu(menuMap, menu);
        }
    }

    private void setNotRootMenu(Map<Long, SysMenu> menuMap, SysMenu menu) {
        long parentId = menu.getParentId();
        SysMenu parentMenu = menuMap.get(parentId);
        if (parentMenu.getChildren() == null) {
            parentMenu.setChildren(new ArrayList<>());
        }
        parentMenu.getChildren().add(menu);
    }

    private boolean checkIfRootMenu(SysMenu menu) {
        long parentId = menu.getParentId();
        return parentId == 0L;
    }

    @Override
    public List<SysMenu> getAllMenus(long userId) {
        UserRole role = userService.getUserRoleByUserId(userId);
        return getCurrentBean().getAllMenus(role);
    }

    @Cacheable(value = "menu:role", key = "#role")
    public List<SysMenu> getAllMenus(UserRole role) {

        if (UserRole.ADMIN.equals(role)) {
            // Admin role has access to all menus
            return this.getAllMenus();
        }

        List<SysMenu> allMenus = getCurrentBean().getAllMenusFromDatabase();

        // Filter menus based on role
        List<SysMenu> menus = allMenus.stream()
                .filter(menu -> checkRolePermission(menu, role))
                .collect(Collectors.toList());

        // Build menu tree
        return buildMenuTree(menus);
    }

    private boolean checkRolePermission(SysMenu menu, UserRole role) {
        String permissions = menu.getPermissions();
        String[] perms = permissions.split(":");
        return Arrays.stream(perms)
                .anyMatch(perm -> role.role().equalsIgnoreCase(perm));
    }

    @Override
    public SysMenu getMenu(long id, UserLoginInfoDTO userInfo) {
        Long userId = userInfo.getUserId();
        String username = userInfo.getUsername();
        UserRole userRole = userService.getUserRoleByUserId(id);

        log.info("Getting menu with ID {} by 【user ID: {}, username: {}, user role: {}】",
                id, userId, username, userRole.role());
        SysMenu menu = systemMenuMapper.getMenuData(id);
        log.info("Menu data: {}", menu);

        return Optional.ofNullable(menu)
                .filter(m -> checkRolePermission(m, userRole))
                .orElseThrow(() -> new ServiceException(ResultCode.MENU_NOT_FOUND));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "menu:all", allEntries = true),
            @CacheEvict(value = "menu:role", allEntries = true)
    })
    public void addMenu(SysMenuRequest sysMenuRequest, UserLoginInfoDTO userInfo) {
        Long userId = userInfo.getUserId();
        String username = userInfo.getUsername();
        SysMenuDTO menu = BeanUtils.convert(sysMenuRequest, SysMenuDTO.class);
        log.info("Adding menu: 【{}】 by 【user ID: {}, username: {}】", menu, userId, username);

        String user = "User ID: " + userId + ", Username: " + username;
        menu.setCreatedBy(user);
        menu.setUpdatedBy(user);
        log.info("Creating menu by: {}", user);

        systemMenuMapper.addMenu(menu);
        log.info("Menu added: {}", menu);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "menu:all", allEntries = true),
            @CacheEvict(value = "menu:role", allEntries = true)
    })
    public void updateMenu(SysMenuRequest sysMenuRequest) {
        log.info("Updating menu: {}", sysMenuRequest);
        SysMenuDTO menu = BeanUtils.convert(sysMenuRequest, SysMenuDTO.class);
        systemMenuMapper.updateMenu(menu);
        log.info("Menu updated: {}", menu);
    }

    @Override
    public void deleteMenu(long id, UserLoginInfoDTO userInfo) {
        Long userId = userInfo.getUserId();
        String username = userInfo.getUsername();
        log.info("Deleting menu with ID {} by 【user ID: {}, username: {}】", id, userId, username);
        systemMenuMapper.deleteMenu(id);
        log.info("Menu deleted: ID: {}", id);
    }
}
