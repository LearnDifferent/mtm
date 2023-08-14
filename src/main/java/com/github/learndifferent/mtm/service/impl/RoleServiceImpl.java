package com.github.learndifferent.mtm.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import com.github.learndifferent.mtm.service.UserService;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <a href='https://sa-token.cc/doc.html#/use/jur-auth'>sa-token settings</a>
 *
 * @author zhou
 * @date 2021/09/05
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements StpInterface {

    private final UserService userService;

    @Override
    public List<String> getPermissionList(Object o, String s) {
        return null;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginKey) {

        String role = userService.getRoleByName((String) loginId);
        return Collections.singletonList(role);
    }
}