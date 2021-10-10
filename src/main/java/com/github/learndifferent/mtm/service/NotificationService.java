package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.dto.ReplyNotificationDTO;
import java.util.List;

/**
 * 操作通知（在 Redis 中）
 *
 * @author zhou
 * @date 2021/09/21
 */
public interface NotificationService {

    /**
     * Count the total number of reply notifications
     *
     * @param receiveUsername 获得用户名
     * @return total number of reply notifications
     */
    long countReplyNotifications(String receiveUsername);

    /**
     * 获取回复的通知
     *
     * @param receiveUsername 接收通知的用户的用户名
     * @param size            接收通知的数量
     * @return {@link List}<{@link ReplyNotificationDTO}>
     * @throws com.github.learndifferent.mtm.exception.ServiceException If no results found, it will throw an exception
     *                                                                  with {@link com.github.learndifferent.mtm.constant.enums.ResultCode#NO_RESULTS_FOUND}
     */
    List<ReplyNotificationDTO> getReplyNotifications(String receiveUsername, int size);

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
