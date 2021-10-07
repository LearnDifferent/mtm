package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
     * Get notifications
     *
     * @return {@link ResultVO}<{@link String}> notifications
     */
    @SystemLog(title = "Notification", optsType = OptsType.READ)
    @GetMapping
    public ResultVO<String> getNotifications() {
        return ResultCreator.okResult(notificationService.getSystemNotificationsHtml());
    }

    /**
     * Delete notifications
     *
     * @return {@link ResultVO}<{@link ?}> {@link ResultCreator#okResult()}
     */
    @SystemLog(title = "Notification", optsType = OptsType.DELETE)
    @DeleteMapping
    public ResultVO<?> delNotifications() {
        notificationService.deleteSystemNotification();
        return ResultCreator.okResult();
    }

    /**
     * Send a notification
     *
     * @param content the content to send
     * @return {@link ResultVO}<{@link ?}> {@link ResultCreator#okResult()}
     */
    @SystemLog(title = "Notification", optsType = OptsType.CREATE)
    @GetMapping("/{content}")
    public ResultVO<?> sendNotification(@PathVariable String content) {
        notificationService.sendSystemNotification(content);
        return ResultCreator.okResult();
    }
}
