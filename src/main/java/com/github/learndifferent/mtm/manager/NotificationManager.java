package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.ReplyNotificationDTO;
import com.github.learndifferent.mtm.dto.ReplyNotificationWithMsgDTO;
import com.github.learndifferent.mtm.entity.CommentDO;
import com.github.learndifferent.mtm.mapper.CommentMapper;
import com.github.learndifferent.mtm.mapper.WebsiteMapper;
import com.github.learndifferent.mtm.utils.JsonUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * About notification
 *
 * @author zhou
 * @date 2021/10/7
 */
@Component
public class NotificationManager {

    private final StringRedisTemplate redisTemplate;
    private final CommentMapper commentMapper;
    private final WebsiteMapper websiteMapper;

    @Autowired
    public NotificationManager(StringRedisTemplate redisTemplate,
                               CommentMapper commentMapper,
                               WebsiteMapper websiteMapper) {
        this.redisTemplate = redisTemplate;
        this.commentMapper = commentMapper;
        this.websiteMapper = websiteMapper;
    }

    /**
     * Delete all notifications
     *
     * @param notificationRedisKey redis key
     */
    public void deleteNotificationByKey(String notificationRedisKey) {
        redisTemplate.delete(notificationRedisKey);
    }

    public long countReplyNotifications(String receiveUsername) {
        String key = KeyConstant.REPLY_NOTIFICATION_PREFIX + receiveUsername.toLowerCase();
        Long size = redisTemplate.opsForList().size(key);
        return size == null ? 0 : size;
    }

    public List<ReplyNotificationWithMsgDTO> getReplyNotifications(String receiveUsername, int from, int to) {
        String key = KeyConstant.REPLY_NOTIFICATION_PREFIX + receiveUsername.toLowerCase();
        List<String> notifications = redisTemplate.opsForList().range(key, from, to);

        boolean hasNoNotifications = CollectionUtils.isEmpty(notifications);
        ThrowExceptionUtils.throwIfTrue(hasNoNotifications, ResultCode.NO_RESULTS_FOUND);

        return notifications.stream()
                .map(n -> {
                    ReplyNotificationWithMsgDTO no = JsonUtils.toObject(n, ReplyNotificationWithMsgDTO.class);
                    int commentId = no.getCommentId();
                    // the text is null if the comment does not exist
                    String text = commentMapper.getCommentTextById(commentId);
                    no.setMessage(text);
                    return no;
                })
                .collect(Collectors.toList());
    }

    public void sendReplyNotification(CommentDO comment) {

        ReplyNotificationDTO notification = getReplyNotificationDTO(comment);

        String receiveUsername = notification.getReceiveUsername();
        String key = KeyConstant.REPLY_NOTIFICATION_PREFIX + receiveUsername.toLowerCase();

        String value = JsonUtils.toJson(notification);
        redisTemplate.opsForList().leftPush(key, value);
    }

    private ReplyNotificationDTO getReplyNotificationDTO(CommentDO comment) {

        int commentId = comment.getCommentId();
        int webId = comment.getWebId();
        Integer replyToCommentId = comment.getReplyToCommentId();

        // 如果 replyToCommentId 为空，就提醒 webId 的所有者，否则，提醒 replyToCommentId 的所有者
        boolean notifyWebsiteOwner = replyToCommentId == null;
        String receiveUsername;

        if (notifyWebsiteOwner) {
            receiveUsername = websiteMapper.getUsernameByWebId(webId);
        } else {
            receiveUsername = commentMapper.getUsernameByCommentId(replyToCommentId);
        }

        String sendUsername = comment.getUsername();
        Date creationTime = comment.getCreationTime();

        return ReplyNotificationDTO.builder()
                .creationTime(creationTime)
                .receiveUsername(receiveUsername)
                .sendUsername(sendUsername)
                .commentId(commentId)
                .webId(webId)
                .replyToCommentId(replyToCommentId)
                .build();
    }

    public void deleteReplyNotification(ReplyNotificationDTO data) {
        String receiveUsername = data.getReceiveUsername();
        String key = KeyConstant.REPLY_NOTIFICATION_PREFIX + receiveUsername.toLowerCase();
        String value = JsonUtils.toJson(data);
        redisTemplate.opsForList().remove(key, 1, value);
    }

    /**
     * 发送系统通知
     *
     * @param content 通知内容
     */
    public void sendSystemNotification(String content) {
        // 将消息存入 key 中
        redisTemplate.opsForList().leftPush(KeyConstant.SYSTEM_NOTIFICATION, content);
        // 确保该 key 只有 20 个 value
        redisTemplate.opsForList().trim(KeyConstant.SYSTEM_NOTIFICATION, 0, 19);
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
        return redisTemplate.opsForList().range(KeyConstant.SYSTEM_NOTIFICATION, 0, -1);
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
