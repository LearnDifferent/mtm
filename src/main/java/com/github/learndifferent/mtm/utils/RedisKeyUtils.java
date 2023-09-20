package com.github.learndifferent.mtm.utils;

import com.github.learndifferent.mtm.config.RedisConfigProperties;
import com.github.learndifferent.mtm.constant.consist.RedisConstant;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A class offering utilities for working with Redis keys (and Bitmap offsets)
 *
 * @author zhou
 * @date 2023/8/29
 */
@Component
public class RedisKeyUtils {

    private RedisKeyUtils() {
    }

    private final static Map<String, String> KEY_NAMES = new HashMap<>();

    @Autowired
    public void initKeyNames(RedisConfigProperties configProperties) {
        // get config properties
        Map<String, Map<String, Long>> keyConstantsAndCacheConfigs = configProperties.getKeyConstantsAndCacheConfigs();

        Set<Entry<String, Map<String, Long>>> entries = keyConstantsAndCacheConfigs.entrySet();
        for (Entry<String, Map<String, Long>> entry : entries) {
            // get the key, which is the constant name
            String constantName = entry.getKey();

            // get the Redis Key name:
            // get the value, which is a map
            Map<String, Long> valueMap = entry.getValue();
            // get the key set of the map
            Set<String> valueMapKeys = valueMap.keySet();
            // the Set<String> valueMapKeys contains only one element,
            // so this for each loop will iterate only once
            // put the key and value in KEY_NAMES map
            valueMapKeys.forEach(key -> KEY_NAMES.put(constantName, key));
        }
    }

    public static String getCommentCountKeyPrefix() {
        return KEY_NAMES.get(RedisConstant.COMMENT_COUNT);
    }

    public static String getBookmarkVisitedKeyPrefix() {
        return KEY_NAMES.get(RedisConstant.BOOKMARK_VISITED);
    }

    public static String getTagAllKeyPrefix() {
        return KEY_NAMES.get(RedisConstant.TAG_ALL);
    }

    public static String getTagOneKeyPrefix() {
        return KEY_NAMES.get(RedisConstant.TAG_ONE);
    }

    public static String getUserNameKeyPrefix() {
        return KEY_NAMES.get(RedisConstant.USER_NAME);
    }

    public static String getSystemLogKeyPrefix() {
        return KEY_NAMES.get(RedisConstant.SYSTEM_LOG);
    }

    public static String getEmptyUserAllKeyPrefix() {
        return KEY_NAMES.get(RedisConstant.EMPTY_USER_ALL);
    }

    public static String getTagPopularKeyPrefix() {
        return KEY_NAMES.get(RedisConstant.TAG_POPULAR);
    }

    public static String getReplyNotificationKey(Long recipientUserId) {
        return RedisConstant.USER_REPLY_NOTIFICATIONS_PREFIX + recipientUserId;
    }

    public static String getReplyNotificationReadStatusKey(Long recipientUserId) {
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

    public static long getSysNotificationReadStatusReadByUserOffset(Long userId) {
        return userId;
    }

    public static String getSysNotificationReadStatusTrackNotificationsOfUserKey(Long userId) {
        return RedisConstant.SYS_NOTIFICATION_READ_STATUS_TRACK_NOTIFICATIONS_OF_USER_PREFIX + userId;
    }

    public static long getSysNotificationReadStatusTrackNotificationsOfUserOffset(long notificationId) {
        return notificationId;
    }

    public static String getCurrentIdKey(String bizTag) {
        return RedisConstant.CURRENT_ID_PREFIX + bizTag;
    }
}