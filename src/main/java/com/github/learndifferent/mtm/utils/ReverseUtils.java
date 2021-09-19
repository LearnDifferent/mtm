package com.github.learndifferent.mtm.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.constant.enums.RoleType;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import java.util.Collection;
import org.springframework.util.CollectionUtils;

/**
 * 反转一些判断的 boolean，让其符合语义
 *
 * @author zhou
 * @date 2021/09/05
 */
public class ReverseUtils {

    public static boolean collectionNotEmpty(Collection<?> collection) {
        return !CollectionUtils.isEmpty(collection);
    }

    public static boolean stringNotEqualsIgnoreCase(String baseString, String anotherString) {
        if (baseString == null) {
            // 如果传入的 String 为 null，直接返回 true 表示不相等
            return true;
        }
        return !baseString.equalsIgnoreCase(anotherString);
    }

    public static boolean notAdmin() {
        return !StpUtil.hasRole(RoleType.ADMIN.role());
    }

    public static boolean hasNoPermissionToDelete(String userName, WebsiteDTO web) {
        boolean hasPermissionToDelete = web != null
                && web.getUserName().equals(userName);

        return !hasPermissionToDelete;
    }
}
