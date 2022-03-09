package com.github.learndifferent.mtm.annotation.validation.register;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Verify register information
 * This annotation has to be used along with {@link com.github.learndifferent.mtm.annotation.common.InvitationCodeToken},
 * {@link com.github.learndifferent.mtm.annotation.common.InvitationCode},
 * {@link com.github.learndifferent.mtm.annotation.common.UserRole},
 * {@link com.github.learndifferent.mtm.annotation.common.VerificationCodeToken},
 * and {@link com.github.learndifferent.mtm.annotation.common.VerificationCode}.
 *
 * @author zhou
 * @date 2021/09/05
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterCodeCheck {}
