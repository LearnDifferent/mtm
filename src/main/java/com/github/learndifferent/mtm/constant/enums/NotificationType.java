package com.github.learndifferent.mtm.constant.enums;

import com.github.learndifferent.mtm.constant.consist.NotificationConstant;

/**
 * Notification Type
 *
 * @author zhou
 * @date 2023/8/24
 */
public enum NotificationType {
    /**
     * Reply or comment notification
     */
    REPLY_NOTIFICATION(NotificationConstant.REPLY_NOTIFICATION),
    /**
     * System notification
     */
    SYSTEM_NOTIFICATION(NotificationConstant.SYSTEM_NOTIFICATION);

    private final String type;

    NotificationType(final String type) {
        this.type = type;
    }

    public String type() {
        return this.type;
    }
}