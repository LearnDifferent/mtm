package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.ReplyNotificationDTO;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.NotificationService;
import com.github.learndifferent.mtm.utils.ReverseUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        return ResultCreator.okResult(notificationService.getSystemNotificationsHtml());
    }

    /**
     * Get reply notifications
     *
     * @param username user's name who is about to receive notifications
     * @param size     size
     * @return {@link ResultVO}<{@link List}<{@link ReplyNotificationDTO}>> If the user is current user,
     * returns notifications with {@link ResultCode#SUCCESS}.
     * If the user is not current user, returns {@link ResultCode#PERMISSION_DENIED}
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link NotificationService#getReplyNotifications(String,
     *                                                                  int)}
     *                                                                  will throw an exception with
     *                                                                  {@link  ResultCode#NO_RESULTS_FOUND} if there
     *                                                                  is no notifications found
     */
    @GetMapping("/reply")
    public ResultVO<List<ReplyNotificationDTO>> getReplyNotifications(@RequestParam String username,
                                                                      @RequestParam(defaultValue = "10") int size) {
        String currentUsername = (String) StpUtil.getLoginId();
        boolean notCurrentUser = ReverseUtils.stringNotEqualsIgnoreCase(currentUsername, username);
        return notCurrentUser ? ResultCreator.result(ResultCode.PERMISSION_DENIED)
                : ResultCreator.okResult(notificationService.getReplyNotifications(username, size));
    }

    /**
     * Delete system notifications
     *
     * @return {@link ResultVO}<{@link ?}> {@link ResultCreator#okResult()}
     */
    @SystemLog(title = "Notification", optsType = OptsType.DELETE)
    @DeleteMapping
    public ResultVO<?> delSystemNotifications() {
        notificationService.deleteSystemNotification();
        return ResultCreator.okResult();
    }

    /**
     * Send a system notification
     *
     * @param content the content to send
     * @return {@link ResultVO}<{@link ?}> {@link ResultCreator#okResult()}
     */
    @SystemLog(title = "Notification", optsType = OptsType.CREATE)
    @GetMapping("send/{content}")
    public ResultVO<?> sendSystemNotification(@PathVariable String content) {
        notificationService.sendSystemNotification(content);
        return ResultCreator.okResult();
    }
}