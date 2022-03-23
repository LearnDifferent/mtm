package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.ReplyNotificationDTO;
import com.github.learndifferent.mtm.dto.ReplyNotificationWithMsgDTO;
import com.github.learndifferent.mtm.entity.CommentDO;
import com.github.learndifferent.mtm.entity.WebsiteDO;
import com.github.learndifferent.mtm.mapper.CommentMapper;
import com.github.learndifferent.mtm.mapper.WebsiteMapper;
import com.github.learndifferent.mtm.utils.JsonUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Notification Manager
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
     * Delete all notifications of {@code notificationRedisKey}
     *
     * @param notificationRedisKey redis key
     */
    public void deleteNotificationByKey(String notificationRedisKey) {
        redisTemplate.delete(notificationRedisKey);
    }

    public long countReplyNotifications(String receiveUsername) {
        String key = KeyConstant.REPLY_NOTIFICATION_PREFIX + receiveUsername.toLowerCase();
        Long size = redisTemplate.opsForList().size(key);
        return Optional.ofNullable(size).orElse(0L);
    }

    public int countNewReplyNotifications(String receiveUsername) {
        String key = KeyConstant.REPLY_NOTIFICATION_COUNT_PREFIX + receiveUsername.toLowerCase();
        String notificationCount = redisTemplate.opsForValue().get(key);
        String count = Optional.ofNullable(notificationCount).orElse("0");
        try {
            return Integer.parseInt(count);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Get reply notifications and clear notification count
     *
     * @param receiveUsername username
     * @param from            from
     * @param to              to
     * @return reply notifications
     */
    public List<ReplyNotificationWithMsgDTO> getReplyNotifications(String receiveUsername, int from, int to) {
        String key = KeyConstant.REPLY_NOTIFICATION_PREFIX + receiveUsername.toLowerCase();
        List<String> notifications = redisTemplate.opsForList().range(key, from, to);

        boolean hasNoNotifications = CollectionUtils.isEmpty(notifications);
        ThrowExceptionUtils.throwIfTrue(hasNoNotifications, ResultCode.NO_RESULTS_FOUND);

        // clear notification count
        redisTemplate.delete(KeyConstant.REPLY_NOTIFICATION_COUNT_PREFIX + receiveUsername.toLowerCase());

        return notifications.stream()
                .map(n -> {
                    ReplyNotificationWithMsgDTO no = JsonUtils.toObject(n, ReplyNotificationWithMsgDTO.class);
                    String text = getCommentTextIfWebsiteAndCommentExist(no);
                    no.setMessage(text);
                    return no;
                })
                .collect(Collectors.toList());
    }

    private String getCommentTextIfWebsiteAndCommentExist(ReplyNotificationWithMsgDTO notification) {

        Integer webId = notification.getWebId();
        // include private website data because another method
        // that views the details will verify the permission later on
        WebsiteDO web = websiteMapper.getWebsiteDataById(webId);

        if (web == null) {
            // if the website data does not exist,
            // returns null to indicate that the comment does not exist
            return null;
        }

        int commentId = notification.getCommentId();
        // the result is null if the comment does not exist
        return commentMapper.getCommentTextById(commentId);
    }

    /**
     * Send reply notification and increase notification count
     *
     * @param comment comment / reply to send
     */
    public void sendReplyNotification(CommentDO comment) {

        ReplyNotificationDTO notification = getReplyNotificationDTO(comment);

        String receiveUsername = notification.getReceiveUsername();
        String key = KeyConstant.REPLY_NOTIFICATION_PREFIX + receiveUsername.toLowerCase();

        String value = JsonUtils.toJson(notification);
        redisTemplate.opsForList().leftPush(key, value);

        // increase notification count
        redisTemplate.opsForValue().increment(
                KeyConstant.REPLY_NOTIFICATION_COUNT_PREFIX + receiveUsername.toLowerCase());
    }

    private ReplyNotificationDTO getReplyNotificationDTO(CommentDO comment) {

        Integer commentId = comment.getCommentId();
        Integer webId = comment.getWebId();
        Integer replyToCommentId = comment.getReplyToCommentId();

        // the notification belongs to the owner of the website data if replyToCommentId is null,
        // and belongs to the owner of the comment data if it's not null
        boolean notifyWebsiteOwner = replyToCommentId == null;
        String receiveUsername;

        if (notifyWebsiteOwner) {
            receiveUsername = websiteMapper.getUsernameByWebId(webId);
        } else {
            receiveUsername = commentMapper.getUsernameByCommentId(replyToCommentId);
        }

        String sendUsername = comment.getUsername();
        Instant creationTime = comment.getCreationTime();

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
     * Send System Notification and ensure the limit is 20
     *
     * @param content content of notification
     */
    public void sendSystemNotification(String content) {
        redisTemplate.opsForList().leftPush(KeyConstant.SYSTEM_NOTIFICATION, content);
        redisTemplate.opsForList().trim(KeyConstant.SYSTEM_NOTIFICATION, 0, 19);
    }

    /**
     * Get first 20 system notifications and convert the text to an HTML format.
     * <p>This will also record the username of the user who wants to get the latest system notifications</p>
     *
     * @param username username of the user who wants to get the latest system notifications
     * @return first 20 system notifications
     */
    public String getSysNotHtmlAndRecordName(String username) {

        // get first 20 messages
        List<String> messages = redisTemplate.opsForList().range(KeyConstant.SYSTEM_NOTIFICATION, 0, 19);

        if (CollectionUtils.isEmpty(messages)) {
            return "No Notifications Yet";
        }

        StringBuilder sb = getHtmlMsg(messages);

        // record the lowercase username
        redisTemplate.opsForSet().add(KeyConstant.SYSTEM_NOTIFICATION_READ_USER, username.toLowerCase());
        return sb.toString();
    }

    private StringBuilder getHtmlMsg(List<String> msg) {

        int size = msg.size();
        int count = Math.min(size, 20);

        StringBuilder sb = new StringBuilder("Alerting System Notifications (" + count + ")：<br>");

        for (int i = 1; i <= count; i++) {
            // format：<br>1. first message <br>2. message .........
            sb.append("<br>").append(i).append(". ").append(msg.get(i - 1));
        }
        return sb;
    }
}
