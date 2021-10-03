package com.github.learndifferent.mtm.constant.enums;

/**
 * 响应码
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
     * Website data does not exists
     */
    WEBSITE_DATA_NOT_EXISTS(2001, "The website data does not exists"),
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
     * already marked it
     */
    ALREADY_MARKED(2010, "You have ALREADY MARKED it. Can't do it twice."),
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
     * 成功修改密码
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
     * 用户名只能为英文字母和数字
     */
    USERNAME_ONLY_LETTERS_NUMBERS(3003, "Username must contain ONLY letters and numbers"),
    /**
     * 用户名太长
     */
    USERNAME_TOO_LONG(3004, "Username must be less than 30 characters"),
    /**
     * 用户名太长
     */
    PASSWORD_TOO_LONG(3005, "Password must be less than 50 characters"),
    /**
     * 用户名为空
     */
    USERNAME_EMPTY(3006, "Please Enter Username"),
    /**
     * 密码为空
     */
    PASSWORD_EMPTY(3007, "Please Enter Password"),
    /**
     * 电子邮件设置错误
     */
    EMAIL_SET_UP_ERROR(3008, "Email not set up properly"),
    /**
     * 评论已经存在
     */
    COMMENT_EXISTS(3009, "Duplicate comment detected: The comment has already been sent by you."),
    /**
     * 评论为空
     */
    COMMENT_EMPTY(3010, "Please enter a comment."),
    /**
     * 评论太长
     */
    COMMENT_TOO_LONG(3011, "Comment should not be longer than 140 characters."),
    /**
     * 评论不存在
     */
    COMMENT_NOT_EXISTS(3012, "The comment does not exists."),
    /**
     * Error related to Json
     */
    JSON_ERROR(3013, "Error related to Json"),
    /**
     * Can't find the user role
     */
    USER_ROLE_NOT_FOUND(3014, "Can't find the user role"),
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
