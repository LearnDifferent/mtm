package com.github.learndifferent.mtm.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.learndifferent.mtm.config.CustomInstantDeserializer;
import com.github.learndifferent.mtm.config.CustomInstantSerializer;
import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Notification with reply message
 *
 * @author zhou
 * @date 2022/4/8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplyMessageNotificationVO implements Serializable {

    /**
     * Reply Message
     * <p>Null if the bookmark, comment or reply has been deleted</p>
     */
    private String message;

    /**
     * Creation time
     */
    @JsonSerialize(using = CustomInstantSerializer.class)
    @JsonDeserialize(using = CustomInstantDeserializer.class)
    private Instant creationTime;

    /**
     * Name of the user, who is about to receive the notification.
     * If {@link #getReplyToCommentId()} is null,
     * then the user is the owner of the website data.
     * If {@link #getReplyToCommentId()} is not null,
     * then the user is the author of the comment being replied to.
     */
    private String receiveUsername;

    /**
     * Name of the user who sent the reply (or comment)
     */
    private String sendUsername;

    /**
     * ID of the comment
     */
    private Integer commentId;

    /**
     * ID of the bookmarked website data
     */
    private Integer webId;

    /**
     * The ID of the comment being replied to
     * <p>Not reply to any comment if null, which means this is a bookmark comment</p>
     */
    private Integer replyToCommentId;

    private static final long serialVersionUID = 1L;
}