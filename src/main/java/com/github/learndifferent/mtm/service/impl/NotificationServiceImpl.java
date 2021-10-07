package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import com.github.learndifferent.mtm.manager.NotificationManager;
import com.github.learndifferent.mtm.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 操作通知（在 Redis 中）
 *
 * @author zhou
 * @date 2021/09/21
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationManager notificationManager;

    @Autowired
    public NotificationServiceImpl(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    @Override
    public void deleteSystemNotification() {
        notificationManager.deleteNotificationByKey(KeyConstant.SYSTEM_NOTIFICATION);
    }

    /**
     * 获取前 20 条通知，并转化为 HTML 的形式
     */
    @Override
    public String getSystemNotificationsHtml() {
        return notificationManager.getSystemNotificationsHtml();
    }

    /**
     * 发送通知
     *
     * @param content 通知
     */
    @Override
    public void sendSystemNotification(String content) {
        notificationManager.sendSystemNotification(content);
    }
}
