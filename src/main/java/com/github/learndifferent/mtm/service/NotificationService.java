package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.constant.enums.NotificationType;
import com.github.learndifferent.mtm.constant.enums.PriorityLevel;
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
     * Calculate the count of current user's unread replies
     *
     * @param recipientUsername username
     * @return Return the number of unread replies
     * <p>If the user turned off notifications, return 0</p>
     */
    long countUnreadReplies(String recipientUsername);

    /**
     * Mark the reply notification as read
     *
     * @param data notification data
     */
    void markReplyNotificationAsRead(NotificationDTO data);

    /**
     * Mark the reply notification as unread
     *
     * @param data notification data
     */
    void markReplyNotificationAsUnread(NotificationDTO data);

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
     * Delete all system notifications and remove all saved usernames of users
     * stored in the cache that read the most recent previous system notifications
     */
    void deleteSystemNotifications();

    /**
     * Get first 20 system notifications and convert the text to an HTML format.
     * <p>Username of the user who read the latest system notifications
     * will be stored in the cache.</p>
     *
     * @param username username of the user who wants to get the latest system notifications
     * @return first 20 system notifications
     */
    String getSystemNotificationsHtml(String username);

    /**
     * Send a system notification and ensure the limit is 20.
     * <p>
     * This will also delete all saved usernames of users who
     * read the most recent previous system notifications,
     * which are stored in the cache, to make this notification a push notification,
     * if the priority level is {@link PriorityLevel#URGENT}.
     * </p>
     *
     * @param username username of the user who is sending the notification
     * @param message  the message to send
     * @param priority priority level of the notification
     */
    void sendSystemNotification(String username, String message, PriorityLevel priority);

    /**
     * Check whether the user has read the latest system notification
     *
     * @param username username of the user
     * @return Return true if user has read the latest notification, or there is no system notification.
     * If the user turned off the notifications, return true as well.
     * <p>Return false if user has not read the latest notification</p>
     */
    boolean checkIfReadLatestSysNotification(String username);

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