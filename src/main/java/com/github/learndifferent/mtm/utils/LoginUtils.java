package com.github.learndifferent.mtm.utils;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.constant.enums.UserRole;
import com.github.learndifferent.mtm.dto.UserLoginInfoDTO;

/**
 * A class offering utilities for login
 *
 * @author zhou
 * @date 2023/9/19
 */
public class LoginUtils {

    private LoginUtils() {
    }

    public static SaTokenInfo setLoginInfoAndGetToken(UserLoginInfoDTO info) {

        String loginId = JsonUtils.toJson(info);

        // set login ID
        StpUtil.setLoginId(loginId);

        // token information
        return StpUtil.getTokenInfo();
    }

    public static long getTokenTimeout() {
        return StpUtil.getTokenTimeout();
    }

    public static UserLoginInfoDTO getCurrentUserInfo() {
        String loginId = StpUtil.getLoginIdAsString();
        return JsonUtils.toObject(loginId, UserLoginInfoDTO.class);
    }

    public static String getCurrentUsername() {
        UserLoginInfoDTO info = getCurrentUserInfo();
        return info.getUsername();
    }

    public static long getCurrentUserId() {
        UserLoginInfoDTO info = getCurrentUserInfo();
        return info.getUserId();
    }

    public static void logout() {
        StpUtil.logout();
    }

    public static boolean isGuest() {
        return StpUtil.hasRole(UserRole.GUEST.role());
    }

    public static boolean isNotAdmin() {
        return !StpUtil.hasRole(UserRole.ADMIN.role());
    }
}
