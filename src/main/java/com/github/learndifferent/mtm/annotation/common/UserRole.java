package com.github.learndifferent.mtm.annotation.common;

import com.github.learndifferent.mtm.constant.enums.RoleType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解所修饰的参数为用户角色
 *
 * @author zhou
 * @date 2021/09/20
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserRole {

    /**
     * The default role to use as a fallback when the request parameter is
     * not provided or the role value does not match any value of {@link RoleType}
     *
     * @return {@link RoleType} default role
     */
    RoleType defaultRole();
}
