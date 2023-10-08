package com.github.learndifferent.mtm.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import com.github.learndifferent.mtm.dto.UserLoginInfoDTO;
import com.github.learndifferent.mtm.mapper.UserMapper;
import com.github.learndifferent.mtm.utils.JsonUtils;
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

    private final UserMapper userMapper;

    @Override
    public List<String> getPermissionList(Object o, String s) {
        return null;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginKey) {

        String json = (String) loginId;
        UserLoginInfoDTO userInfo = JsonUtils.toObject(json, UserLoginInfoDTO.class);
        Long userId = userInfo.getUserId();

        String role = userMapper.getRoleByUserId(userId);
        return Collections.singletonList(role);
    }
}