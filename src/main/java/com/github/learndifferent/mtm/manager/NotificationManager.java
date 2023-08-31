package com.github.learndifferent.mtm.manager;

import static com.github.learndifferent.mtm.constant.enums.UserRole.ADMIN;
import static com.github.learndifferent.mtm.constant.enums.UserRole.USER;

import com.github.learndifferent.mtm.constant.consist.RedisConstant;
import com.github.learndifferent.mtm.constant.enums.NotificationType;
import com.github.learndifferent.mtm.constant.enums.UserRole;
import com.github.learndifferent.mtm.dto.NotificationDTO;
import com.github.learndifferent.mtm.entity.CommentDO;
import com.github.learndifferent.mtm.strategy.notification.NotificationStrategyContext;
import com.github.learndifferent.mtm.utils.JsonUtils;
import com.github.learndifferent.mtm.utils.RedisKeyUtils;
import com.github.learndifferent.mtm.vo.NotificationVO;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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

    public void sendSystemNotification(String sender, String message) {
        NotificationDTO notification = NotificationDTO.ofNewSystemNotification(sender, message);
        notificationStrategyContext.sendNotification(notification);
    }

    public List<NotificationVO> getNotifications(NotificationType notificationType,
                                                 Integer recipientUserId,
                                                 int loadCount) {
        return notificationStrategyContext.getNotifications(notificationType, recipientUserId, loadCount);
    }

    public long countAllReplyNotifications(Integer recipientUserId) {
        String key = RedisKeyUtils.getReplyNotificationKey(recipientUserId);
        Long size = this.redisTemplate.opsForList().size(key);
        return Optional.ofNullable(size).orElse(0L);
    }

    public long countAllSystemNotifications() {
        String key = RedisKeyUtils.getSystemNotificationKey();
        Long size = this.redisTemplate.opsForList().size(key);
        return Optional.ofNullable(size).orElse(0L);
    }

    public long countUnreadReplies(Integer recipientUserId) {
        String key = RedisKeyUtils.getReplyNotificationReadStatusKey(recipientUserId);

        Long notificationCount = redisTemplate.execute(
                (RedisCallback<Long>) connection -> connection.bitCount(key.getBytes()));

        return Optional.ofNullable(notificationCount).orElse(0L);
    }

    public boolean checkIfUserHasUnreadSysNotifications(Integer recipientUserId) {
        long systemNotificationNumber = countAllSystemNotifications();
        if (systemNotificationNumber < 1L) {
            // if no system notifications,
            // return false to indicate there is no unread system notifications
            return false;
        }

        for (long i = 0L; i < systemNotificationNumber; i++) {
            boolean hasUnread = checkIfHasUnreadSysNotificationsWhenHavingSysNotifications(recipientUserId, i);
            if (hasUnread) {
                return true;
            }
        }

        // if every notification is read,
        // return false to indicate there is no unread system notifications
        return false;
    }

    private boolean checkIfHasUnreadSysNotificationsWhenHavingSysNotifications(Integer recipientUserId,
                                                                               long sysNotificationIndex) {
        long readStatusOffset = RedisKeyUtils.getSystemNotificationReadStatusOffset(recipientUserId);
        String readStatusKey = getReadStatusKeyWhenHavingSysNotifications(sysNotificationIndex);

        // 0 stands for unread (false / null stands for unread)
        Boolean result = redisTemplate.opsForValue().getBit(readStatusKey, readStatusOffset);
        return BooleanUtils.isNotTrue(result);
    }

    private String getReadStatusKeyWhenHavingSysNotifications(long index) {
        String systemNotificationKey = RedisKeyUtils.getSystemNotificationKey();
        List<String> result = redisTemplate.opsForList().range(systemNotificationKey, index, index);
        assert result != null;
        String notificationJson = result.get(0);
        NotificationDTO notification = JsonUtils.toObject(notificationJson, NotificationDTO.class);
        UUID id = notification.getId();
        return RedisKeyUtils.getSystemNotificationReadStatusKey(id);
    }

    public void markNotificationAsRead(NotificationDTO notification) {
        notificationStrategyContext.markNotificationAsRead(notification);
    }

    public void markNotificationAsUnread(NotificationDTO notification) {
        notificationStrategyContext.markNotificationAsUnread(notification);
    }

    public void deleteReplyNotificationData(Integer recipientUserId) {
        String replyNotificationKey = RedisKeyUtils.getReplyNotificationKey(recipientUserId);
        redisTemplate.delete(replyNotificationKey);

        String replyNotificationReadStatusKey = RedisKeyUtils.getReplyNotificationReadStatusKey(recipientUserId);
        redisTemplate.delete(replyNotificationReadStatusKey);
    }

    /**
     * Delete {@code key}
     *
     * @param key redis key
     */
    public void deleteByKey(String key) {
        this.redisTemplate.delete(key);
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

        String key = RedisConstant.ROLE_CHANGE_RECORD_PREFIX + id;
        // put new role
        this.redisTemplate.opsForHash().put(key, RedisConstant.NEW_ROLE_CHANGE_RECORD_HASH_KEY, newRole.role());
        // put former role if absent: only record the first role
        this.redisTemplate.opsForHash().putIfAbsent(key, RedisConstant.FORMER_ROLE_CHANGE_RECORD_HASH_KEY,
                formerRole.role());
    }

    /**
     * Generate a User Role Change Notification
     *
     * @param userId ID of the user
     * @return return the notification or an empty string if the user role is not changed
     */
    public String generateRoleChangeNotification(Integer userId) {
        String key = RedisConstant.ROLE_CHANGE_RECORD_PREFIX + userId;

        Object newRoleObject = this.redisTemplate.opsForHash().get(key, RedisConstant.NEW_ROLE_CHANGE_RECORD_HASH_KEY);
        if (Objects.isNull(newRoleObject)) {
            return "";
        }

        Object formerRoleObject =
                this.redisTemplate.opsForHash().get(key, RedisConstant.FORMER_ROLE_CHANGE_RECORD_HASH_KEY);
        if (Objects.isNull(formerRoleObject)) {
            return "";
        }

        String newRoleString = String.valueOf(newRoleObject);
        String formerRoleString = String.valueOf(formerRoleObject);

        try {
            UserRole newRole = UserRole.valueOf(newRoleString.toUpperCase());
            UserRole formerRole = UserRole.valueOf(formerRoleString.toUpperCase());
            return compareAndReturnRoleChangeNotification(newRole, formerRole);
        } catch (IllegalArgumentException e) {
            log.error("Return empty string if the role is illegal", e);
            return "";
        }
    }

    private String compareAndReturnRoleChangeNotification(UserRole newRole, UserRole formerRole) {
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
        String key = RedisConstant.ROLE_CHANGE_RECORD_PREFIX + userId;
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
                .isMember(RedisConstant.MUTE_NOTIFICATIONS, username.toLowerCase());

        return Optional.ofNullable(result).orElse(false);
    }

    /**
     * Turn on notifications if the user turned off notifications and
     * turn off notifications if the user turned on notifications
     *
     * @param username username of the user who wants to turn on/off notifications
     */
    public void turnOnTurnOffNotifications(String username) {
        String key = RedisConstant.MUTE_NOTIFICATIONS;
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