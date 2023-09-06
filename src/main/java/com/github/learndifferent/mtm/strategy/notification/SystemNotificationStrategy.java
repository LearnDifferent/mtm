package com.github.learndifferent.mtm.strategy.notification;

import com.github.learndifferent.mtm.constant.consist.NotificationConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.NotificationDTO;
import com.github.learndifferent.mtm.utils.JsonUtils;
import com.github.learndifferent.mtm.utils.RedisKeyUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.learndifferent.mtm.vo.NotificationVO;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * System Notification
 * <p>
 * [Notice] Read status has two dimensions:
 * one is to store whether a particular notification has been read by a user,
 * and the other is to track the notifications that a specific user has read
 * </p>
 *
 * @author zhou
 * @date 2023/8/24
 */
@Component(NotificationConstant.SYSTEM_NOTIFICATION)
@RequiredArgsConstructor
public class SystemNotificationStrategy implements NotificationStrategy {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void sendNotification(NotificationDTO notification) {
        // push the notification to the list
        String content = JsonUtils.toJson(notification);
        String key = RedisKeyUtils.getSystemNotificationKey();
        redisTemplate.opsForList().leftPush(key, content);
    }

    @Override
    public void markNotificationAsRead(NotificationDTO notification) {
        updateNotificationReadStatus(notification, true);
    }

    @Override
    public void markNotificationAsUnread(NotificationDTO notification) {
        updateNotificationReadStatus(notification, false);
    }

    private void updateNotificationReadStatus(NotificationDTO notification, boolean isRead) {
        long notificationId = notification.getId();
        Integer userId = notification.getRecipientUserId();

        // 1. store whether a particular notification has been read by a user
        String notificationReadStatusKey = RedisKeyUtils.getSysNotificationReadStatusReadByUserKey(notificationId);
        long notificationReadStatusOffset = RedisKeyUtils.getSysNotificationReadStatusReadByUserOffset(userId);
        // key: prefix + notificationId
        // offset: user ID
        // indicate that if user has read the specific notification
        // 0 stands for unread, 1 stand for read
        redisTemplate.opsForValue().setBit(notificationReadStatusKey, notificationReadStatusOffset, isRead);

        // 2. track the notifications that a specific user has read
        String userReadStatusKey = RedisKeyUtils.getSysNotificationReadStatusTrackNotificationsOfUserKey(userId);
        long userReadStatusOffset = RedisKeyUtils
                .getSysNotificationReadStatusTrackNotificationsOfUserOffset(notificationId);
        // 0 stands for unread, 1 stand for read
        redisTemplate.opsForValue().setBit(userReadStatusKey, userReadStatusOffset, isRead);
    }

    @Override
    public List<NotificationVO> getNotifications(Integer recipientUserId, int loadCount, boolean isOrderReversed) {
        return getNotificationsWithoutReadStatus(loadCount, isOrderReversed)
                // set recipientUserId to the notifications
                .peek(notification -> notification.setRecipientUserId(recipientUserId))
                // get the read status and generate the NotificationVO
                .map(this::getReadStatusAndGenerateNotificationVO)
                .collect(Collectors.toList());
    }

    private Stream<NotificationDTO> getNotificationsWithoutReadStatus(int loadCount, boolean isOrderReverse) {
        boolean illegalLoadCount = loadCount <= 0;
        ThrowExceptionUtils.throwIfTrue(illegalLoadCount, ResultCode.NO_RESULTS_FOUND);

        String notificationsKey = RedisKeyUtils.getSystemNotificationKey();
        List<String> notifications = isOrderReverse ? getNotificationsFromNewestToOldest(notificationsKey, loadCount)
                : getNotificationsFromOldestToNewest(notificationsKey, loadCount);

        boolean hasNoResults = CollectionUtils.isEmpty(notifications);
        ThrowExceptionUtils.throwIfTrue(hasNoResults, ResultCode.NO_RESULTS_FOUND);

        return notifications.stream()
                .map(json -> JsonUtils.toObject(json, NotificationDTO.class));
    }

    private List<String> getNotificationsFromNewestToOldest(String notificationsKey, int loadCount) {
        int end = loadCount - 1;
        return redisTemplate.opsForList().range(notificationsKey, 0, end);
    }

    private List<String> getNotificationsFromOldestToNewest(String notificationKey, int loadCount) {
        // get the start
        // e.g. loadCount is 2: `lrange notificationKey -2 -1`
        int start = loadCount * -1;
        List<String> list = redisTemplate.opsForList().range(notificationKey, start, -1);

        // if no results, return empty list
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        // reverse the list for the reason that Redis command is `lrange` (from left to right)
        // and the needed order is from right to left
        Collections.reverse(list);
        return list;
    }

    private NotificationVO getReadStatusAndGenerateNotificationVO(NotificationDTO notification) {
        long id = notification.getId();
        Integer userId = notification.getRecipientUserId();

        String key = RedisKeyUtils.getSysNotificationReadStatusReadByUserKey(id);
        long offset = RedisKeyUtils.getSysNotificationReadStatusReadByUserOffset(userId);

        Boolean result = redisTemplate.opsForValue().getBit(key, offset);
        // the read status will be 'read' if the result is null
        boolean isRead = Optional.ofNullable(result).orElse(true);

        return NotificationVO.of(notification, isRead);
    }
}