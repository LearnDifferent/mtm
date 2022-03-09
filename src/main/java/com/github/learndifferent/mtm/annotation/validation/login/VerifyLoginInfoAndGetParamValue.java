package com.github.learndifferent.mtm.annotation.validation.login;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Check if the Login Info is valid and get parameter values from Request.
 * This annotation has to be used along with {@link com.github.learndifferent.mtm.annotation.common.Username},
 * {@link com.github.learndifferent.mtm.annotation.common.Password},
 * {@link com.github.learndifferent.mtm.annotation.common.VerificationCode}
 * and {@link com.github.learndifferent.mtm.annotation.common.VerificationCodeToken}
 *
 * @author zhou
 * @date 2021/09/05
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VerifyLoginInfoAndGetParamValue {}