package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.entity.SysMenu;
import com.github.learndifferent.mtm.mapper.SystemMenuMapper;
import com.github.learndifferent.mtm.service.SystemMenuService;
import com.github.learndifferent.mtm.utils.ApplicationContextUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * System Menu Service
 *
 * @author zhou
 * @date 2024/4/15
 */
@Service
@RequiredArgsConstructor
public class SystemMenuServiceImpl implements SystemMenuService {

    private final SystemMenuMapper systemMenuMapper;

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
}
