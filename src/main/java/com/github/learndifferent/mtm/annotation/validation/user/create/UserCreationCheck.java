package com.github.learndifferent.mtm.annotation.validation.user.create;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Verify username, password and user role.
 * This annotation has to be used along with {@link com.github.learndifferent.mtm.annotation.common.Username
 * Username}, {@link com.github.learndifferent.mtm.annotation.common.Password Password}
 * and {@link com.github.learndifferent.mtm.annotation.common.UserRole UserRole}
 * If failed verification, throw exception with the following result codes according to the situation:
 * <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#USER_ALREADY_EXIST}</p>
 * <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#USERNAME_ONLY_LETTERS_NUMBERS}</p>
 * <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#USERNAME_TOO_LONG}</p>
 * <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#USERNAME_EMPTY}</p>
 * <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#PASSWORD_TOO_LONG}</p>
 * <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#PASSWORD_EMPTY}</p>
 * <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#USER_ROLE_NOT_FOUND}</p>
 *
 * @author zhou
 * @date 2021/09/13
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UserCreationCheck {}
