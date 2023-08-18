package com.github.learndifferent.mtm.constant.enums;

import com.github.learndifferent.mtm.constant.consist.ConstraintConstant;

/**
 * Result Code
 *
 * @author zhou
 * @date 2021/09/05
 */
public enum ResultCode {

    /**
     * Success
     */
    SUCCESS(200, "Success"),
    /**
     * Fail
     */
    FAILED(500, "Fail"),
    /**
     * Website data does not exist
     */
    WEBSITE_DATA_NOT_EXISTS(2001, "The website data does not exist"),
    /**
     * Fail to update
     */
    UPDATE_FAILED(2002, "Fail to update"),
    /**
     * Fail to DELETE
     */
    DELETE_FAILED(2003, "Fail to DELETE"),
    /**
     * This username is already taken
     */
    USER_ALREADY_EXIST(2004, "This username is already taken"),
    /**
     * Please Login
     */
    NOT_LOGIN(2005, "Please Login"),
    /**
     * The username or password is incorrect
     */
    USER_NOT_EXIST(2006, "Invalid username or password"),
    /**
     * The verification code you entered is incorrect.
     */
    VERIFICATION_CODE_FAILED(2007, "The verification code you entered is incorrect."),
    /**
     * The verification code you entered is incorrect.
     */
    INVITATION_CODE_FAILED(2008, "The invitation code you entered is incorrect."),
    /**
     * Don't have permission to do it.
     */
    PERMISSION_DENIED(2009, "You don't have permission to do it."),
    /**
     * Has already bookmarked
     */
    ALREADY_SAVED(2010, "You have already saved it. Can't do it twice."),
    /**
     * enter a valid URL
     */
    URL_MALFORMED(2011, "Please enter a valid URL"),
    /**
     * Access denied for some reasons
     */
    URL_ACCESS_DENIED(2012, "Access denied for some reasons. Please try another link."),
    /**
     * no results
     */
    NO_RESULTS_FOUND(2013, "There are no results that match your search"),
    /**
     * Successfully reset the password
     */
    PASSWORD_CHANGED(2014, "Successfully reset your password!"),
    /**
     * Deleted
     */
    DELETE_SUCCESS(3001, "Deleted Successfully"),
    /**
     * Incorrect Password
     */
    PASSWORD_INCORRECT(3002, "Incorrect Password"),
    /**
     * Username must contain only letters and numbers
     */
    USERNAME_ONLY_LETTERS_NUMBERS(3003, "Username must contain ONLY letters and numbers"),
    /**
     * Username is too long
     */
    USERNAME_TOO_LONG(3004, "Username must be less than "
            + ConstraintConstant.USERNAME_MAX_LENGTH + " characters"),
    /**
     * Password is too long
     */
    PASSWORD_TOO_LONG(3005, "Password must be less than "
            + ConstraintConstant.PASSWORD_MAX_LENGTH + " characters"),
    /**
     * Username is empty
     */
    USERNAME_EMPTY(3006, "Please Enter Username"),
    /**
     * Password is empty
     */
    PASSWORD_EMPTY(3007, "Please Enter Password"),
    /**
     * Email not set up properly
     */
    EMAIL_SET_UP_ERROR(3008, "Email not set up properly"),
    /**
     * Duplicate comment
     */
    COMMENT_EXISTS(3009, "Duplicate comment detected: The comment has already been sent by you."),
    /**
     * Comment is empty
     */
    COMMENT_EMPTY(3010, "Please enter a comment."),
    /**
     * Comment is too long
     */
    COMMENT_TOO_LONG(3011, "Comment should not be longer than "
            + ConstraintConstant.COMMENT_MAX_LENGTH + " characters."),
    /**
     * Comment does not exist
     */
    COMMENT_NOT_EXISTS(3012, "The comment does not exist."),
    /**
     * Error related to Json
     */
    JSON_ERROR(3013, "Error related to Json"),
    /**
     * Timestamp is invalid
     */
    TIMESTAMP_INVALID(3014, "Please check your timestamp"),
    /**
     * Tag is too long
     */
    TAG_TOO_LONG(3015, "Tag should not be longer than "
            + ConstraintConstant.TAG_MAX_LENGTH + " characters"),
    /**
     * Tag does not exist
     */
    TAG_NOT_EXISTS(3016, "Please apply a tag."),
    /**
     * Tag has already been applied
     */
    TAG_EXISTS(3017, "The tag has already been applied"),
    /**
     * Not a valid HTML file that contains bookmarks
     */
    HTML_FILE_NO_BOOKMARKS(3018, "No Data Available. Please Upload the Correct HTML file."),
    /**
     * Password is too short
     */
    PASSWORD_TOO_SHORT(3019, "Password must be greater than "
            + ConstraintConstant.PASSWORD_MIN_LENGTH + " characters"),
    /**
     * Validation Failed
     */
    VALIDATION_FAILED(3020, "Validation Failed"),
    /**
     * Idempotency key cannot be blank
     */
    IDEMPOTENCY_KEY_BLANK(3021, "Idempotency key cannot be blank"),
    /**
     * Idempotency key conflict
     */
    IDEMPOTENCY_KEY_CONFLICT(3022, "Idempotency key conflict"),
    /**
     * Idempotency key is not valid
     */
    IDEMPOTENCY_KEY_NOT_VALID(3023, "Idempotency key is not valid"),
    /**
     * The passwords are the same
     */
    PASSWORD_SAME(3024, "The new password cannot be the same as the old password"),
    /**
     * Unable to connect to the server
     */
    CONNECTION_ERROR(5001, "Unable to connect to the server. Please try again later."),
    /**
     * Unknown Error
     */
    ERROR(502, "Unknown Error");

    private final Integer value;

    private final String msg;

    ResultCode(final Integer value, final String msg) {
        this.value = value;
        this.msg = msg;
    }

    public Integer value() {
        return value;
    }

    public String msg() {
        return msg;
    }
}
