package com.github.learndifferent.mtm.utils;

import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import java.util.UUID;

/**
 * A class offering utilities for working with Redis keys (and Bitmap offsets)
 *
 * @author zhou
 * @date 2023/8/29
 */
public class RedisKeyUtils {

    private RedisKeyUtils() {
    }

    public static String getReplyNotificationKey(Integer recipientUserId) {
        return KeyConstant.USER_REPLY_NOTIFICATIONS_PREFIX + recipientUserId;
    }

    public static String getReplyNotificationReadStatusKey(Integer recipientUserId) {
        return KeyConstant.USER_REPLY_NOTIFICATION_READ_STATUS_PREFIX + recipientUserId;
    }

    public static long getReplyNotificationReadStatusOffset(UUID notificationId) {
        return Math.abs(notificationId.hashCode());
    }

    public static String getSystemNotificationKey() {
        return KeyConstant.SYSTEM_NOTIFICATIONS;
    }

    public static String getSystemNotificationReadStatusKey(UUID notificationId) {
        return KeyConstant.SYSTEM_NOTIFICATION_READ_STATUS_PREFIX + notificationId;
    }

    public static long getSystemNotificationReadStatusOffset(Integer userId) {
        return userId;
    }
}
