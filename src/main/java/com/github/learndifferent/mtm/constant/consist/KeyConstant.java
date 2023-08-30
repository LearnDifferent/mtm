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
     * The key of all system notifications
     */
    public static final String SYSTEM_NOTIFICATIONS = "notification:system";

    /**
     * Redis key prefix for the read status of the system notification.
     * Key: prefix + notification ID
     */
    public static final String SYSTEM_NOTIFICATION_READ_STATUS_PREFIX = "notification:system:is_read:";

    /**
     * Redis key prefix for all reply notifications of the user.
     * Key: prefix + user ID
     */
    public static final String USER_REPLY_NOTIFICATIONS_PREFIX = "notification:reply:user:";

    /**
     * Redis key prefix for the read status of the reply notification of the user.
     * Key: prefix + user ID
     */
    public static final String USER_REPLY_NOTIFICATION_READ_STATUS_PREFIX = "notification:reply:user:is_read";

    /**
     * The key of the set in Redis which stores all the keys that contain the view data
     */
    public static final String VIEW_KEY_SET = "all:view";

    /**
     * The key prefix of the key that stores the number of views of a bookmark
     */
    public static final String WEB_VIEW_COUNT_PREFIX = "view:";

    /**
     * The key of user role's change record is "change:record:" + {@code userId}
     */
    public static final String ROLE_CHANGE_RECORD_PREFIX = "change:record:";

    /**
     * The hash key of {@link #ROLE_CHANGE_RECORD_PREFIX}
     */
    public static final String FORMER_ROLE_CHANGE_RECORD_HASH_KEY = "former";

    /**
     * The hash key of {@link #ROLE_CHANGE_RECORD_PREFIX}
     */
    public static final String NEW_ROLE_CHANGE_RECORD_HASH_KEY = "new";

    /**
     * Usernames of the users who turned off notifications
     */
    public static final String MUTE_NOTIFICATIONS = "mute:notifications";

    /**
     * {@link com.github.learndifferent.mtm.annotation.general.idempotency.IdempotencyCheck} key prefix
     */
    public static final String IDEMPOTENCY_CHECK_PREFIX = "idempotency:check:";

    /**
     * Idempotency key prefix
     */
    public static final String IDEMPOTENCY_KEY_PREFIX = "idempotency:key:";
}