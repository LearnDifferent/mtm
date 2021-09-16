package com.github.learndifferent.mtm.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import com.github.learndifferent.mtm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * sa-token 框架用于获取角色权限的实现
 *
 * @author zhou
 * @date 2021/09/05
 */
@Component
public class RoleServiceImpl implements StpInterface {

    private final UserService userService;

    @Autowired
    public RoleServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public List<String> getPermissionList(Object o, String s) {
        return null;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginKey) {

        String role = userService.getRoleByName((String) loginId);
        ArrayList<String> list = new ArrayList<>();
        list.add(role);
        return list;
    }
}
