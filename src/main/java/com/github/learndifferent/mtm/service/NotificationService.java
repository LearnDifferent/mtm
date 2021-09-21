package com.github.learndifferent.mtm.service;

/**
 * 操作通知（在 Redis 中）
 *
 * @author zhou
 * @date 2021/09/21
 */
public interface NotificationService {

    /**
     * 删除通知
     *
     * @return true 表示刚刚删除成功，false 之前已经删除了
     */
    Boolean trueMeansDeleteFalseMeansAlreadyDeleted();

    /**
     * 获取前 20 条通知，并转化为 HTML 的形式
     *
     * @return 转化为 HTML 的形式的前 20 条通知
     */
    String getNotificationsHtml();

    /**
     * 发送通知
     *
     * @param content 通知
     */
    void sendNotification(String content);
}
