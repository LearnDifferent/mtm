package com.github.learndifferent.mtm.annotation.validation.login;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 检查登陆信息是否正确，并给需要的参数赋值。
 * 搭配{@link com.github.learndifferent.mtm.annotation.common.Username}、
 * {@link com.github.learndifferent.mtm.annotation.common.Password}、
 * {@link com.github.learndifferent.mtm.annotation.common.VerificationCode} 以及
 * {@link com.github.learndifferent.mtm.annotation.common.VerificationCodeToken} 使用
 *
 * @author zhou
 * @date 2021/09/05
 * @see VerifyLoginInfoAndGetUsernameAspect 从 Request 中读取相应参数的值，并检查登陆信息是否正确
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VerifyLoginInfoAndGetParamValue {}