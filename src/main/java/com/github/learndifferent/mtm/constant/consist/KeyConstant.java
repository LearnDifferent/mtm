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
    public static final String SYSTEM_NOTIFICATION = "system:notification";

    /**
     * The key of the lowercase usernames of users that have read the latest system notifications
     */
    public static final String SYSTEM_NOTIFICATION_READ_USERS = "system:notification:read";

    /**
     * The key of reply notification is "notification:" + lowercase username
     */
    public static final String REPLY_NOTIFICATION_PREFIX = "notification:";

    /**
     * Replies to read.
     * The key is "reply_to_read:user:" + lowercase username
     */
    public static final String USER_REPLY_TO_READ = "reply_to_read:user:";

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