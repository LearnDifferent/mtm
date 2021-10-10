package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.dto.ReplyNotificationWithMsgDTO;
import com.github.learndifferent.mtm.query.DelReNotificationRequest;
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
     * @param receiveUsername user's name who is about to receive notifications
     * @param to              index of the last element of the reply notification list
     * @return {@link List}<{@link ReplyNotificationWithMsgDTO}>
     * @throws com.github.learndifferent.mtm.exception.ServiceException If no results found, it will throw an exception
     *                                                                  with {@link com.github.learndifferent.mtm.constant.enums.ResultCode#NO_RESULTS_FOUND}
     */
    List<ReplyNotificationWithMsgDTO> getReplyNotifications(String receiveUsername, int to);


    /**
     * 删除回复通知
     *
     * @param data 需要删除的通知数据
     */
    void deleteReplyNotification(DelReNotificationRequest data);

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
