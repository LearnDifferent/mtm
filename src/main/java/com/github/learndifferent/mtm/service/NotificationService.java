package com.github.learndifferent.mtm.service;

/**
 * 操作通知（在 Redis 中）
 *
 * @author zhou
 * @date 2021/09/21
 */
public interface NotificationService {

    /**
     * 删除系统通知
     */
    void deleteSystemNotification();

    /**
     * 获取前 20 条通知，并转化为 HTML 的形式
     *
     * @return 转化为 HTML 的形式的前 20 条通知
     */
    String getSystemNotificationsHtml();

    /**
     * 发送通知
     *
     * @param content 通知
     */
    void sendSystemNotification(String content);
}
