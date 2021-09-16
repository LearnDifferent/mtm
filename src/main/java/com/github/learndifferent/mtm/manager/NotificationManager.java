package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 操作通知（在 Redis 中）
 *
 * @author zhou
 * @date 2021/09/05
 */
@Component
public class NotificationManager {

    private final StringRedisTemplate template;

    @Autowired
    public NotificationManager(StringRedisTemplate template) {
        this.template = template;
    }

    /**
     * 删除通知
     *
     * @return true 表示刚刚删除成功，false 之前已经删除了
     */
    public Boolean trueMeansDeleteFalseMeansAlreadyDeleted() {
        return template.delete(KeyConstant.NOTICE);
    }

    /**
     * 获取前 20 条通知，并转化为 HTML 的形式
     *
     * @return 转化为 HTML 的形式的前 20 条通知
     */
    public String getNotificationsHtml() {

        // 获取 notice 为 key 的所有值
        List<String> msg = getNotificationsFromRedis();

        int size = msg.size();

        if (size == 0) {
            // 如果没有消息，直接返回没消息的文字
            return "No Notifications Yet";
        }

        StringBuilder sb = getHtmlMsg(msg, size);

        return sb.toString();
    }

    @NotNull
    private StringBuilder getHtmlMsg(List<String> msg, int size) {
        // 如果小于 20 就用 List 的 size，如果大于 20，就为前 20 个
        int count = Math.min(size, 20);

        StringBuilder sb = new StringBuilder("Alerting Notifications (" + count + ")：<br>");

        for (int i = 1; i <= count; i++) {
            // 返回格式：<br>1. 消息<br>2. 消息...
            sb.append("<br>").append(i).append(". ").append(msg.get(i - 1));
        }
        return sb;
    }

    /**
     * 获取 Redis 该通知相关 key 内的所有值
     *
     * @return Redis 该通知相关 key 内的所有值
     */
    private List<String> getNotificationsFromRedis() {
        return template.opsForList().range(KeyConstant.NOTICE, 0, -1);
    }

    /**
     * 发送通知
     *
     * @param content 通知
     */
    public void sendNotification(String content) {
        // 将消息存入 key 中
        template.opsForList().leftPush(KeyConstant.NOTICE, content);
        // 确保该 key 只有 20 个 value
        template.opsForList().trim(KeyConstant.NOTICE, 0, 19);
    }
}
