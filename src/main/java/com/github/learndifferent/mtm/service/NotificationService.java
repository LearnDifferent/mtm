package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.constant.enums.NotificationType;
import com.github.learndifferent.mtm.dto.NotificationDTO;
import com.github.learndifferent.mtm.vo.NotificationVO;
import java.util.List;

/**
 * Notification Service
 *
 * @author zhou
 * @date 2021/09/21
 */
public interface NotificationService {

    /**
     * Calculate the count of user's unread replies
     *
     * @param recipientUsername username
     * @return Return the number of unread replies
     * <p>If the user turned off notifications, return 0</p>
     */
    long countUnreadReplies(String recipientUsername);

    /**
     * Calculate the count of user's unread system notifications
     *
     * @param recipientUsername username
     * @return Return the number of unread system notifications
     * <p>If the user turned off notifications, return 0</p>
     */
    long countUnreadSystemNotifications(String recipientUsername);

    /**
     * Mark the notification as read
     *
     * @param data notification data
     */
    void markNotificationAsRead(NotificationDTO data);

    /**
     * Mark the notification as unread
     *
     * @param data notification data
     */
    void markNotificationAsUnread(NotificationDTO data);

    /**
     * Get notifications
     *
     * @param notificationType  Notification type
     * @param recipientUsername Username of the recipient who will receive the notifications
     * @param loadCount         Number of notifications to be loaded
     * @return List of notifications
     * @throws com.github.learndifferent.mtm.exception.ServiceException If no results found, this will throw an
     *                                                                  exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#NO_RESULTS_FOUND}.
     */
    List<NotificationVO> getNotifications(NotificationType notificationType, String recipientUsername, int loadCount);

    /**
     * Send system notification
     *
     * @param sender  the sender of the system notification
     * @param message message
     */
    void sendSystemNotification(String sender, String message);

    /**
     * Check if the user has unread system notifications
     *
     * @param recipientUsername username
     * @return true if the user has unread system notifications
     */
    boolean checkIfHasUnreadSysNotifications(String recipientUsername);

    /**
     * Count the total number of reply notifications
     *
     * @param recipientUsername username
     * @return total number of reply notifications
     */
    long countAllReplyNotifications(String recipientUsername);

    /**
     * Count the total number of system notifications
     *
     * @return total number of system notifications
     */
    long countAllSystemNotifications();

    /**
     * Generate a User Role Change Notification
     *
     * @param username Username of the user
     * @return User Role Change Notification
     * (It will be an empty string if the user role is not changed)
     */
    String generateRoleChangeNotification(String username);

    /**
     * Delete Role Change Notification for the User
     *
     * @param username Username of the user
     */
    void deleteRoleChangeNotification(String username);

    /**
     * Check if the user has turned off notifications
     *
     * @param username username
     * @return true if the user has turned off notifications
     */
    boolean checkIfTurnOffNotifications(String username);

    /**
     * Turn on notifications if the user turned off notifications and
     * turn off notifications if the user turned on notifications
     *
     * @param username username of the user who wants to turn on/off notifications
     */
    void turnOnTurnOffNotifications(String username);
}