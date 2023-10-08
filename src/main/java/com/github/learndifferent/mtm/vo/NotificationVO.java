package com.github.learndifferent.mtm.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.learndifferent.mtm.config.CustomInstantDeserializer;
import com.github.learndifferent.mtm.config.CustomInstantSerializer;
import com.github.learndifferent.mtm.constant.enums.NotificationAccessStatus;
import com.github.learndifferent.mtm.constant.enums.NotificationType;
import com.github.learndifferent.mtm.dto.NotificationDTO;
import com.github.learndifferent.mtm.utils.BeanUtils;
import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Notification
 *
 * @author zhou
 * @date 2023/8/24
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Accessors(chain = true)
public class NotificationVO implements Serializable {

    public static NotificationVO of(NotificationDTO notification, boolean isRead) {
        NotificationVO data = BeanUtils.convert(notification, NotificationVO.class);
        return data.setIsRead(isRead);
    }

    /**
     * ID of the notification
     * <p>Null if the notification is deleted</p>
     */
    private Long id;

    /**
     * Type of the notification.
     */
    private NotificationType notificationType;

    /**
     * The message of the notification
     * <p>Null if the bookmark, comment or reply has been deleted
     * when the notification is a reply (or comment) notification</p>
     * <p>If the bookmark ID is not null and the message is null,
     * the user has no permission of the bookmark</p>
     */
    private String message;

    /**
     * Time of creation.
     */
    @JsonSerialize(using = CustomInstantSerializer.class)
    @JsonDeserialize(using = CustomInstantDeserializer.class)
    private Instant creationTime;

    /**
     * When the notification is a reply (or comment) notification,
     * it represents the username of the sender.
     * If the notification is a system notification,
     * it represents the sender of the system notification.
     */
    private String sender;

    /**
     * When the {@link NotificationType} is {@link NotificationType#REPLY_NOTIFICATION},
     * this represents the User ID of the recipient who will receive the reply notification.
     * If {@link #getReplyToCommentId()} is null,
     * the recipient is the owner of the bookmark.
     * If {@link #getReplyToCommentId()} is not null,
     * the recipient is the author of the comment being replied to.
     * <p>
     * When the {@link NotificationType} is {@link NotificationType#SYSTEM_NOTIFICATION},
     * this can be used to store the ID of the user who get the message.
     * </p>
     */
    private Long recipientUserId;

    /**
     * ID of the comment
     * <p>If the comment ID is null ,the comment doesn't exist</p>
     */
    private Integer commentId;

    /**
     * ID of the bookmark
     * <p>If the bookmark ID is null, the bookmark doesn't exist.</p>
     * <p>If the bookmark ID is not null and the message is null,
     * the user has no permission of the bookmark</p>
     */
    private Integer bookmarkId;

    /**
     * The ID of the comment being replied to
     * <p>Null if not replying to any comment, indicating it's a bookmark comment.</p>
     */
    private Integer replyToCommentId;

    /**
     * True if the notification is read
     */
    private Boolean isRead;

    /**
     * Access status
     */
    private NotificationAccessStatus accessStatus;

    private static final long serialVersionUID = 1L;
}