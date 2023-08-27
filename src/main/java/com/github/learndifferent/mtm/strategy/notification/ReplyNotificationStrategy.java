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
 * Reply Notification
 *
 * @author zhou
 * @date 2023/8/24
 */
@Component(NotificationConstant.REPLY_NOTIFICATION)
@RequiredArgsConstructor
public class ReplyNotificationStrategy implements NotificationStrategy {

    private final StringRedisTemplate redisTemplate;

    @NotNull
    private String getNotificationsKey(Integer userId) {
        return KeyConstant.USER_REPLY_NOTIFICATIONS_PREFIX + userId;
    }

    @NotNull
    private String getReadStatusKey(Integer userId) {
        return KeyConstant.USER_REPLY_NOTIFICATION_READ_STATUS_PREFIX + userId;
    }

    private long getReadStatusOffset(UUID notificationId) {
        return Math.abs(notificationId.getMostSignificantBits());
    }

    @Override
    public void sendNotification(NotificationDTO notification) {
        // push the notification to the list
        Integer userId = notification.getRecipientUserId();
        String key = getNotificationsKey(userId);
        String content = JsonUtils.toJson(notification);
        redisTemplate.opsForList().leftPush(key, content);
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
        Integer userId = notification.getRecipientUserId();
        UUID notificationId = notification.getId();

        // key: prefix + user ID
        // offset: a derived value based on the notification ID
        // indicate the notifications that the user hasn't read yet
        String key = getReadStatusKey(userId);
        long offset = getReadStatusOffset(notificationId);
        redisTemplate.opsForValue().setBit(key, offset, isUnread);
    }

    @Override
    public List<NotificationVO> getNotifications(Integer userId, int loadCount) {
        Stream<NotificationDTO> notifications = getNotificationsWithoutReadStatus(userId, loadCount);
        return notifications
                .map(this::getReadStatusAndGenerateNotificationVO)
                .collect(Collectors.toList());
    }

    private Stream<NotificationDTO> getNotificationsWithoutReadStatus(Integer userId, int loadCount) {
        int end = loadCount - 1;

        boolean illegalEnd = end < 0;
        ThrowExceptionUtils.throwIfTrue(illegalEnd, ResultCode.NO_RESULTS_FOUND);

        String key = getNotificationsKey(userId);
        List<String> list = redisTemplate.opsForList().range(key, 0, end);

        boolean hasNoResults = CollectionUtils.isEmpty(list);
        ThrowExceptionUtils.throwIfTrue(hasNoResults, ResultCode.NO_RESULTS_FOUND);

        return list.stream()
                .map(json -> JsonUtils.toObject(json, NotificationDTO.class));
    }

    private NotificationVO getReadStatusAndGenerateNotificationVO(NotificationDTO notification) {
        Integer userId = notification.getRecipientUserId();
        UUID notificationId = notification.getId();

        String key = getReadStatusKey(userId);
        long offset = getReadStatusOffset(notificationId);

        Boolean result = redisTemplate.opsForValue().getBit(key, offset);
        // the read status will be 'read' if the result is null
        boolean isUnread = Optional.ofNullable(result).orElse(false);

        return NotificationVO.of(notification, !isUnread);
    }

}