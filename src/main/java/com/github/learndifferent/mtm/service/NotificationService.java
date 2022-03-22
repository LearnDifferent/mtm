package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.dto.ReplyNotificationWithMsgDTO;
import com.github.learndifferent.mtm.query.DelReNotificationRequest;
import java.util.List;

/**
 * Notification Service
 *
 * @author zhou
 * @date 2021/09/21
 */
public interface NotificationService {

    /**
     * Count the total number of reply notifications
     *
     * @param receiveUsername username
     * @return total number of reply notifications
     */
    long countReplyNotifications(String receiveUsername);

    /**
     * Count the number of new reply notifications
     *
     * @param receiveUsername username
     * @return number of new reply notifications
     */
    int countNewReplyNotifications(String receiveUsername);

    /**
     * Get reply / comment notifications and clear notification count
     *
     * @param receiveUsername user's name who is about to receive notifications
     * @param to              index of the last element of the reply notification list
     * @return {@link List}<{@link ReplyNotificationWithMsgDTO}> reply / comment notification list
     * @throws com.github.learndifferent.mtm.exception.ServiceException If the user is not current user, this method
     *                                                                  will throw an exception with
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  and if no results found, the result code will
     *                                                                  be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#NO_RESULTS_FOUND}.
     */
    List<ReplyNotificationWithMsgDTO> getReplyNotifications(String receiveUsername, int to);


    /**
     * 删除回复通知
     *
     * @param data 需要删除的通知数据
     * @throws com.github.learndifferent.mtm.exception.ServiceException 检查用户的权限并抛出异常
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
