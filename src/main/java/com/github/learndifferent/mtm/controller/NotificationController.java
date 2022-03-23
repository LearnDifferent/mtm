package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.ReplyNotificationWithMsgDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.query.DelReNotificationRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.NotificationService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
        String currentUsername = (String) StpUtil.getLoginId();
        String notifications = notificationService.getSysNotHtmlAndRecordName(currentUsername);
        return ResultCreator.okResult(notifications);
    }

    /**
     * Get reply / comment notifications
     *
     * @param username user's name who is about to receive notifications
     * @param to       index of the last element of the reply / comment notification list
     * @return {@link ResultVO}<{@link List}<{@link ReplyNotificationWithMsgDTO}>> reply / comment notification list
     * with the result code of {@link ResultCode#SUCCESS}
     * @throws ServiceException {@link NotificationService#getReplyNotifications(String, int)}
     *                          will throw an exception with {@link ResultCode#NO_RESULTS_FOUND}
     *                          if there is no notifications found. If the user is not current user,
     *                          the result code will be {@link ResultCode#PERMISSION_DENIED}
     */
    @GetMapping("/reply")
    public ResultVO<List<ReplyNotificationWithMsgDTO>> getReplyNotifications(@RequestParam("username") String username,
                                                                             @RequestParam(value = "to", defaultValue = "10")
                                                                                     int to) {
        return ResultCreator.okResult(notificationService.getReplyNotifications(username, to));
    }


    /**
     * Count the number of new reply notifications for the current user
     *
     * @return number of new reply notifications
     */
    @GetMapping("/reply/new")
    public ResultVO<Integer> countNewReplyNotifications() {
        String currentUsername = (String) StpUtil.getLoginId();
        return ResultCreator.okResult(notificationService.countNewReplyNotifications(currentUsername));
    }

    /**
     * Delete a reply notification
     *
     * @param requestBody the data of notification to delete
     * @throws ServiceException {@link NotificationService#deleteReplyNotification(DelReNotificationRequest)}
     *                          will throw an exception if the current user has no permission to
     *                          delete the reply notification with the result code of {@link
     *                          ResultCode#PERMISSION_DENIED}
     */
    @PostMapping("/reply/delete")
    public void deleteReplyNotification(@RequestBody DelReNotificationRequest requestBody) {
        notificationService.deleteReplyNotification(requestBody);
    }

    /**
     * Delete system notifications
     *
     * @return {@link ResultCreator#okResult()}
     */
    @SystemLog(title = "Notification", optsType = OptsType.DELETE)
    @DeleteMapping
    public ResultVO<ResultCode> deleteSystemNotifications() {
        notificationService.deleteSystemNotification();
        return ResultCreator.okResult();
    }

    /**
     * Send a system notification
     *
     * @param content the content to send
     * @return {@link ResultCreator#okResult()}
     */
    @SystemLog(title = "Notification", optsType = OptsType.CREATE)
    @GetMapping("send/{content}")
    public ResultVO<ResultCode> sendSystemNotification(@PathVariable String content) {
        notificationService.sendSysNotAndDelSavedNames(content);
        return ResultCreator.okResult();
    }
}