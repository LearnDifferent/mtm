package com.github.learndifferent.mtm.manager;

import static com.github.learndifferent.mtm.constant.enums.UserRole.ADMIN;
import static com.github.learndifferent.mtm.constant.enums.UserRole.USER;

import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import com.github.learndifferent.mtm.constant.enums.NotificationType;
import com.github.learndifferent.mtm.constant.enums.PriorityLevel;
import com.github.learndifferent.mtm.constant.enums.UserRole;
import com.github.learndifferent.mtm.dto.NotificationDTO;
import com.github.learndifferent.mtm.dto.ReplyNotificationDTO;
import com.github.learndifferent.mtm.entity.CommentDO;
import com.github.learndifferent.mtm.query.DeleteReplyNotificationRequest;
import com.github.learndifferent.mtm.strategy.notification.NotificationStrategyContext;
import com.github.learndifferent.mtm.utils.JsonUtils;
import com.github.learndifferent.mtm.vo.NotificationVO;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Notification Manager
 *
 * @author zhou
 * @date 2021/10/7
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationManager {

    private final StringRedisTemplate redisTemplate;
    private final NotificationStrategyContext notificationStrategyContext;

    public void sendReplyNotification(CommentDO comment) {
        Integer commentId = comment.getId();
        Integer bookmarkId = comment.getBookmarkId();
        Integer replyToCommentId = comment.getReplyToCommentId();
        String commentMessage = comment.getComment();

        String sendUsername = comment.getUsername();
        Instant creationTime = comment.getCreationTime();

        NotificationDTO notification = NotificationDTO.ofNewReplyNotification(
                sendUsername, commentMessage, creationTime, commentId, bookmarkId, replyToCommentId);
        notificationStrategyContext.sendNotification(notification);
    }

    public List<NotificationVO> getReplyNotifications(Integer recipientUserId, int loadCount) {
        return notificationStrategyContext.getNotifications(
                NotificationType.REPLY_NOTIFICATION, recipientUserId, loadCount);
    }

    /**
     * Delete {@code key}
     *
     * @param key redis key
     */
    public void deleteByKey(String key) {
        this.redisTemplate.delete(key);
    }

    public long countReplyNotifications(String receiveUsername) {
        String key = KeyConstant.REPLY_NOTIFICATION_PREFIX + receiveUsername.toLowerCase();
        Long size = this.redisTemplate.opsForList().size(key);
        return Optional.ofNullable(size).orElse(0L);
    }

    public long countUnreadReplies(Integer recipientUserId) {
        String key = KeyConstant.USER_REPLY_NOTIFICATION_READ_STATUS_PREFIX + recipientUserId;

        Long notificationCount = redisTemplate.execute(
                (RedisCallback<Long>) connection -> connection.bitCount(key.getBytes()));

        return Optional.ofNullable(notificationCount).orElse(0L);
    }

    public void markReplyNotification(ReplyNotificationDTO data, boolean isUnread) {
        String receiveUsername = data.getReceiveUsername();
        String key = KeyConstant.USER_REPLY_TO_READ + receiveUsername.toLowerCase();

        // use the hashcode of the ReplyNotificationDTO as the offset
        int offset = Math.abs(data.hashCode());

        // set the value to 'true' to indicate the reply has NOT been read
        // set the value to 'false' to indicate the reply has been read
        this.redisTemplate.opsForValue().setBit(key, offset, isUnread);
    }

    public void deleteReplyNotification(DeleteReplyNotificationRequest data) {
        String receiveUsername = data.getReceiveUsername();
        String key = KeyConstant.REPLY_NOTIFICATION_PREFIX + receiveUsername.toLowerCase();
        String value = JsonUtils.toJson(data);
        this.redisTemplate.opsForList().remove(key, 1, value);
    }

    /**
     * Send System Notification and ensure the limit is 20
     *
     * @param message  the message to send
     * @param priority priority level
     */
    public void sendSystemNotification(String message, PriorityLevel priority) {
        if (PriorityLevel.URGENT.equals(priority)) {
            // if this is an urgent message
            // delete all saved usernames to make it a push notification
            this.deleteByKey(KeyConstant.SYSTEM_NOTIFICATION_READ_USERS);
            // add <span style='color: #e84a5f'> </span> to the message
            message = "<span style='color: #e84a5f'>" + message + "</span>";
        }

        this.redisTemplate.opsForList().leftPush(KeyConstant.SYSTEM_NOTIFICATION, message);
        this.redisTemplate.opsForList().trim(KeyConstant.SYSTEM_NOTIFICATION, 0, 19);
    }

    /**
     * Get first 20 system notifications and convert the text to an HTML format.
     * <p>Username of the user who read the latest system notifications
     * will be stored in the cache.</p>
     *
     * @param username username of the user who wants to get the latest system notifications
     * @return first 20 system notifications
     */
    public String getSystemNotificationsHtml(String username) {

        List<String> messages = getFirst20SystemNotifications();

        boolean isMessageEmpty = CollectionUtils.isEmpty(messages);

        return isMessageEmpty ? "No Notifications Yet"
                : this.getSystemNotificationsHtml(username, messages);
    }

    private String getSystemNotificationsHtml(String username, List<String> messages) {
        StringBuilder sb = getHtmlMsg(messages);

        // record the lowercase username
        this.redisTemplate.opsForSet().add(KeyConstant.SYSTEM_NOTIFICATION_READ_USERS, username.toLowerCase());
        return sb.toString();
    }

    private List<String> getFirst20SystemNotifications() {
        // get first 20 messages
        return this.redisTemplate.opsForList().range(KeyConstant.SYSTEM_NOTIFICATION, 0, 19);
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

    /**
     * Remove the username from the set of users who have read the most recent previous system notification
     *
     * @param username The username of the user who has read the most recent previous system notification
     */
    public void deleteFromReadSysNot(String username) {
        this.redisTemplate.opsForSet().remove(KeyConstant.SYSTEM_NOTIFICATION_READ_USERS, username.toLowerCase());
    }

    /**
     * Check whether the user has read the latest system notification
     *
     * @param username username of the user
     * @return true if user has read the latest notification, or there is no system notification
     * <p>false if user has not read the latest notification</p>
     */
    public boolean checkIfReadLatestSysNotification(String username) {

        // return true if there is no notification
        List<String> notifications = getFirst20SystemNotifications();
        if (CollectionUtils.isEmpty(notifications)) {
            return true;
        }

        // return true if user has read the latest notification
        Boolean isMember = this.redisTemplate.opsForSet()
                .isMember(KeyConstant.SYSTEM_NOTIFICATION_READ_USERS, username.toLowerCase());

        return Optional.ofNullable(isMember).orElse(false);
    }

    /**
     * Asynchronously log user role changes
     *
     * @param id         the ID of the user
     * @param formerRole the former role of the user
     * @param newRole    the new role that the user has been assigned to
     */
    @Async("asyncTaskExecutor")
    public void logRoleChangesAsync(int id, UserRole formerRole, UserRole newRole) {
        if (formerRole.equals(newRole)) {
            return;
        }

        String key = KeyConstant.ROLE_CHANGE_RECORD_PREFIX + id;
        // put new role
        this.redisTemplate.opsForHash().put(key, KeyConstant.NEW_ROLE_CHANGE_RECORD_HASH_KEY, newRole.role());
        // put former role if absent: only record the first role
        this.redisTemplate.opsForHash().putIfAbsent(key, KeyConstant.FORMER_ROLE_CHANGE_RECORD_HASH_KEY,
                formerRole.role());
    }

    /**
     * Generate a User Role Change Notification
     *
     * @param userId ID of the user
     * @return return the notification or an empty string if the user role is not changed
     */
    public String generateRoleChangeNotification(Integer userId) {
        String key = KeyConstant.ROLE_CHANGE_RECORD_PREFIX + userId;

        Object newRoleObject = this.redisTemplate.opsForHash().get(key, KeyConstant.NEW_ROLE_CHANGE_RECORD_HASH_KEY);
        if (Objects.isNull(newRoleObject)) {
            return "";
        }

        Object formerRoleObject =
                this.redisTemplate.opsForHash().get(key, KeyConstant.FORMER_ROLE_CHANGE_RECORD_HASH_KEY);
        if (Objects.isNull(formerRoleObject)) {
            return "";
        }

        String newRoleString = String.valueOf(newRoleObject);
        String formerRoleString = String.valueOf(formerRoleObject);

        try {
            UserRole newRole = UserRole.valueOf(newRoleString.toUpperCase());
            UserRole formerRole = UserRole.valueOf(formerRoleString.toUpperCase());
            return compareAndReturnNotification(newRole, formerRole);
        } catch (IllegalArgumentException e) {
            log.error("Return empty string if the role is illegal", e);
            return "";
        }
    }

    private String compareAndReturnNotification(UserRole newRole, UserRole formerRole) {
        String notification = "";
        if (USER.equals(formerRole) && ADMIN.equals(newRole)) {
            notification = "Your account has been upgraded to Admin by Administer";
        }
        if (ADMIN.equals(formerRole) && USER.equals(newRole)) {
            notification = "Your account has been downgraded to Standard User by Administer";
        }
        return notification;
    }

    /**
     * Delete Role Change Notification for the User
     *
     * @param userId ID of the user
     */
    public void deleteRoleChangeNotification(Integer userId) {
        String key = KeyConstant.ROLE_CHANGE_RECORD_PREFIX + userId;
        this.deleteByKey(key);
    }

    /**
     * Check if the user has turned off notifications
     *
     * @param username username
     * @return true if the user has turned off notifications
     */
    public boolean checkIfTurnOffNotifications(String username) {
        Boolean result = this.redisTemplate.opsForSet()
                .isMember(KeyConstant.MUTE_NOTIFICATIONS, username.toLowerCase());

        return Optional.ofNullable(result).orElse(false);
    }

    /**
     * Turn on notifications if the user turned off notifications and
     * turn off notifications if the user turned on notifications
     *
     * @param username username of the user who wants to turn on/off notifications
     */
    public void turnOnTurnOffNotifications(String username) {
        String key = KeyConstant.MUTE_NOTIFICATIONS;
        String val = username.toLowerCase();

        boolean hasTurnedOff = checkIfTurnOffNotifications(val);
        if (hasTurnedOff) {
            // turn on notifications
            this.redisTemplate.opsForSet().remove(key, val);
            return;
        }

        // turn off notifications
        this.redisTemplate.opsForSet().add(key, val);
    }
}