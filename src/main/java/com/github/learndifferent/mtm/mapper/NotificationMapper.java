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
     * Save system notification to database
     *
     * @param notification notification
     */
    void saveSystemNotification(NotificationVO notification);

    /**
     * Save reply notification to database
     *
     * @param notification notification
     */
    void saveReplyNotification(NotificationVO notification);
}