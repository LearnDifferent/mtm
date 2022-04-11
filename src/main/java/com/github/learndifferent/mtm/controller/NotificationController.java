package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.query.DeleteReplyNotificationRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.NotificationService;
import com.github.learndifferent.mtm.vo.ReplyMessageNotificationVO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Get reply notifications
     *
     * @param lastIndex index of the last element of the reply notification list
     * @return reply notifications
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link NotificationService#getReplyNotifications(String,
     *                                                                  int)} will throw an exception with
     *                                                                  {@link ResultCode#NO_RESULTS_FOUND}
     *                                                                  if there is no notifications found
     */
    @GetMapping
    public List<ReplyMessageNotificationVO> getReplyNotifications(
            @RequestParam(value = "lastIndex", defaultValue = "10") int lastIndex) {

        String username = StpUtil.getLoginIdAsString();
        return notificationService.getReplyNotifications(username, lastIndex);
    }

    /**
     * Delete a reply notification
     *
     * @param data Request body that contains the data of the reply notification to delete
     * @throws com.github.learndifferent.mtm.exception.ServiceException throw an exception with the result code of
     *                                                                  {@link ResultCode#PERMISSION_DENIED} if
     *                                                                  the user that is currently logged in is not
     *                                                                  the owner of the notification to delete
     */
    @PostMapping
    public void deleteReplyNotification(@RequestBody DeleteReplyNotificationRequest data) {
        String currentUsername = StpUtil.getLoginIdAsString();
        notificationService.deleteReplyNotification(data, currentUsername);
    }

    /**
     * Count the number of new reply notifications for the current user
     *
     * @return number of new reply notifications
     */
    @GetMapping("/count")
    public ResultVO<Integer> countNewReplyNotifications() {
        String currentUsername = StpUtil.getLoginIdAsString();
        int count = notificationService.countNewReplyNotifications(currentUsername);
        return ResultCreator.okResult(count);
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
        String currentUsername = StpUtil.getLoginIdAsString();
        String notification = notificationService.generateRoleChangeNotification(currentUsername);
        return StringUtils.isEmpty(notification) ? ResultCreator.result(ResultCode.UPDATE_FAILED)
                : ResultCreator.okResult(notification);
    }

    /**
     * Delete Role Change Notification for Current User
     */
    @DeleteMapping("/role-changed")
    public void deleteRoleChangeNotification() {
        String currentUsername = StpUtil.getLoginIdAsString();
        notificationService.deleteRoleChangeNotification(currentUsername);
    }
}