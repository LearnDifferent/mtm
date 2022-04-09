package com.github.learndifferent.mtm.query;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.learndifferent.mtm.config.CustomInstantDeserializer;
import com.github.learndifferent.mtm.config.CustomInstantSerializer;
import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Request body that contains the data of the reply notification to delete
 *
 * @author zhou
 * @date 2021/10/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class DeleteReplyNotificationRequest implements Serializable {

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