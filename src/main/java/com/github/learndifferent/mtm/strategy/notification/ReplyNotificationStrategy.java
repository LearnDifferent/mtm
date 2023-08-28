package com.github.learndifferent.mtm.strategy.notification;

import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import com.github.learndifferent.mtm.constant.consist.NotificationConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.NotificationDTO;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.mapper.CommentMapper;
import com.github.learndifferent.mtm.utils.JsonUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.learndifferent.mtm.vo.NotificationVO;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Reply Notification
 *
 * @author zhou
 * @date 2023/8/24
 */
@Component(NotificationConstant.REPLY_NOTIFICATION)
@RequiredArgsConstructor
public class ReplyNotificationStrategy implements NotificationStrategy {

    private final StringRedisTemplate redisTemplate;
    private final BookmarkMapper bookmarkMapper;
    private final CommentMapper commentMapper;

    @NotNull
    private String getNotificationsKey(Integer userId) {
        return KeyConstant.USER_REPLY_NOTIFICATIONS_PREFIX + userId;
    }

    @NotNull
    private String getReadStatusKey(Integer userId) {
        return KeyConstant.USER_REPLY_NOTIFICATION_READ_STATUS_PREFIX + userId;
    }

    private long getReadStatusOffset(UUID notificationId) {
        return Math.abs(notificationId.hashCode());
    }

    @Override
    public void sendNotification(NotificationDTO notification) {
        Integer replyToCommentId = notification.getReplyToCommentId();
        Integer bookmarkId = notification.getBookmarkId();

        // the notification belongs to the owner of the bookmark data if replyToCommentId is null,
        // and belongs to the owner of the comment data if it's not null
        boolean shouldNotifyBookmarkOwner = Objects.isNull(replyToCommentId);
        Integer recipientUserId =
                shouldNotifyBookmarkOwner ? bookmarkMapper.getBookmarkOwnerUserId(bookmarkId)
                        : commentMapper.getCommentSenderUserId(replyToCommentId);
        notification.setRecipientUserId(recipientUserId);

        // push the notification to the list
        String key = getNotificationsKey(recipientUserId);
        String content = JsonUtils.toJson(notification);
        redisTemplate.opsForList().leftPush(key, content);
        // mark it as unread
        markNotificationAsUnread(notification);
    }

    @Override
    public void markNotificationAsRead(NotificationDTO notification) {
        updateNotificationReadStatus(notification, false);
    }

    @Override
    public void markNotificationAsUnread(NotificationDTO notification) {
        updateNotificationReadStatus(notification, true);
    }

    private void updateNotificationReadStatus(NotificationDTO notification, boolean isUnread) {
        Integer userId = notification.getRecipientUserId();
        UUID notificationId = notification.getId();

        // key: prefix + user ID
        // offset: a derived value based on the notification ID
        // indicate the notifications that the user hasn't read yet
        String key = getReadStatusKey(userId);
        long offset = getReadStatusOffset(notificationId);
        redisTemplate.opsForValue().setBit(key, offset, isUnread);
    }

    @Override
    public List<NotificationVO> getNotifications(Integer userId, int loadCount) {
        Stream<NotificationDTO> notifications = getNotificationsWithoutReadStatus(userId, loadCount);
        return notifications
                .map(this::getReadStatusAndGenerateNotificationVO)
                .peek(this::updateCommentBasedOnConditions)
                .collect(Collectors.toList());
    }

    private Stream<NotificationDTO> getNotificationsWithoutReadStatus(Integer userId, int loadCount) {
        int end = loadCount - 1;

        boolean illegalEnd = end < 0;
        ThrowExceptionUtils.throwIfTrue(illegalEnd, ResultCode.NO_RESULTS_FOUND);

        String key = getNotificationsKey(userId);
        List<String> list = redisTemplate.opsForList().range(key, 0, end);

        boolean hasNoResults = CollectionUtils.isEmpty(list);
        ThrowExceptionUtils.throwIfTrue(hasNoResults, ResultCode.NO_RESULTS_FOUND);

        return list.stream()
                .map(json -> JsonUtils.toObject(json, NotificationDTO.class));
    }

    private NotificationVO getReadStatusAndGenerateNotificationVO(NotificationDTO notification) {
        Integer userId = notification.getRecipientUserId();
        UUID notificationId = notification.getId();

        String key = getReadStatusKey(userId);
        long offset = getReadStatusOffset(notificationId);

        Boolean result = redisTemplate.opsForValue().getBit(key, offset);
        // the read status will be 'read' if the result is null
        boolean isUnread = Optional.ofNullable(result).orElse(false);

        return NotificationVO.of(notification, !isUnread);
    }

    private void updateCommentBasedOnConditions(NotificationVO notification) {
        Integer bookmarkId = notification.getBookmarkId();
        Integer recipientUserId = notification.getRecipientUserId();

        // Unavailable: bookmark does not exist, bookmark has been deleted,
        // or user has no permission of the bookmark
        boolean isBookmarkUnavailable = !bookmarkMapper
                .checkIfBookmarkAvailable(bookmarkId, recipientUserId);
        if (isBookmarkUnavailable) {
            notification.setMessage(null);
            return;
        }

        Integer commentId = notification.getCommentId();
        // the comment message will be null if the comment does not exist
        String comment = commentMapper.getCommentTextById(commentId);
        notification.setMessage(comment);
    }

}