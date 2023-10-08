package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.general.idempotency.IdempotencyCheck;
import com.github.learndifferent.mtm.constant.consist.ErrorInfoConstant;
import com.github.learndifferent.mtm.constant.enums.NotificationType;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.NotificationDTO;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.NotificationService;
import com.github.learndifferent.mtm.utils.LoginUtils;
import com.github.learndifferent.mtm.vo.NotificationsAndCountVO;
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
 * Notification Controller
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
     * Get all notifications and their count
     *
     * @param notificationType Notification type
     * @param loadCount        Number of notifications to be loaded
     * @param isOrderReversed  true if reverse order
     * @return Notifications and count
     */
    @GetMapping
    public NotificationsAndCountVO getAllNotificationsAndCount(
            @RequestParam(value = "notificationType") NotificationType notificationType,
            @RequestParam(value = "loadCount", defaultValue = "10")
            @Positive(message = ErrorInfoConstant.NO_DATA) int loadCount,
            @RequestParam("isOrderReversed") boolean isOrderReversed) {

        long currentUserId = LoginUtils.getCurrentUserId();
        return notificationService.getAllNotificationsAndCount(notificationType, currentUserId, loadCount,
                isOrderReversed);
    }

    /**
     * Retrieve unread notifications and their count
     *
     * @param notificationType Notification type
     * @param loadCount        Number of notifications to be loaded
     * @param isOrderReversed  true if reverse order
     * @return Unread notifications and count
     */
    @GetMapping("/unread")
    public NotificationsAndCountVO getUnreadNotificationsAndCount(
            @RequestParam(value = "notificationType") NotificationType notificationType,
            @RequestParam(value = "loadCount", defaultValue = "10")
            @Positive(message = ErrorInfoConstant.NO_DATA) int loadCount,
            @RequestParam("isOrderReversed") boolean isOrderReversed) {

        long currentUserId = LoginUtils.getCurrentUserId();
        return notificationService
                .getUnreadNotificationsAndCount(notificationType, currentUserId, loadCount, isOrderReversed);
    }

    /**
     * Mark the notification as read
     *
     * @param data notification data
     */
    @PostMapping("/read")
    public void markNotificationAsRead(@RequestBody NotificationDTO data) {
        notificationService.markNotificationAsRead(data);
    }

    /**
     * Mark the notification as unread
     *
     * @param data notification data
     */
    @PostMapping("/unread")
    public void markNotificationAsUnread(@RequestBody NotificationDTO data) {
        notificationService.markNotificationAsUnread(data);
    }

    /**
     * Calculate the count of current user's unread replies
     *
     * @return number of unread replies
     */
    @GetMapping("/count/reply")
    public ResultVO<Long> countUnreadReplies() {
        long currentUserId = LoginUtils.getCurrentUserId();
        long count = notificationService.countUnreadReplies(currentUserId);
        return ResultCreator.okResult(count);
    }

    /**
     * Calculate the count of current user's unread replies
     *
     * @return number of unread replies
     */
    @GetMapping("/count/system")
    public ResultVO<Long> countUnreadSystemNotifications() {
        long currentUserId = LoginUtils.getCurrentUserId();
        long count = notificationService.countUnreadSystemNotifications(currentUserId);
        return ResultCreator.okResult(count);
    }

    /**
     * Count the total number of notifications
     *
     * @param notificationType Notification type
     * @return total number of notifications
     */
    @GetMapping("/count/all")
    public long countNotifications(@RequestParam(value = "notificationType") NotificationType notificationType) {
        switch (notificationType) {
            case SYSTEM_NOTIFICATION:
                return notificationService.countAllSystemNotifications();
            case REPLY_NOTIFICATION:
            default:
                long currentUserId = LoginUtils.getCurrentUserId();
                return notificationService.countAllReplyNotifications(currentUserId);
        }
    }

    /**
     * Send system notification
     *
     * @param message notification message
     */
    @GetMapping("/system/send")
    public void sendSystemNotification(@RequestParam("message") String message) {
        String sender = LoginUtils.getCurrentUsername();
        notificationService.sendSystemNotification(sender, message);
    }

    /**
     * Check if the user has unread system notifications
     *
     * @return true if the current user has unread system notifications
     */
    @GetMapping("/system")
    public boolean checkIfHasUnreadSystemNotifications() {
        Long userId = LoginUtils.getCurrentUserId();
        return notificationService.checkIfHasUnreadSysNotifications(userId);
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
        long currentUserId = LoginUtils.getCurrentUserId();
        String notification = notificationService.generateRoleChangeNotification(currentUserId);
        return StringUtils.isEmpty(notification) ? ResultCreator.result(ResultCode.UPDATE_FAILED)
                : ResultCreator.okResult(notification);
    }

    /**
     * Delete role change notification for current user
     */
    @DeleteMapping("/role-changed")
    @IdempotencyCheck
    public void deleteRoleChangeNotification() {
        long currentUserId = LoginUtils.getCurrentUserId();
        notificationService.deleteRoleChangeNotification(currentUserId);
    }

    /**
     * Check if the user currently logged in has turned off notifications
     *
     * @return Return {@link ResultCode#SUCCESS} if the user has turned off notifications
     * and {@link ResultCode#FAILED} if the user has turned on notifications
     */
    @GetMapping("/mute")
    public ResultVO<ResultCode> checkIfTurnOffNotifications() {
        long currentUserId = LoginUtils.getCurrentUserId();
        boolean hasTurnedOff = notificationService.checkIfTurnOffNotifications(currentUserId);
        return hasTurnedOff ? ResultCreator.okResult() : ResultCreator.failResult();
    }

    /**
     * Turn on notifications if the current user turned off notifications and
     * turn off notifications if the current user turned on notifications
     */
    @GetMapping("/mute/switch")
    @IdempotencyCheck
    public void turnOnTurnOffNotifications() {
        long currentUserId = LoginUtils.getCurrentUserId();
        notificationService.turnOnTurnOffNotifications(currentUserId);
    }
}