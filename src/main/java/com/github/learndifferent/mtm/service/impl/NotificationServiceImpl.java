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
 * Notification Service Implementation
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
    public int countNewReplyNotifications(String receiveUsername) {
        return notificationManager.countNewReplyNotifications(receiveUsername);
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
    public void deleteSysNotificationAndSavedNames() {
        // delete all notifications
        notificationManager.deleteByKey(KeyConstant.SYSTEM_NOTIFICATION);
        // delete all saved usernames
        notificationManager.deleteByKey(KeyConstant.SYSTEM_NOTIFICATION_READ_USERS);
    }

    @Override
    public String getSysNotHtmlAndRecordName(String username) {
        return notificationManager.getSysNotHtmlAndRecordName(username);
    }

    @Override
    public void sendSysNotAndDelSavedNames(String content) {
        // send notification
        notificationManager.sendSystemNotification(content);
        // delete all saved usernames
        notificationManager.deleteByKey(KeyConstant.SYSTEM_NOTIFICATION_READ_USERS);
    }

    @Override
    public boolean checkIfReadLatestSysNotification(String username) {
        return notificationManager.checkIfReadLatestSysNotification(username);
    }
}
