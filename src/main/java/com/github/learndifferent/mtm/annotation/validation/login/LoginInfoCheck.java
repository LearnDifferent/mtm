package com.github.learndifferent.mtm.annotation.validation.login;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 检查登陆信息是否正确
 *
 * @author zhou
 * @date 2021/09/05
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginInfoCheck {

    /**
     * 验证码参数的名称
     *
     * @return 验证码参数的名称
     */
    String codeParamName();

    /**
     * 前端 localStorage 存储的，用于验证码的验证的 token 的参数名称
     *
     * @return 名称
     */
    String verifyTokenParamName();

    /**
     * 用户名的参数的名称
     *
     * @return 用户名参数名称
     */
    String usernameParamName();

    /**
     * 密码的参数的名称
     *
     * @return 密码参数名称
     */
    String passwordParamName();
}
