package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.constant.enums.NotificationType;
import com.github.learndifferent.mtm.dto.NotificationDTO;
import com.github.learndifferent.mtm.manager.NotificationManager;
import com.github.learndifferent.mtm.mapper.UserMapper;
import com.github.learndifferent.mtm.service.NotificationService;
import com.github.learndifferent.mtm.vo.NotificationVO;
import com.github.learndifferent.mtm.vo.NotificationsAndCountVO;
import java.util.List;
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
    private final UserMapper userMapper;

    @Override
    public long countUnreadReplies(String recipientUsername) {
        boolean hasTurnedOff = checkIfTurnOffNotifications(recipientUsername);
        return hasTurnedOff ? 0L : countUnreadRepliesWhenTurnOnNotification(recipientUsername);
    }

    private long countUnreadRepliesWhenTurnOnNotification(String recipientUsername) {
        Integer userId = userMapper.getUserIdByUsername(recipientUsername);
        return notificationManager.countUnreadReplies(userId);
    }

    @Override
    public long countUnreadSystemNotifications(String recipientUsername) {
        boolean hasTurnedOff = checkIfTurnOffNotifications(recipientUsername);
        return hasTurnedOff ? 0L : countUnreadSysNotificationsWhenTurnOnNotification(recipientUsername);
    }

    private long countUnreadSysNotificationsWhenTurnOnNotification(String recipientUsername) {
        Integer userId = userMapper.getUserIdByUsername(recipientUsername);
        return notificationManager.countUnreadSystemNotifications(userId);
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
    public List<NotificationVO> getNotifications(NotificationType notificationType,
                                                 String recipientUsername,
                                                 int loadCount,
                                                 boolean isOrderReversed) {
        Integer recipientUserId = userMapper.getUserIdByUsername(recipientUsername);
        return notificationManager.getNotifications(notificationType, recipientUserId, loadCount, isOrderReversed);
    }

    @Override
    public NotificationsAndCountVO getUnreadNotificationsAndCount(NotificationType notificationType,
                                                                  String recipientUsername,
                                                                  int loadCount,
                                                                  boolean isOrderReversed) {
        Integer recipientUserId = userMapper.getUserIdByUsername(recipientUsername);
        return notificationManager
                .getUnreadNotificationsAndCount(notificationType, recipientUserId, loadCount, isOrderReversed);
    }

    @Override
    public void sendSystemNotification(String sender, String message) {
        notificationManager.sendSystemNotification(sender, message);
    }

    @Override
    public boolean checkIfHasUnreadSysNotifications(String recipientUsername) {
        Integer recipientUserId = userMapper.getUserIdByUsername(recipientUsername);
        return notificationManager.checkIfUserHasUnreadSysNotifications(recipientUserId);
    }

    @Override
    public long countAllReplyNotifications(String recipientUsername) {
        Integer recipientUserId = userMapper.getUserIdByUsername(recipientUsername);
        return notificationManager.countAllReplyNotifications(recipientUserId);
    }

    @Override
    public long countAllSystemNotifications() {
        return notificationManager.countAllSystemNotifications();
    }

    @Override
    public String generateRoleChangeNotification(String username) {
        Integer userId = userMapper.getUserIdByUsername(username);

        return Optional.ofNullable(userId)
                // generate a user role change notification by user ID
                .map(notificationManager::generateRoleChangeNotification)
                // return empty string if there is no user with that username
                .orElse("");
    }

    @Override
    public void deleteRoleChangeNotification(String username) {
        Integer userId = userMapper.getUserIdByUsername(username);
        if (Objects.isNull(userId) || userId <= 0) {
            return;
        }
        notificationManager.deleteRoleChangeNotification(userId);
    }

    @Override
    public boolean checkIfTurnOffNotifications(String username) {
        return notificationManager.checkIfTurnOffNotifications(username);
    }

    @Override
    public void turnOnTurnOffNotifications(String username) {
        notificationManager.turnOnTurnOffNotifications(username);
    }
}