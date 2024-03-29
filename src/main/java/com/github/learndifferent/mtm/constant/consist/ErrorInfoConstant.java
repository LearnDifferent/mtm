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

    public static final String PASSWORD_EMPTY = "Password cannot be empty";

    public static final String PASSWORD_LENGTH = "Password must be between {min} and {max} characters long";

    public static final String VERIFICATION_CODE_EMPTY = "Please enter verification code";

    public static final String OLD_PASSWORD_EMPTY = "Old password cannot be empty";

    public static final String OLD_PASSWORD_LENGTH = "Old password must be between {min} and {max} characters long";

    public static final String NEW_PASSWORD_EMPTY = "New password cannot be empty";

    public static final String NEW_PASSWORD_LENGTH = "New password must be between {min} and {max} characters long";

    public static final String USERNAME_EMPTY = "Username cannot be empty";

    public static final String USERNAME_LENGTH = "Username must be between {min} and {max} characters long";

    public static final String TAG_LENGTH = "Tag must be between {min} and {max} characters long";

    public static final String URL_INVALID = "The URL you entered is invalid";

    public static final String COMMENT_EMPTY = "Comment cannot be empty";

    public static final String TAG_EMPTY = "Tag cannot be empty";

    public static final String COMMENT_NOT_FOUND = "Can't find the comment";

    public static final String BOOKMARK_NOT_FOUND = "Can't find the bookmark";

    public static final String USER_NOT_FOUND = "Can't find the user";

    public static final String NO_DATA = "No data available";
}