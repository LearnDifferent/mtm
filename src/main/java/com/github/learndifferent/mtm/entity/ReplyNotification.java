package com.github.learndifferent.mtm.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.learndifferent.mtm.config.CustomInstantDeserializer;
import com.github.learndifferent.mtm.config.CustomInstantSerializer;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Reply Notification
 *
 * @author zhou
 * @date 2022/11/2
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@SuperBuilder
public class ReplyNotification {

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
     * ID of the bookmark
     */
    private Integer bookmarkId;

    /**
     * The ID of the comment being replied to
     * <p>Not reply to any comment if null, which means this is a bookmark comment</p>
     */
    private Integer replyToCommentId;
}