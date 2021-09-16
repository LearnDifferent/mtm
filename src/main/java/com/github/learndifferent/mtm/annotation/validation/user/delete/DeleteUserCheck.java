package com.github.learndifferent.mtm.annotation.validation.user.delete;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 删除用户前查看是否可以删除
 *
 * @author zhou
 * @date 2021/09/05
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DeleteUserCheck {

    /**
     * 用户名参数的名称
     *
     * @return {@code String} 用户名参数的名称
     */
    String usernameParamName();

    /**
     * 密码参数的名称
     *
     * @return {@code String} 密码参数的名称
     */
    String passwordParamName();
}
