package com.github.learndifferent.mtm.annotation.validation.user.create;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Verify username, password and user role.
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
public @interface NewUserCheck {

    /**
     * The class that has the fields of username and password
     *
     * @return {@code Class<? extends Serializable>} class that has the fields of username and password
     */
    Class<?> userClass();

    /**
     * The field name of username in the class
     *
     * @return {@code String} field name of username in the class
     */
    String usernameFieldName();

    /**
     * The field name of password in the class
     *
     * @return {@code String} field name of password in the class
     */
    String passwordFieldName();

    /**
     * The field name of user role in the class
     *
     * @return {@code String} field name of user role in the class
     */
    String roleFieldName();
}
