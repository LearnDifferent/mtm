package com.github.learndifferent.mtm.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Delete a reply notification
 *
 * @author zhou
 * @date 2021/10/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class DelReNotificationRequest implements Serializable {

    /**
     * Creation time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date creationTime;

    /**
     * User's name, who is about to receive the notification.
     * If {@link com.github.learndifferent.mtm.dto.ReplyNotificationDTO#getReplyToCommentId()} is null,
     * then the user is the owner of the website data.
     * If {@link com.github.learndifferent.mtm.dto.ReplyNotificationDTO#getReplyToCommentId()} is not null,
     * then the user is the author of the comment being replied to.
     */
    private String receiveUsername;

    /**
     * User's name, who sent the reply (or comment)
     */
    private String sendUsername;

    /**
     * Comment id
     */
    private Integer commentId;

    /**
     * Web id
     */
    private Integer webId;

    /**
     * The ID of the comment being replied to (null if not reply to any comment)
     */
    private Integer replyToCommentId;

    private static final long serialVersionUID = 1L;
}
