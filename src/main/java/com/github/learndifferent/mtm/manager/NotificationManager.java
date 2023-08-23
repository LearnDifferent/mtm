package com.github.learndifferent.mtm.manager;

import static com.github.learndifferent.mtm.constant.enums.UserRole.ADMIN;
import static com.github.learndifferent.mtm.constant.enums.UserRole.USER;

import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import com.github.learndifferent.mtm.constant.enums.PriorityLevel;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.UserRole;
import com.github.learndifferent.mtm.dto.ReplyNotificationDTO;
import com.github.learndifferent.mtm.entity.BookmarkDO;
import com.github.learndifferent.mtm.entity.CommentDO;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.mapper.CommentMapper;
import com.github.learndifferent.mtm.query.DeleteReplyNotificationRequest;
import com.github.learndifferent.mtm.utils.JsonUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.learndifferent.mtm.vo.ReplyMessageNotificationVO;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final CommentMapper commentMapper;
    private final BookmarkMapper bookmarkMapper;

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

    public int countNewReplyNotifications(String receiveUsername) {
        String key = KeyConstant.REPLY_NOTIFICATION_COUNT_PREFIX + receiveUsername.toLowerCase();
        String notificationCount = this.redisTemplate.opsForValue().get(key);
        String count = Optional.ofNullable(notificationCount).orElse("0");
        try {
            return Integer.parseInt(count);
        } catch (NumberFormatException e) {
            log.info("{} is not a number, return 0 as default", count);
            return 0;
        }
    }

    /**
     * Get reply notifications and clear notification count
     *
     * @param receiveUsername username
     * @param from            from
     * @param lastIndex       to
     * @return reply notifications
     */
    public List<ReplyMessageNotificationVO> getReplyMessageNotification(String receiveUsername,
                                                                        int from,
                                                                        int lastIndex) {
        String key = KeyConstant.REPLY_NOTIFICATION_PREFIX + receiveUsername.toLowerCase();
        List<String> notifications = this.redisTemplate.opsForList().range(key, from, lastIndex);

        boolean hasNoNotifications = CollectionUtils.isEmpty(notifications);
        ThrowExceptionUtils.throwIfTrue(hasNoNotifications, ResultCode.NO_RESULTS_FOUND);

        // clear notification count
        String notificationCountKey = KeyConstant.REPLY_NOTIFICATION_COUNT_PREFIX + receiveUsername.toLowerCase();
        this.deleteByKey(notificationCountKey);

        return notifications.stream()
                .map(this::getReplyMessageNotification)
                .collect(Collectors.toList());
    }

    private ReplyMessageNotificationVO getReplyMessageNotification(String notification) {
        ReplyMessageNotificationVO no = JsonUtils.toObject(notification, ReplyMessageNotificationVO.class);
        String text = this.getCommentTextIfBookmarkAndCommentExist(no);
        no.setMessage(text);
        return no;
    }

    private String getCommentTextIfBookmarkAndCommentExist(ReplyMessageNotificationVO notification) {

        Integer bookmarkId = notification.getBookmarkId();
        // include private bookmarks because another method
        // that views the details will verify the permission later on
        BookmarkDO bookmark = bookmarkMapper.getBookmarkById(bookmarkId);

        boolean isNotExists = Objects.isNull(bookmark);

        // if the bookmark does not exist,
        // returns null to indicate that the comment does not exist
        return isNotExists ? null : getCommentTextFromNotification(notification);
    }

    private String getCommentTextFromNotification(ReplyMessageNotificationVO notification) {
        int commentId = notification.getCommentId();
        // the result is null if the comment does not exist
        return this.commentMapper.getCommentTextById(commentId);
    }

    /**
     * Send reply notification and increase notification count
     *
     * @param comment comment / reply to send
     */
    public void sendReplyNotification(CommentDO comment) {

        ReplyNotificationDTO notification = getReplyNotificationDTO(comment);

        String receiveUsername = notification.getReceiveUsername();
        String receiveInfo = KeyConstant.REPLY_NOTIFICATION_PREFIX + receiveUsername.toLowerCase();

        String notificationContent = JsonUtils.toJson(notification);
        this.redisTemplate.opsForList().leftPush(receiveInfo, notificationContent);

        // key: prefix + username
        // offset: hashcode of the ReplyNotificationDTO (absolute value)
        // value: true if user has not read the reply / true if the reply should be read by the user
        String replyToReadKey = KeyConstant.USER_REPLY_TO_READ + receiveUsername.toLowerCase();
        int replyToReadOffset = Math.abs(notification.hashCode());

        this.redisTemplate.opsForValue().setBit(replyToReadKey, replyToReadOffset, true);

        // increase notification count
        String countKey = KeyConstant.REPLY_NOTIFICATION_COUNT_PREFIX + receiveUsername.toLowerCase();
        this.redisTemplate.opsForValue().increment(countKey);
    }

    private ReplyNotificationDTO getReplyNotificationDTO(CommentDO comment) {

        Integer commentId = comment.getId();
        Integer bookmarkId = comment.getBookmarkId();
        Integer replyToCommentId = comment.getReplyToCommentId();

        // the notification belongs to the owner of the bookmark data if replyToCommentId is null,
        // and belongs to the owner of the comment data if it's not null
        boolean shouldNotifyBookmarkOwner = Objects.isNull(replyToCommentId);
        String receiveUsername =
                shouldNotifyBookmarkOwner ? this.bookmarkMapper.getBookmarkOwnerName(bookmarkId)
                        : this.commentMapper.getCommentSenderName(replyToCommentId);

        String sendUsername = comment.getUsername();
        Instant creationTime = comment.getCreationTime();

        return ReplyNotificationDTO.builder()
                .creationTime(creationTime)
                .receiveUsername(receiveUsername)
                .sendUsername(sendUsername)
                .commentId(commentId)
                .bookmarkId(bookmarkId)
                .replyToCommentId(replyToCommentId)
                .build();
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
    public String generateRoleChangeNotification(String userId) {
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
    public void deleteRoleChangeNotification(String userId) {
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