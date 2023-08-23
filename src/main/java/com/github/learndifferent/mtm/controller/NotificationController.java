package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.general.idempotency.IdempotencyCheck;
import com.github.learndifferent.mtm.constant.consist.ErrorInfoConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.ReplyNotificationDTO;
import com.github.learndifferent.mtm.query.DeleteReplyNotificationRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.NotificationService;
import com.github.learndifferent.mtm.vo.ReplyMessageNotificationAndItsReadStatusVO;
import java.util.List;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
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
@Validated
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Get reply notifications
     *
     * @param size size of the reply notification list
     * @return Reply message notification and its read status
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link NotificationService#getReplyNotifications(String,
     *                                                                  int)} will throw an exception with
     *                                                                  {@link ResultCode#NO_RESULTS_FOUND}
     *                                                                  if there is no notifications found
     */
    @GetMapping
    public List<ReplyMessageNotificationAndItsReadStatusVO> getReplyNotifications(
            @RequestParam(value = "size", defaultValue = "10")
            @Positive(message = ErrorInfoConstant.NO_DATA) int size) {

        String username = StpUtil.getLoginIdAsString();
        List<ReplyMessageNotificationAndItsReadStatusVO> replyNotifications = notificationService.getReplyNotifications(
                username, size);
        log.info("replyNotifications: {}", replyNotifications);
        return replyNotifications;
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
    @IdempotencyCheck
    public void deleteReplyNotification(@RequestBody DeleteReplyNotificationRequest data) {
        String currentUsername = StpUtil.getLoginIdAsString();
        notificationService.deleteReplyNotification(data, currentUsername);
    }

    /**
     * Calculate the count of current user's unread replies
     *
     * @return number of unread replies
     */
    @GetMapping("/reply")
    public ResultVO<Long> countUnreadReplies() {
        String currentUsername = StpUtil.getLoginIdAsString();
        long count = notificationService.countUnreadReplies(currentUsername);
        return ResultCreator.okResult(count);
    }

    /**
     * Mark the reply notification as read
     *
     * @param data notification data
     */
    @PostMapping("/reply")
    public void markReplyNotificationAsRead(@RequestBody ReplyNotificationDTO data) {
        notificationService.markReplyNotificationAsRead(data);
    }

    /**
     * Get current user's role change notification
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
     * Delete role change notification for current user
     */
    @DeleteMapping("/role-changed")
    @IdempotencyCheck
    public void deleteRoleChangeNotification() {
        String currentUsername = StpUtil.getLoginIdAsString();
        notificationService.deleteRoleChangeNotification(currentUsername);
    }

    /**
     * Check if the user currently logged in has turned off notifications
     *
     * @return Return {@link ResultCode#SUCCESS} if the user has turned off notifications
     * and {@link ResultCode#FAILED} if the user has turned on notifications
     */
    @GetMapping("/mute")
    public ResultVO<ResultCode> checkIfTurnOffNotifications() {
        String currentUsername = StpUtil.getLoginIdAsString();
        boolean hasTurnedOff = notificationService.checkIfTurnOffNotifications(currentUsername);
        return hasTurnedOff ? ResultCreator.okResult() : ResultCreator.failResult();
    }

    /**
     * Turn on notifications if the current user turned off notifications and
     * turn off notifications if the current user turned on notifications
     */
    @GetMapping("/mute/switch")
    @IdempotencyCheck
    public void turnOnTurnOffNotifications() {
        String currentUsername = StpUtil.getLoginIdAsString();
        notificationService.turnOnTurnOffNotifications(currentUsername);
    }
}