package com.github.learndifferent.mtm.strategy.notification;

import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import com.github.learndifferent.mtm.constant.consist.NotificationConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.NotificationDTO;
import com.github.learndifferent.mtm.utils.JsonUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.learndifferent.mtm.vo.NotificationVO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * System Notification
 *
 * @author zhou
 * @date 2023/8/24
 */
@Component(NotificationConstant.SYSTEM_NOTIFICATION)
@RequiredArgsConstructor
public class SystemNotificationStrategy implements NotificationStrategy {

    private final StringRedisTemplate redisTemplate;

    @NotNull
    private String getNotificationsKey() {
        return KeyConstant.SYSTEM_NOTIFICATIONS;
    }

    @NotNull
    private String getReadStatusKey(UUID notificationId) {
        return KeyConstant.SYSTEM_NOTIFICATION_READ_STATUS_PREFIX + notificationId;
    }

    private long getReadStatusOffset(Integer userId) {
        return userId;
    }

    @Override
    public void sendNotification(NotificationDTO notification) {
        // push the notification to the list
        String content = JsonUtils.toJson(notification);
        redisTemplate.opsForList().leftPush(getNotificationsKey(), content);
        // mark it as unread
        markNotificationAsUnread(notification);
    }

    @Override
    public void markNotificationAsRead(NotificationDTO notification) {
        updateNotificationReadStatus(notification, false);
    }

    @Override
    public void markNotificationAsUnread(NotificationDTO notification) {
        updateNotificationReadStatus(notification, true);
    }

    private void updateNotificationReadStatus(NotificationDTO notification, boolean isUnread) {
        UUID notificationId = notification.getId();
        Integer userId = notification.getRecipientUserId();

        String readStatusKey = getReadStatusKey(notificationId);
        long readStatusOffset = getReadStatusOffset(userId);
        // key: prefix + notificationId
        // offset: user ID
        // indicate that if user has read the specific notification
        redisTemplate.opsForValue().setBit(readStatusKey, readStatusOffset, isUnread);
    }

    @Override
    public List<NotificationVO> getNotifications(Integer recipientUserId, int loadCount) {
        return getNotificationsWithoutReadStatus(loadCount)
                .map(this::getReadStatusAndGenerateNotificationVO)
                .collect(Collectors.toList());
    }

    private Stream<NotificationDTO> getNotificationsWithoutReadStatus(int loadCount) {
        int end = loadCount - 1;

        boolean illegalEnd = end < 0;
        ThrowExceptionUtils.throwIfTrue(illegalEnd, ResultCode.NO_RESULTS_FOUND);

        String notificationsKey = getNotificationsKey();
        List<String> list = redisTemplate.opsForList().range(notificationsKey, 0, end);

        boolean hasNoResults = CollectionUtils.isEmpty(list);
        ThrowExceptionUtils.throwIfTrue(hasNoResults, ResultCode.NO_RESULTS_FOUND);

        return list.stream()
                .map(json -> JsonUtils.toObject(json, NotificationDTO.class));
    }

    private NotificationVO getReadStatusAndGenerateNotificationVO(NotificationDTO notification) {
        UUID id = notification.getId();
        Integer userId = notification.getRecipientUserId();

        String key = getReadStatusKey(id);
        long offset = getReadStatusOffset(userId);

        Boolean result = redisTemplate.opsForValue().getBit(key, offset);
        // the read status will be 'read' if the result is null
        boolean isUnread = Optional.ofNullable(result).orElse(false);

        return NotificationVO.of(notification, !isUnread);
    }
}