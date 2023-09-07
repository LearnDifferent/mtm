package com.github.learndifferent.mtm.mapper;

import com.github.learndifferent.mtm.vo.NotificationVO;
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
     * Save user's read or unread system notification
     *
     * @param notification notification
     */
    void saveUserSystemNotification(NotificationVO notification);
}