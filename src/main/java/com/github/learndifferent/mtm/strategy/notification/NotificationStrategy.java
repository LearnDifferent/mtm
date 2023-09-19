package com.github.learndifferent.mtm.strategy.notification;

import com.github.learndifferent.mtm.dto.NotificationDTO;
import com.github.learndifferent.mtm.vo.NotificationsAndCountVO;

/**
 * Notification-related Strategy
 *
 * @author zhou
 * @date 2023/8/24
 */
public interface NotificationStrategy {

    /**
     * Send notification
     *
     * @param notification notification
     */
    void sendNotification(NotificationDTO notification);

    /**
     * Mark the notification as read
     *
     * @param notification notification data
     */
    void markNotificationAsRead(NotificationDTO notification);

    /**
     * Mark the notification as read
     *
     * @param notification notification data
     */
    void markNotificationAsUnread(NotificationDTO notification);

    /**
     * Get all notifications and their count
     *
     * @param recipientUserId User ID
     * @param loadCount       load count
     * @param isOrderReversed true if reverse order
     * @return notifications and count
     */
    NotificationsAndCountVO getAllNotificationsAndCount(Integer recipientUserId,
                                                        int loadCount,
                                                        boolean isOrderReversed);

    /**
     * Count the total number of notifications
     *
     * @param recipientUserId User ID
     *                        <p>When counting the system notifications, the recipient user ID is null</p>
     * @return total number of notifications
     */
    long countAllNotifications(Integer recipientUserId);

    /**
     * Retrieve unread notifications and their count
     *
     * @param recipientUserId User ID
     * @param loadCount       load count
     * @param isOrderReversed true if reverse order
     * @return unread notifications and count
     */
    NotificationsAndCountVO getUnreadNotificationAndCount(long recipientUserId, int loadCount, boolean isOrderReversed);
}