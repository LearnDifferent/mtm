package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.manager.NotificationManager;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 发送和接受消息
 *
 * @author zhou
 * @date 2021/09/05
 */
@RestController
@RequestMapping("/notify")
public class NotificationController {

    private final NotificationManager notificationManager;

    @Autowired
    public NotificationController(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    @SystemLog(title = "Notification", optsType = OptsType.READ)
    @GetMapping
    public ResultVO<String> getNotifications() {
        return ResultCreator.okResult(notificationManager.getNotificationsHtml());
    }

    @SystemLog(title = "Notification", optsType = OptsType.DELETE)
    @DeleteMapping
    public ResultVO<Boolean> delNotifications() {
        Boolean deleteOrAlreadyDeleted = notificationManager
                .trueMeansDeleteFalseMeansAlreadyDeleted();
        return ResultCreator.okResult(deleteOrAlreadyDeleted);
    }

    @SystemLog(title = "Notification", optsType = OptsType.CREATE)
    @GetMapping("/{content}")
    public ResultVO<?> sendNotification(@PathVariable String content) {
        notificationManager.sendNotification(content);
        return ResultCreator.okResult();
    }
}
