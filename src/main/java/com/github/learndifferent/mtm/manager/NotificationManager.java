package com.github.learndifferent.mtm.manager;

import static com.github.learndifferent.mtm.constant.enums.UserRole.ADMIN;
import static com.github.learndifferent.mtm.constant.enums.UserRole.USER;

import com.github.learndifferent.mtm.constant.consist.NotificationConstant;
import com.github.learndifferent.mtm.constant.consist.RedisConstant;
import com.github.learndifferent.mtm.constant.enums.NotificationType;
import com.github.learndifferent.mtm.constant.enums.UserRole;
import com.github.learndifferent.mtm.dto.CommentDTO;
import com.github.learndifferent.mtm.dto.NotificationDTO;
import com.github.learndifferent.mtm.service.IdGeneratorService;
import com.github.learndifferent.mtm.strategy.notification.NotificationStrategyContext;
import com.github.learndifferent.mtm.utils.JsonUtils;
import com.github.learndifferent.mtm.utils.RedisKeyUtils;
import com.github.learndifferent.mtm.vo.NotificationsAndCountVO;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    private final IdGeneratorService idGeneratorService;

    private long generateReplyNotificationId() {
        return idGeneratorService.generateId(NotificationConstant.REPLY_NOTIFICATION);
    }

    private long generateSystemNotificationId() {
        return idGeneratorService.generateId(NotificationConstant.SYSTEM_NOTIFICATION);
    }

    public void sendReplyNotification(CommentDTO comment) {
        Long commentId = comment.getId();
        Long bookmarkId = comment.getBookmarkId();
        Long replyToCommentId = comment.getReplyToCommentId();
        String commentMessage = comment.getComment();

        String sendUsername = comment.getUsername();
        Instant creationTime = comment.getCreationTime();

        long id = generateReplyNotificationId();

        NotificationDTO notification = NotificationDTO.ofNewReplyNotification(
                id, sendUsername, commentMessage, creationTime, commentId, bookmarkId, replyToCommentId);
        notificationStrategyContext.sendNotification(notification);
    }

    public void sendSystemNotification(String sender, String message) {
        long id = generateSystemNotificationId();
        NotificationDTO notification = NotificationDTO.ofNewSystemNotification(id, sender, message);
        notificationStrategyContext.sendNotification(notification);
    }

    public NotificationsAndCountVO getAllNotificationsAndCount(NotificationType notificationType,
                                                               Long recipientUserId,
                                                               int loadCount,
                                                               boolean isOrderReversed) {
        return notificationStrategyContext.getAllNotificationsAndCount(
                notificationType, recipientUserId, loadCount, isOrderReversed);
    }

    public NotificationsAndCountVO getUnreadNotificationsAndCount(NotificationType notificationType,
                                                                  Long recipientUserId,
                                                                  int loadCount,
                                                                  boolean isOrderReversed) {
        return notificationStrategyContext.getUnreadNotificationsAndCount(
                notificationType, recipientUserId, loadCount, isOrderReversed);
    }

    public long countAllReplyNotifications(Long recipientUserId) {
        return notificationStrategyContext.countAllNotifications(NotificationType.REPLY_NOTIFICATION, recipientUserId);
    }

    public long countAllSystemNotifications() {
        return notificationStrategyContext.countAllNotifications(NotificationType.SYSTEM_NOTIFICATION, null);
    }

    public long countUnreadReplies(Long recipientUserId) {
        String key = RedisKeyUtils.getReplyNotificationReadStatusKey(recipientUserId);

        Long notificationCount = redisTemplate.execute(
                (RedisCallback<Long>) connection -> connection.bitCount(key.getBytes()));

        return Optional.ofNullable(notificationCount).orElse(0L);
    }

    public long countUnreadSystemNotifications(Long recipientUserId) {
        String key = RedisKeyUtils
                .getSysNotificationReadStatusTrackNotificationsOfUserKey(recipientUserId);
        // count the number of notifications that a user has read
        Long result =
                redisTemplate.execute((RedisCallback<Long>) connection -> connection.bitCount(key.getBytes()));
        long userReadCount = Optional.ofNullable(result).orElse(0L);

        // count total notifications
        long sysCount = countAllSystemNotifications();

        long userUnread = sysCount - userReadCount;
        if (userUnread < 0L) {
            log.warn("System notification count is lower than the user's read count, which is abnormal. User ID: {}",
                    recipientUserId);
            userUnread = 0L;
        }
        return userUnread;
    }

    public boolean checkIfUserHasUnreadSysNotifications(Long recipientUserId) {
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

    private boolean checkIfHasUnreadSysNotificationsWhenHavingSysNotifications(Long recipientUserId,
                                                                               long sysNotificationIndex) {
        long readStatusOffset = RedisKeyUtils.getSysNotificationReadStatusReadByUserOffset(recipientUserId);
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
        Long id = notification.getId();
        return RedisKeyUtils.getSysNotificationReadStatusReadByUserKey(id);
    }

    public void markNotificationAsRead(NotificationDTO notification) {
        notificationStrategyContext.markNotificationAsRead(notification);
    }

    public void markNotificationAsUnread(NotificationDTO notification) {
        notificationStrategyContext.markNotificationAsUnread(notification);
    }

    public void deleteReplyNotificationData(Long recipientUserId) {
        String replyNotificationKey = RedisKeyUtils.getReplyNotificationKey(recipientUserId);
        redisTemplate.delete(replyNotificationKey);

        String replyNotificationReadStatusKey = RedisKeyUtils.getReplyNotificationReadStatusKey(recipientUserId);
        redisTemplate.delete(replyNotificationReadStatusKey);
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
    public String generateRoleChangeNotification(Long userId) {
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
    public void deleteRoleChangeNotification(Long userId) {
        String key = RedisConstant.ROLE_CHANGE_RECORD_PREFIX + userId;
        redisTemplate.delete(key);
    }

    /**
     * Check if the user has turned off notifications
     *
     * @param userId ID of the user
     * @return true if the user has turned off notifications
     */
    public boolean checkIfTurnOffNotifications(Long userId) {
        Boolean result = this.redisTemplate.opsForSet()
                .isMember(RedisConstant.MUTE_NOTIFICATIONS, String.valueOf(userId));

        return Optional.ofNullable(result).orElse(false);
    }

    /**
     * Turn on notifications if the user turned off notifications and
     * turn off notifications if the user turned on notifications
     *
     * @param userId user ID of the user who wants to turn on/off notifications
     */
    public void turnOnTurnOffNotifications(Long userId) {
        String key = RedisConstant.MUTE_NOTIFICATIONS;

        boolean hasTurnedOff = checkIfTurnOffNotifications(userId);
        if (hasTurnedOff) {
            // turn on notifications
            this.redisTemplate.opsForSet().remove(key, String.valueOf(userId));
            return;
        }

        // turn off notifications
        this.redisTemplate.opsForSet().add(key, String.valueOf(userId));
    }
}