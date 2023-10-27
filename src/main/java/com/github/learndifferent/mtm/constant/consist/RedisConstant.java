package com.github.learndifferent.mtm.constant.consist;

/**
 * Constants related to Redis
 *
 * @author zhou
 * @date 2021/09/05
 */
public final class RedisConstant {

    private RedisConstant() {
    }

    /**
     * The key of all system notifications
     */
    public static final String SYSTEM_NOTIFICATIONS = "notification:system";

    /**
     * Key: prefix + user ID
     * <p>This is to store whether a particular notification has been read by a user</p>
     */
    public static final String SYS_NOTIFICATION_READ_STATUS_READ_BY_USER_PREFIX = "notification:system:read:";

    /**
     * Key: prefix + notification ID
     * <p>This is to track the notifications that a specific user has read</p>
     */
    public static final String SYS_NOTIFICATION_READ_STATUS_TRACK_NOTIFICATIONS_OF_USER_PREFIX =
            "notification:system:user:read:";

    /**
     * Redis key prefix for all reply notifications of the user.
     * Key: prefix + user ID
     */
    public static final String USER_REPLY_NOTIFICATIONS_PREFIX = "notification:reply:user:";

    /**
     * Redis key prefix for the read status of the reply notification of the user.
     * Key: prefix + user ID
     */
    public static final String USER_REPLY_NOTIFICATION_READ_STATUS_PREFIX = "notification:reply:user:unread:";

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


    public static final String COMMENT_COUNT = "COMMENT_COUNT";
    public static final String USER_NAME = "USER_NAME";
    public static final String TAG_ALL = "TAG_ALL";
    public static final String TAG_POPULAR = "TAG_POPULAR";
    public static final String EMPTY_USER_ALL = "EMPTY_USER_ALL";
    public static final String SYSTEM_LOG = "SYSTEM_LOG";
    public static final String BOOKMARK_VISITED = "BOOKMARK_VISITED";
    public static final String TAG_ONE = "TAG_ONE";
}