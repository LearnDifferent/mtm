package com.github.learndifferent.mtm.strategy.notification;

import com.github.learndifferent.mtm.dto.NotificationDTO;
import com.github.learndifferent.mtm.vo.NotificationVO;
import java.util.List;

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
     * Get notifications
     *
     * @param recipientUserId User ID
     * @param loadCount       load count
     * @return notifications
     */
    List<NotificationVO> getNotifications(Integer recipientUserId, int loadCount);
}