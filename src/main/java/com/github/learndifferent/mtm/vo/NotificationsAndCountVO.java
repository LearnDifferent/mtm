package com.github.learndifferent.mtm.vo;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Notifications and count
 *
 * @author zhou
 * @date 2023/9/16
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationsAndCountVO implements Serializable {

    public static NotificationsAndCountVO of(List<NotificationVO> notifications, long count) {
        return of(notifications, Math.toIntExact(count));
    }

    public static NotificationsAndCountVO of(List<NotificationVO> notifications, int count) {
        return new NotificationsAndCountVO(notifications, count);
    }

    public static NotificationsAndCountVO empty() {
        return new NotificationsAndCountVO(Collections.emptyList(), 0);
    }

    /**
     * Notifications
     */
    private List<NotificationVO> notifications;

    /**
     * Notification count
     */
    private Integer count;

    private static final long serialVersionUID = 1L;
}