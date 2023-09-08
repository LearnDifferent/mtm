package com.github.learndifferent.mtm.utils;

import com.github.learndifferent.mtm.constant.consist.RedisConstant;

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
        return RedisConstant.USER_REPLY_NOTIFICATIONS_PREFIX + recipientUserId;
    }

    public static String getReplyNotificationReadStatusKey(Integer recipientUserId) {
        return RedisConstant.USER_REPLY_NOTIFICATION_READ_STATUS_PREFIX + recipientUserId;
    }

    public static long getReplyNotificationReadStatusOffset(long notificationId) {
        return notificationId;
    }

    public static String getSystemNotificationKey() {
        return RedisConstant.SYSTEM_NOTIFICATIONS;
    }

    public static String getSysNotificationReadStatusReadByUserKey(Long notificationId) {
        return RedisConstant.SYS_NOTIFICATION_READ_STATUS_READ_BY_USER_PREFIX + notificationId;
    }

    public static long getSysNotificationReadStatusReadByUserOffset(Integer userId) {
        return userId;
    }

    public static String getSysNotificationReadStatusTrackNotificationsOfUserKey(Integer userId) {
        return RedisConstant.SYS_NOTIFICATION_READ_STATUS_TRACK_NOTIFICATIONS_OF_USER_PREFIX + userId;
    }

    public static long getSysNotificationReadStatusTrackNotificationsOfUserOffset(long notificationId) {
        return notificationId;
    }

    public static String getCurrentId(String bizTag) {
        return RedisConstant.CURRENT_ID_PREFIX + bizTag;
    }
}