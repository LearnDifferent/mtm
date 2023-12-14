package com.github.learndifferent.mtm.strategy.notification;

import com.github.learndifferent.mtm.constant.consist.NotificationConstant;
import com.github.learndifferent.mtm.constant.enums.NotificationAccessStatus;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.NotificationDTO;
import com.github.learndifferent.mtm.dto.UserIdAndUsernameDTO;
import com.github.learndifferent.mtm.entity.BookmarkDO;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.mapper.CommentMapper;
import com.github.learndifferent.mtm.mapper.NotificationMapper;
import com.github.learndifferent.mtm.mapper.UserMapper;
import com.github.learndifferent.mtm.utils.JsonUtils;
import com.github.learndifferent.mtm.utils.RedisKeyUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.learndifferent.mtm.vo.NotificationVO;
import com.github.learndifferent.mtm.vo.NotificationsAndCountVO;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
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
    private final NotificationMapper notificationMapper;
    private final UserMapper userMapper;

    private final ExecutorService executorService = new ThreadPoolExecutor(2,
            5,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(),
            new ReplyNotificationThreadFactory()
    );

    public static class ReplyNotificationThreadFactory implements ThreadFactory {

        private static int threadNumber = 0;

        private static synchronized int nextThreadNumber() {
            return threadNumber++;
        }

        @Override
        public Thread newThread(@NotNull Runnable r) {
            return new Thread(r, "Thread-Reply-Notification-" + nextThreadNumber());
        }
    }

    @Override
    public void sendNotification(NotificationDTO notification) {
        Long replyToCommentId = notification.getReplyToCommentId();
        Long bookmarkId = notification.getBookmarkId();

        // the notification belongs to the owner of the bookmark data if replyToCommentId is null,
        // and belongs to the owner of the comment data if it's not null
        boolean shouldNotifyBookmarkOwner = Objects.isNull(replyToCommentId);
        Long recipientUserId =
                shouldNotifyBookmarkOwner ? bookmarkMapper.getBookmarkOwnerUserId(bookmarkId)
                        : commentMapper.getCommentSenderUserId(replyToCommentId);
        notification.setRecipientUserId(recipientUserId);

        // push the notification to the list
        String key = RedisKeyUtils.getReplyNotificationKey(recipientUserId);
        String content = JsonUtils.toJson(notification);
        redisTemplate.opsForList().leftPush(key, content);
        // mark it as unread
        markNotificationAsUnread(notification);

        // save the notification to database
        executorService.execute(() -> notificationMapper.saveReplyNotification(NotificationVO.of(notification, false)));
    }

    @Override
    public void markNotificationAsRead(NotificationDTO notification) {
        updateNotificationReadStatus(notification, false);
    }

    private void markNotificationAsRead(NotificationVO notification) {
        long notificationId = notification.getId();
        Long recipientUserId = notification.getRecipientUserId();
        updateNotificationReadStatus(notificationId, recipientUserId, false);
    }

    @Override
    public void markNotificationAsUnread(NotificationDTO notification) {
        updateNotificationReadStatus(notification, true);
    }

    private void updateNotificationReadStatus(NotificationDTO notification, boolean isUnread) {
        Long userId = notification.getRecipientUserId();
        long notificationId = notification.getId();

        updateNotificationReadStatus(notificationId, userId, isUnread);
    }

    private void updateNotificationReadStatus(long notificationId, Long recipientUserId, boolean isUnread) {
        String key = RedisKeyUtils.getReplyNotificationReadStatusKey(recipientUserId);
        long offset = RedisKeyUtils.getReplyNotificationReadStatusOffset(notificationId);
        // set read status in Redis (0 for read, 1 for unread)
        redisTemplate.opsForValue().setBit(key, offset, isUnread);

        // save the read status to database
        executorService.execute(() -> notificationMapper.updateReplyNotificationReadStatus(!isUnread, notificationId));
    }

    @Override
    public NotificationsAndCountVO getAllNotificationsAndCount(Long recipientUserId,
                                                               int loadCount,
                                                               boolean isOrderReversed) {
        long count = countAllNotifications(recipientUserId);
        if (count == 0L) {
            return NotificationsAndCountVO.empty();
        }

        Stream<NotificationDTO> notificationsStream =
                getNotificationsWithoutReadStatus(recipientUserId, loadCount, isOrderReversed);
        List<NotificationVO> notifications = notificationsStream
                .map(this::getReadStatusAndGenerateNotificationVO)
                .peek(this::updateCommentAndReadStatusBasedOnConditions)
                .collect(Collectors.toList());

        // update sender username
        updateSenderUsername(notifications);

        return NotificationsAndCountVO.of(notifications, count);
    }

    private Stream<NotificationDTO> getNotificationsWithoutReadStatus(Long userId,
                                                                      int loadCount,
                                                                      boolean isOrderReverse) {
        boolean illegalLoadCount = loadCount <= 0;
        ThrowExceptionUtils.throwIfTrue(illegalLoadCount, ResultCode.NO_RESULTS_FOUND);

        String key = RedisKeyUtils.getReplyNotificationKey(userId);
        List<String> list = isOrderReverse ? getListFromNewestToOldest(key, loadCount)
                : getListFromOldestToNewest(key, loadCount);

        return list
                .stream()
                .map(json -> JsonUtils.toObject(json, NotificationDTO.class));
    }

    private List<String> getListFromNewestToOldest(String key, int loadCount) {
        int end = loadCount - 1;
        return redisTemplate.opsForList().range(key, 0, end);
    }

    private List<String> getListFromOldestToNewest(String key, int loadCount) {
        // get the start
        // e.g. loadCount is 2: `lrange key -2 -1`
        int start = loadCount * -1;
        List<String> list = redisTemplate.opsForList().range(key, start, -1);

        // if no results, return empty list
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        // reverse the list for the reason that Redis command is `lrange` (from left to right)
        // and the needed order is from right to left
        Collections.reverse(list);
        return list;
    }

    private NotificationVO getReadStatusAndGenerateNotificationVO(NotificationDTO notification) {
        Long userId = notification.getRecipientUserId();
        long notificationId = notification.getId();

        String key = RedisKeyUtils.getReplyNotificationReadStatusKey(userId);
        long offset = RedisKeyUtils.getReplyNotificationReadStatusOffset(notificationId);

        Boolean result = redisTemplate.opsForValue().getBit(key, offset);
        // the read status will be 'read' if the result is null
        boolean isUnread = Optional.ofNullable(result).orElse(false);

        return NotificationVO.of(notification, !isUnread);
    }

    private void updateCommentAndReadStatusBasedOnConditions(NotificationVO notification) {
        Long bookmarkId = notification.getBookmarkId();
        Long recipientUserId = notification.getRecipientUserId();

        BookmarkDO bookmark = bookmarkMapper.getBookmarkById(bookmarkId);
        // if the bookmark does not exist
        boolean isBookmarkNotPresent = Objects.isNull(bookmark);
        if (isBookmarkNotPresent) {
            // set message and bookmark ID to null to indicate the bookmark doesn't exist
            notification.setMessage(null);
            notification.setBookmarkId(null);
            // mark as read and set the read status to 'read'
            notification.setIsRead(true);
            markNotificationAsRead(notification);
            // set access status
            notification.setAccessStatus(NotificationAccessStatus.BOOKMARK_NOT_EXIST);
            return;
        }

        Boolean bookmarkIsPublic = bookmark.getIsPublic();
        boolean isPublic = BooleanUtils.isTrue(bookmarkIsPublic);

        boolean hasPermission = isPublic
                || checkIfRecipientUserIsOwnerOfBookmark(recipientUserId, bookmark);
        if (!hasPermission) {
            // if user has no permission of the bookmark
            notification.setMessage(null);
            notification.setAccessStatus(NotificationAccessStatus.UNAUTHORIZED);
            return;
        }

        Long commentId = notification.getCommentId();
        String comment = commentMapper.getCommentTextById(commentId);
        if (StringUtils.isBlank(comment)) {
            // set message and comment ID to null to indicate the comment doesn't exist
            notification.setMessage(null);
            notification.setCommentId(null);

            // mark as read and set the read status to 'read'
            notification.setIsRead(true);
            notification.setAccessStatus(NotificationAccessStatus.COMMENT_NOT_EXIST);
            markNotificationAsRead(notification);
            return;
        }

        // set the comment message if nothing wrong
        notification.setMessage(comment);
        notification.setAccessStatus(NotificationAccessStatus.ACCESSIBLE);
    }

    private void updateSenderUsername(List<NotificationVO> notifications) {
        Set<Long> ids = notifications
                .stream()
                .map(NotificationVO::getSenderUserId)
                .collect(Collectors.toSet());

        Map<Long, UserIdAndUsernameDTO> userIdAndUsernameMap = userMapper.getUserIdAndUsernameMap(ids);

        // transfer the map to a map with key as user ID and value as username
        Map<Long, String> userIdsAndUsernames = userIdAndUsernameMap
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> entry.getValue().getUsername()
                ));

        for (NotificationVO notification : notifications) {
            Long senderUserId = notification.getSenderUserId();
            // get the username
            String username = userIdsAndUsernames.getOrDefault(senderUserId,
                    "[The user does not exist or has been deleted]");
            // set the username
            notification.setSender(username);
        }
    }

    private boolean checkIfRecipientUserIsOwnerOfBookmark(Long recipientUserId, BookmarkDO bookmark) {
        Long bookmarkOwnerUserId = bookmark.getUserId();
        return recipientUserId.equals(bookmarkOwnerUserId);
    }

    @Override
    public long countAllNotifications(Long recipientUserId) {
        String key = RedisKeyUtils.getReplyNotificationKey(recipientUserId);
        Long size = this.redisTemplate.opsForList().size(key);
        return Optional.ofNullable(size).orElse(0L);
    }

    @Override
    public NotificationsAndCountVO getUnreadNotificationAndCount(long recipientUserId,
                                                                 int loadCount,
                                                                 boolean isOrderReversed) {
        // count
        int count = notificationMapper.countUnreadReplyNotifications(recipientUserId);
        if (count == 0) {
            return NotificationsAndCountVO.empty();
        }

        // unread notifications
        List<NotificationVO> notifications = notificationMapper.getUnreadReplyNotifications(
                recipientUserId, loadCount, isOrderReversed);
        return NotificationsAndCountVO.of(notifications, count);
    }
}