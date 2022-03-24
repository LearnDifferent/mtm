package com.github.learndifferent.mtm.constant.consist;

/**
 * Constants related to Redis Key
 *
 * @author zhou
 * @date 2021/09/05
 */
public final class KeyConstant {

    private KeyConstant() {
    }

    /**
     * The key of system notifications
     */
    public static final String SYSTEM_NOTIFICATION = "systemNotification";

    /**
     * The key of the lowercase usernames of users that have read the latest system notifications
     */
    public static final String SYSTEM_NOTIFICATION_READ_USERS = "systemNotification:read";

    /**
     * The key of reply notification is "notification:" + lowercase username
     */
    public static final String REPLY_NOTIFICATION_PREFIX = "notification:";

    /**
     * The key of reply notification count is "notification:count:" + lowercase username
     */
    public static final String REPLY_NOTIFICATION_COUNT_PREFIX = "notification:count:";

    /**
     * The key of website data's views is "view:" + {@code webId}
     */
    public static final String WEB_VIEW_COUNT_PREFIX = "view:";
}
