package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.constant.enums.NotificationType;
import com.github.learndifferent.mtm.dto.NotificationDTO;
import com.github.learndifferent.mtm.vo.NotificationsAndCountVO;

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
     * @param recipientUserId user ID
     * @return Return the number of unread replies
     * <p>If the user turned off notifications, return 0</p>
     */
    long countUnreadReplies(Long recipientUserId);

    /**
     * Calculate the count of user's unread system notifications
     *
     * @param recipientUserId user ID
     * @return Return the number of unread system notifications
     * <p>If the user turned off notifications, return 0</p>
     */
    long countUnreadSystemNotifications(Long recipientUserId);

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
     * Get all notifications and their count
     *
     * @param notificationType Notification type
     * @param recipientUserId  User ID of the recipient who will receive the notifications
     * @param loadCount        Number of notifications to be loaded
     * @param isOrderReversed  true if reverse order
     * @return Notifications and count
     */
    NotificationsAndCountVO getAllNotificationsAndCount(NotificationType notificationType,
                                                        Long recipientUserId,
                                                        int loadCount,
                                                        boolean isOrderReversed);

    /**
     * Retrieve unread notifications and their count
     *
     * @param notificationType Notification type
     * @param recipientUserId  User ID of the recipient who will receive the notifications
     * @param loadCount        Number of notifications to be loaded
     * @param isOrderReversed  true if reverse order
     * @return Unread notifications and count
     */
    NotificationsAndCountVO getUnreadNotificationsAndCount(NotificationType notificationType,
                                                           Long recipientUserId,
                                                           int loadCount,
                                                           boolean isOrderReversed);

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
     * @param recipientUserId user ID
     * @return true if the user has unread system notifications
     */
    boolean checkIfHasUnreadSysNotifications(Long recipientUserId);

    /**
     * Count the total number of reply notifications
     *
     * @param recipientUserId user ID
     * @return total number of reply notifications
     */
    long countAllReplyNotifications(Long recipientUserId);

    /**
     * Count the total number of system notifications
     *
     * @return total number of system notifications
     */
    long countAllSystemNotifications();

    /**
     * Generate a User Role Change Notification
     *
     * @param userId User ID of the user
     * @return User Role Change Notification
     * (It will be an empty string if the user role is not changed)
     */
    String generateRoleChangeNotification(Long userId);

    /**
     * Delete Role Change Notification for the User
     *
     * @param userId User ID of the user
     */
    void deleteRoleChangeNotification(Long userId);

    /**
     * Check if the user has turned off notifications
     *
     * @param userId User ID
     * @return true if the user has turned off notifications
     */
    boolean checkIfTurnOffNotifications(Long userId);

    /**
     * Turn on notifications if the user turned off notifications and
     * turn off notifications if the user turned on notifications
     *
     * @param userId user ID of the user who wants to turn on/off notifications
     */
    void turnOnTurnOffNotifications(Long userId);
}