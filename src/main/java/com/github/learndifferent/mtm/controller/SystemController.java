package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.annotation.validation.user.role.admin.AdminValidation;
import com.github.learndifferent.mtm.annotation.validation.user.role.guest.NotGuest;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.constant.enums.PageInfoParam;
import com.github.learndifferent.mtm.constant.enums.PriorityLevel;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.entity.SysLog;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.NotificationService;
import com.github.learndifferent.mtm.service.SystemLogService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * System Controller
 *
 * @author zhou
 * @date 2022/4/11
 */
@RestController
@RequestMapping("/system")
public class SystemController {

    private final SystemLogService logService;
    private final NotificationService notificationService;

    @Autowired
    public SystemController(SystemLogService logService,
                            NotificationService notificationService) {
        this.logService = logService;
        this.notificationService = notificationService;
    }

    /**
     * Get system notifications
     *
     * @return {@link ResultVO}<{@link String}> notifications
     */
    @GetMapping
    @SystemLog(title = "Notification", optsType = OptsType.READ)
    public ResultVO<String> getSystemNotifications() {
        String currentUsername = StpUtil.getLoginIdAsString();
        String notifications = notificationService.getSystemNotificationsHtml(currentUsername);
        return ResultCreator.okResult(notifications);
    }

    /**
     * Send a system notification when the user is admin
     * <p>
     * The notification will be a push notification if the {@code priority} is 0
     * </p>
     *
     * @param message  the message to send
     * @param priority 0 if the message has the highest priority
     * @return {@link ResultCode#SUCCESS} if success
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link AdminValidation} annotation
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link ResultCode#PERMISSION_DENIED}
     *                                                                  if the user is not admin
     */
    @GetMapping("/send")
    @AdminValidation
    @SystemLog(title = "Notification", optsType = OptsType.CREATE)
    public ResultVO<ResultCode> sendSystemNotification(@RequestParam("message") String message,
                                                       @RequestParam(value = "priority", required = false)
                                                               Integer priority) {
        // the message has the highest priority only when the priority is 0
        boolean isHighestPriority = Integer.valueOf(0).equals(priority);
        PriorityLevel priorityLevel = isHighestPriority ? PriorityLevel.URGENT : PriorityLevel.LOW;

        // send the message
        String currentUsername = StpUtil.getLoginIdAsString();
        notificationService.sendSystemNotification(currentUsername, message, priorityLevel);
        return ResultCreator.okResult();
    }

    /**
     * Delete system notifications
     *
     * @return {@link ResultCode#SUCCESS} if success
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link AdminValidation} annotation
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link ResultCode#PERMISSION_DENIED}
     *                                                                  if the user is not admin
     */
    @DeleteMapping
    @AdminValidation
    @SystemLog(title = "Notification", optsType = OptsType.DELETE)
    public ResultVO<ResultCode> deleteSystemNotifications() {
        notificationService.deleteSystemNotifications();
        return ResultCreator.okResult();
    }

    /**
     * Check whether the current user has read the latest system notification
     *
     * @return {@link ResultCode#SUCCESS} if current user has read the latest notification or there is no system notification.
     * {@link ResultCode#FAILED} if current user has not read the latest notification.
     */
    @GetMapping("/read")
    public ResultVO<ResultCode> checkIfReadLatestSysNotification() {
        String currentUsername = StpUtil.getLoginIdAsString();
        boolean hasRead = notificationService.checkIfReadLatestSysNotification(currentUsername);
        return hasRead ? ResultCreator.okResult() : ResultCreator.failResult();
    }

    /**
     * Get system logs from cache and database
     *
     * @param pageInfo pagination information
     * @return system logs
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link AdminValidation} annotation
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link ResultCode#PERMISSION_DENIED}
     *                                                                  if the user is not admin
     */
    @GetMapping("/logs")
    @AdminValidation
    public ResultVO<List<SysLog>> getSystemLogs(
            @PageInfo(size = 20, paramName = PageInfoParam.CURRENT_PAGE) PageInfoDTO pageInfo) {

        List<SysLog> logs = logService.getSystemLogs(pageInfo);
        return ResultCreator.okResult(logs);
    }

    /**
     * Get system logs from database directly
     *
     * @param pageInfo pagination information
     * @return system logs
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link AdminValidation} annotation
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link ResultCode#PERMISSION_DENIED}
     *                                                                  if the user is not admin
     */
    @GetMapping("/logs/no-cache")
    @AdminValidation
    public ResultVO<List<SysLog>> getSystemLogsFromDatabaseDirectly(
            @PageInfo(size = 20, paramName = PageInfoParam.CURRENT_PAGE) PageInfoDTO pageInfo) {

        List<SysLog> logs = logService.getSystemLogsFromDatabaseDirectly(pageInfo);
        return ResultCreator.okResult(logs);
    }
}