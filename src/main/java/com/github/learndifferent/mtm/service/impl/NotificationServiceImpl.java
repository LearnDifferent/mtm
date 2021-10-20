package com.github.learndifferent.mtm.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.ReplyNotificationDTO;
import com.github.learndifferent.mtm.dto.ReplyNotificationWithMsgDTO;
import com.github.learndifferent.mtm.manager.NotificationManager;
import com.github.learndifferent.mtm.query.DelReNotificationRequest;
import com.github.learndifferent.mtm.service.NotificationService;
import com.github.learndifferent.mtm.utils.CompareStringUtil;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.util.List;
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
    public long countReplyNotifications(String receiveUsername) {
        return notificationManager.countReplyNotifications(receiveUsername);
    }

    @Override
    public List<ReplyNotificationWithMsgDTO> getReplyNotifications(String receiveUsername, int to) {
        boolean notCurrentUser = checkIfNotCurrentUser(receiveUsername);
        ThrowExceptionUtils.throwIfTrue(notCurrentUser, ResultCode.PERMISSION_DENIED);
        return notificationManager.getReplyNotifications(receiveUsername, 0, to);
    }

    @Override
    public void deleteReplyNotification(DelReNotificationRequest data) {
        String receiveUsername = data.getReceiveUsername();
        boolean notCurrentUser = checkIfNotCurrentUser(receiveUsername);
        ThrowExceptionUtils.throwIfTrue(notCurrentUser, ResultCode.PERMISSION_DENIED);

        ReplyNotificationDTO replyNotificationDTO = DozerUtils.convert(data, ReplyNotificationDTO.class);
        notificationManager.deleteReplyNotification(replyNotificationDTO);
    }

    /**
     * Check if the {@code username} is not current user's username
     *
     * @param username username
     * @return true if the username is not current user's username
     */
    private boolean checkIfNotCurrentUser(String username) {
        String currentUsername = (String) StpUtil.getLoginId();
        return CompareStringUtil.notEqualsIgnoreCase(currentUsername, username);
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
