package com.github.learndifferent.mtm.mapper;

import com.github.learndifferent.mtm.dto.NotificationDTO;
import com.github.learndifferent.mtm.vo.NotificationVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Notification Mapper
 *
 * @author zhou
 * @date 2023/9/7
 */
@Repository
public interface NotificationMapper {

    /**
     * Save reply notification to database
     *
     * @param notification notification
     */
    void saveReplyNotification(NotificationVO notification);

    /**
     * Save system notification to database
     *
     * @param notification notification
     */
    void saveSystemNotification(NotificationDTO notification);

    /**
     * Save user's read or unread system notification
     *
     * @param notification notification
     */
    void saveUserSystemNotification(NotificationVO notification);

    /**
     * Update the read status of reply notification
     *
     * @param isRead read status
     * @param id     reply notification ID
     */
    void updateReplyNotificationReadStatus(@Param("isRead") boolean isRead, @Param("id") long id);
}