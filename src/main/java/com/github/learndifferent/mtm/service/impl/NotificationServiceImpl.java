package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import com.github.learndifferent.mtm.constant.enums.PriorityLevel;
import com.github.learndifferent.mtm.dto.NotificationDTO;
import com.github.learndifferent.mtm.manager.NotificationManager;
import com.github.learndifferent.mtm.mapper.UserMapper;
import com.github.learndifferent.mtm.service.NotificationService;
import com.github.learndifferent.mtm.utils.ShortenUtils;
import com.github.learndifferent.mtm.vo.NotificationVO;
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
    public long countReplyNotifications(String recipientUsername) {
        Integer recipientUserId = userMapper.getUserIdByUsername(recipientUsername);
        return notificationManager.countReplyNotifications(recipientUserId);
    }

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
    public void markReplyNotificationAsRead(NotificationDTO data) {
        notificationManager.markReplyNotificationAsRead(data);
    }

    @Override
    public void markReplyNotificationAsUnread(NotificationDTO data) {
        notificationManager.markReplyNotificationAsUnread(data);
    }

    @Override
    public List<NotificationVO> getReplyNotifications(String recipientUsername, int loadCount) {
        Integer recipientUserId = userMapper.getUserIdByUsername(recipientUsername);
        return notificationManager.getReplyNotifications(recipientUserId, loadCount);
    }


    @Override
    public void deleteSystemNotifications() {
        // delete all notifications
        notificationManager.deleteByKey(KeyConstant.SYSTEM_NOTIFICATION);
        // delete all saved usernames
        notificationManager.deleteByKey(KeyConstant.SYSTEM_NOTIFICATION_READ_USERS);
    }

    @Override
    public String getSystemNotificationsHtml(String username) {
        return notificationManager.getSystemNotificationsHtml(username);
    }

    @Override
    public void sendSystemNotification(String username, String message, PriorityLevel priority) {
        // shorten the message from user
        String msg = ShortenUtils.flatten(message);
        String m = ShortenUtils.shorten(msg, 30);
        // send notification
        notificationManager.sendSystemNotification(username + ": " + m, priority);
    }

    @Override
    public boolean checkIfReadLatestSysNotification(String username) {
        boolean hasTurnedOffNotification = checkIfTurnOffNotifications(username);
        return hasTurnedOffNotification || notificationManager.checkIfReadLatestSysNotification(username);
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