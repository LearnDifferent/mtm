package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Reply Notification Data Transfer Object
 *
 * @author zhou
 * @date 2021/10/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class ReplyNotificationDTO implements Serializable {

    /**
     * Creation time
     */
    private Instant creationTime;

    /**
     * Name of the user, who is about to receive the notification.
     * If {@link ReplyNotificationDTO#getReplyToCommentId()} is null,
     * then the user is the owner of the website data.
     * If {@link ReplyNotificationDTO#getReplyToCommentId()} is not null,
     * then the user is the author of the comment being replied to.
     */
    private String receiveUsername;

    /**
     * Name of the user who sent the reply (or comment)
     */
    private String sendUsername;

    /**
     * Comment ID
     */
    private Integer commentId;

    /**
     * Web ID
     */
    private Integer webId;

    /**
     * The ID of the comment being replied to (null if not reply to any comment)
     */
    private Integer replyToCommentId;

    private static final long serialVersionUID = 1L;
}
