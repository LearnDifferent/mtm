package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author zhou
 * @date 2023/10/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class CommentDTO implements Serializable {

    /**
     * ID of the comment
     */
    private Long id;

    /**
     * content
     */
    private String comment;

    /**
     * ID of the bookmark
     */
    private Long bookmarkId;

    /**
     * Username
     */
    private String username;

    /**
     * User ID
     */
    private Long userId;

    /**
     * Creation time
     */
    private Instant creationTime;

    /**
     * ID of the comment to reply
     * <p>
     * null if this is not a reply
     * </p>
     */
    private Long replyToCommentId;

    private static final long serialVersionUID = 1L;
}