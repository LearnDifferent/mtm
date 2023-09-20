package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.constant.enums.NotificationType;
import com.github.learndifferent.mtm.dto.NotificationDTO;
import com.github.learndifferent.mtm.manager.NotificationManager;
import com.github.learndifferent.mtm.service.NotificationService;
import com.github.learndifferent.mtm.vo.NotificationsAndCountVO;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Notification Service Implementation
 *
 * @author zhou
 * @date 2021/09/21
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationManager notificationManager;

    @Override
    public long countUnreadReplies(Long recipientUserId) {
        boolean hasTurnedOff = checkIfTurnOffNotifications(recipientUserId);
        return hasTurnedOff ? 0L : countUnreadRepliesWhenTurnOnNotification(recipientUserId);
    }

    private long countUnreadRepliesWhenTurnOnNotification(Long recipientUserId) {
        return notificationManager.countUnreadReplies(recipientUserId);
    }

    @Override
    public long countUnreadSystemNotifications(Long recipientUserId) {
        boolean hasTurnedOff = checkIfTurnOffNotifications(recipientUserId);
        return hasTurnedOff ? 0L : countUnreadSysNotificationsWhenTurnOnNotification(recipientUserId);
    }

    private long countUnreadSysNotificationsWhenTurnOnNotification(Long recipientUserId) {
        return notificationManager.countUnreadSystemNotifications(recipientUserId);
    }

    @Override
    public void markNotificationAsRead(NotificationDTO data) {
        notificationManager.markNotificationAsRead(data);
    }

    @Override
    public void markNotificationAsUnread(NotificationDTO data) {
        notificationManager.markNotificationAsUnread(data);
    }

    @Override
    public NotificationsAndCountVO getAllNotificationsAndCount(NotificationType notificationType,
                                                               Long recipientUserId,
                                                               int loadCount,
                                                               boolean isOrderReversed) {
        return notificationManager
                .getAllNotificationsAndCount(notificationType, recipientUserId, loadCount, isOrderReversed);
    }

    @Override
    public NotificationsAndCountVO getUnreadNotificationsAndCount(NotificationType notificationType,
                                                                  Long recipientUserId,
                                                                  int loadCount,
                                                                  boolean isOrderReversed) {
        return notificationManager
                .getUnreadNotificationsAndCount(notificationType, recipientUserId, loadCount, isOrderReversed);
    }

    @Override
    public void sendSystemNotification(String sender, String message) {
        notificationManager.sendSystemNotification(sender, message);
    }

    @Override
    public boolean checkIfHasUnreadSysNotifications(Long recipientUserId) {
        return notificationManager.checkIfUserHasUnreadSysNotifications(recipientUserId);
    }

    @Override
    public long countAllReplyNotifications(Long recipientUserId) {
        return notificationManager.countAllReplyNotifications(recipientUserId);
    }

    @Override
    public long countAllSystemNotifications() {
        return notificationManager.countAllSystemNotifications();
    }

    @Override
    public String generateRoleChangeNotification(Long userId) {

        return Optional.ofNullable(userId)
                // generate a user role change notification by user ID
                .map(notificationManager::generateRoleChangeNotification)
                // return empty string if there is no user with that username
                .orElse("");
    }

    @Override
    public void deleteRoleChangeNotification(Long userId) {
        if (Objects.isNull(userId) || userId <= 0) {
            return;
        }
        notificationManager.deleteRoleChangeNotification(userId);
    }

    @Override
    public boolean checkIfTurnOffNotifications(Long userId) {
        return notificationManager.checkIfTurnOffNotifications(userId);
    }

    @Override
    public void turnOnTurnOffNotifications(Long userId) {
        notificationManager.turnOnTurnOffNotifications(userId);
    }
}