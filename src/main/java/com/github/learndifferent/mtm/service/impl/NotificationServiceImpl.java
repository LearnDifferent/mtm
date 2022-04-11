package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.manager.NotificationManager;
import com.github.learndifferent.mtm.mapper.UserMapper;
import com.github.learndifferent.mtm.query.DeleteReplyNotificationRequest;
import com.github.learndifferent.mtm.service.NotificationService;
import com.github.learndifferent.mtm.utils.CustomStringUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.learndifferent.mtm.vo.ReplyMessageNotificationVO;
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
    private final UserMapper userMapper;

    @Autowired
    public NotificationServiceImpl(NotificationManager notificationManager,
                                   UserMapper userMapper) {
        this.notificationManager = notificationManager;
        this.userMapper = userMapper;
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
    public List<ReplyMessageNotificationVO> getReplyNotifications(String receiveUsername, int lastIndex) {
        return notificationManager.getReplyMessageNotification(receiveUsername, 0, lastIndex);
    }

    @Override
    public void deleteReplyNotification(DeleteReplyNotificationRequest data, String username) {
        String receiveUsername = data.getReceiveUsername();
        boolean notOwner = CustomStringUtils.notEqualsIgnoreCase(receiveUsername, username);
        ThrowExceptionUtils.throwIfTrue(notOwner, ResultCode.PERMISSION_DENIED);

        notificationManager.deleteReplyNotification(data);
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

    @Override
    public String generateRoleChangeNotification(String username) {
        String userId = userMapper.getUserIdByName(username);

        if (userId == null) {
            // return empty string if there is no user with that username
            return "";
        }
        return notificationManager.generateRoleChangeNotification(userId);
    }

    @Override
    public void deleteRoleChangeNotification(String username) {
        String userId = userMapper.getUserIdByName(username);
        if (userId == null) {
            return;
        }
        notificationManager.deleteRoleChangeNotification(userId);
    }
}