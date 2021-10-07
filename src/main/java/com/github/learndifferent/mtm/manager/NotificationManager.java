package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * About notification
 *
 * @author zhou
 * @date 2021/10/7
 */
@Component
public class NotificationManager {

    private final StringRedisTemplate template;

    @Autowired
    public NotificationManager(StringRedisTemplate template) {this.template = template;}

    /**
     * Delete notification
     *
     * @param notificationRedisKey redis key
     */
    public void deleteNotificationByKey(String notificationRedisKey) {
        template.delete(notificationRedisKey);
    }

    /**
     * 发送系统通知
     *
     * @param content 通知内容
     */
    public void sendSystemNotification(String content) {
        // 将消息存入 key 中
        template.opsForList().leftPush(KeyConstant.SYSTEM_NOTIFICATION, content);
        // 确保该 key 只有 20 个 value
        template.opsForList().trim(KeyConstant.SYSTEM_NOTIFICATION, 0, 19);
    }

    /**
     * 获取前 20 条通知，并转化为 HTML 的形式
     *
     * @return 转化为 HTML 的形式的前 20 条通知
     */
    public String getSystemNotificationsHtml() {

        // 获取 notice 为 key 的所有值
        List<String> msg = getSystemNotifications();

        int size = msg.size();

        if (size == 0) {
            // 如果没有消息，直接返回没消息的文字
            return "No Notifications Yet";
        }

        StringBuilder sb = getHtmlMsg(msg, size);

        return sb.toString();
    }

    /**
     * 获取系统的通知
     *
     * @return Redis 该通知相关 key 内的所有值
     */
    private List<String> getSystemNotifications() {
        return template.opsForList().range(KeyConstant.SYSTEM_NOTIFICATION, 0, -1);
    }

    private StringBuilder getHtmlMsg(List<String> msg, int size) {
        // 如果小于 20 就用 List 的 size，如果大于 20，就为前 20 个
        int count = Math.min(size, 20);

        StringBuilder sb = new StringBuilder("Alerting System Notifications (" + count + ")：<br>");

        for (int i = 1; i <= count; i++) {
            // 返回格式：<br>1. 消息<br>2. 消息...
            sb.append("<br>").append(i).append(". ").append(msg.get(i - 1));
        }
        return sb;
    }
}
