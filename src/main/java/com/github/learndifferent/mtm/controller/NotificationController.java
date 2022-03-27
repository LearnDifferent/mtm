package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.ReplyNotificationWithMsgDTO;
import com.github.learndifferent.mtm.query.DelReNotificationRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.NotificationService;
import com.github.learndifferent.mtm.utils.CompareStringUtil;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Get, delete and send notifications
 *
 * @author zhou
 * @date 2021/09/05
 */
@RestController
@RequestMapping("/notify")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Get system notifications
     *
     * @return {@link ResultVO}<{@link String}> notifications
     */
    @SystemLog(title = "Notification", optsType = OptsType.READ)
    @GetMapping
    public ResultVO<String> getSystemNotifications() {
        String currentUsername = getCurrentUsername();
        String notifications = notificationService.getSysNotHtmlAndRecordName(currentUsername);
        return ResultCreator.okResult(notifications);
    }

    /**
     * Get reply / comment notifications
     *
     * @param lastIndex index of the last element of the reply / comment notification list
     * @return {@link ResultVO}<{@link List}<{@link ReplyNotificationWithMsgDTO}>> reply / comment notification list
     * with the result code of {@link ResultCode#SUCCESS}
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link NotificationService#getReplyNotifications(String,
     *                                                                  int)} will throw an exception with
     *                                                                  {@link ResultCode#NO_RESULTS_FOUND}
     *                                                                  if there is no notifications found
     */
    @GetMapping("/reply")
    public ResultVO<List<ReplyNotificationWithMsgDTO>> getReplyNotifications(
            @RequestParam(value = "lastIndex", defaultValue = "10") int lastIndex) {

        String username = getCurrentUsername();
        List<ReplyNotificationWithMsgDTO> n = notificationService.getReplyNotifications(username, lastIndex);
        return ResultCreator.okResult(n);
    }

    /**
     * Count the number of new reply notifications for the current user
     *
     * @return number of new reply notifications
     */
    @GetMapping("/reply/new")
    public ResultVO<Integer> countNewReplyNotifications() {
        String currentUsername = getCurrentUsername();
        int count = notificationService.countNewReplyNotifications(currentUsername);
        return ResultCreator.okResult(count);
    }

    /**
     * Delete a reply notification
     *
     * @param data the data of notification to delete
     * @throws com.github.learndifferent.mtm.exception.ServiceException throw an exception with the result code of
     *                                                                  {@link ResultCode#PERMISSION_DENIED} if
     *                                                                  the user that is currently logged in is not
     *                                                                  the owner of the notification to delete
     */
    @PostMapping("/reply/delete")
    public void deleteReplyNotification(@RequestBody DelReNotificationRequest data) {
        // check the permission
        checkPermission(data);
        // delete notification
        notificationService.deleteReplyNotification(data);
    }

    private void checkPermission(DelReNotificationRequest data) {
        String receiveUsername = data.getReceiveUsername();
        String currentUsername = getCurrentUsername();
        boolean notCurrentUser = CompareStringUtil.notEqualsIgnoreCase(receiveUsername, currentUsername);
        ThrowExceptionUtils.throwIfTrue(notCurrentUser, ResultCode.PERMISSION_DENIED);
    }

    /**
     * Delete system notifications
     *
     * @return {@link ResultCreator#okResult()}
     */
    @SystemLog(title = "Notification", optsType = OptsType.DELETE)
    @DeleteMapping
    public ResultVO<ResultCode> deleteSystemNotifications() {
        notificationService.deleteSysNotificationAndSavedNames();
        return ResultCreator.okResult();
    }

    /**
     * Send a system notification
     *
     * @param content the content to send
     * @return {@link ResultCreator#okResult()}
     */
    @SystemLog(title = "Notification", optsType = OptsType.CREATE)
    @GetMapping("/send/{content}")
    public ResultVO<ResultCode> sendSystemNotification(@PathVariable String content) {
        notificationService.sendSysNotAndDelSavedNames(content);
        return ResultCreator.okResult();
    }

    /**
     * Check whether the current user has read the latest system notification
     *
     * @return true if current user has read the latest notification, or there is no system notification
     * <p>false if current user has not read the latest notification</p>
     */
    @GetMapping("/read")
    public ResultVO<Boolean> checkIfReadLatestSysNotification() {
        String currentUsername = getCurrentUsername();
        boolean hasRead = notificationService.checkIfReadLatestSysNotification(currentUsername);
        return ResultCreator.okResult(hasRead);
    }

    /**
     * Get Current User's Role Change Notification
     *
     * @return Return {@link ResultCreator#okResult(Object)} with the notification.
     * <p>
     * Return {@link ResultVO} with the result code of {@link ResultCode#UPDATE_FAILED}
     * if the notification is empty, which means the user role is not changed.
     * </p>
     */
    @GetMapping("/role-changed")
    public ResultVO<String> getRoleChangeNotification() {
        String currentUsername = getCurrentUsername();
        String notification = notificationService.generateRoleChangeNotification(currentUsername);
        return StringUtils.isEmpty(notification) ? ResultCreator.result(ResultCode.UPDATE_FAILED)
                : ResultCreator.okResult(notification);
    }

    /**
     * Delete Role Change Notification for Current User
     */
    @DeleteMapping("/role-changed")
    public void deleteRoleChangeNotification() {
        String currentUsername = getCurrentUsername();
        notificationService.deleteRoleChangeNotification(currentUsername);
    }

    private String getCurrentUsername() {
        return StpUtil.getLoginIdAsString();
    }
}