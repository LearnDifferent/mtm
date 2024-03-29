package com.github.learndifferent.mtm.strategy.notification;

import com.github.learndifferent.mtm.constant.enums.NotificationType;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.NotificationDTO;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.learndifferent.mtm.vo.NotificationsAndCountVO;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Notification-related Strategy
 *
 * @author zhou
 * @date 2023/8/26
 */
@Component
@RequiredArgsConstructor
public class NotificationStrategyContext {

    private final Map<String, NotificationStrategy> strategies;

    private NotificationStrategy checkAndGetStrategy(NotificationType notificationType) {
        String strategyName = notificationType.type();
        return checkAndGetStrategy(strategyName);
    }

    private NotificationStrategy checkAndGetStrategy(String strategyName) {
        boolean hasNoStrategy = !strategies.containsKey(strategyName);
        ThrowExceptionUtils.throwIfTrue(hasNoStrategy, ResultCode.NO_RESULTS_FOUND);
        return strategies.get(strategyName);
    }

    public void sendNotification(NotificationDTO notification) {
        NotificationType notificationType = notification.getNotificationType();
        checkAndGetStrategy(notificationType)
                .sendNotification(notification);
    }


    public void markNotificationAsRead(NotificationDTO notification) {
        NotificationType notificationType = notification.getNotificationType();
        checkAndGetStrategy(notificationType)
                .markNotificationAsRead(notification);
    }

    public void markNotificationAsUnread(NotificationDTO notification) {
        NotificationType notificationType = notification.getNotificationType();
        checkAndGetStrategy(notificationType)
                .markNotificationAsUnread(notification);
    }

    public NotificationsAndCountVO getAllNotificationsAndCount(NotificationType notificationType,
                                                               Long recipientUserId,
                                                               int loadCount,
                                                               boolean isOrderReversed) {
        return checkAndGetStrategy(notificationType)
                .getAllNotificationsAndCount(recipientUserId, loadCount, isOrderReversed);
    }

    public long countAllNotifications(NotificationType notificationType, Long recipientUserId) {
        return checkAndGetStrategy(notificationType)
                .countAllNotifications(recipientUserId);
    }

    public NotificationsAndCountVO getUnreadNotificationsAndCount(NotificationType notificationType,
                                                                  long recipientUserId,
                                                                  int loadCount,
                                                                  boolean isOrderReversed) {
        return checkAndGetStrategy(notificationType)
                .getUnreadNotificationAndCount(recipientUserId, loadCount, isOrderReversed);
    }
}