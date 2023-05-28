package com.github.learndifferent.mtm.constant.consist;

/**
 * Constants related to error information
 *
 * @author zhou
 * @date 2023/5/28
 */
public final class ErrorInfoConstant {

    private ErrorInfoConstant() {
    }

    public static final String OLD_PASSWORD_EMPTY = "Old password cannot be empty";

    public static final String OLD_PASSWORD_LENGTH = "Old password must be between {min} and {max} characters long";

    public static final String NEW_PASSWORD_EMPTY = "New password cannot be empty";

    public static final String NEW_PASSWORD_LENGTH = "New password must be between {min} and {max} characters long";

    public static final String USERNAME_EMPTY = "Username cannot be empty";

    public static final String USERNAME_LENGTH = "Username must be between {min} and {max} characters long";
}
