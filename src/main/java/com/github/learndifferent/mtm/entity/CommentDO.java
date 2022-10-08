package com.github.learndifferent.mtm.entity;

import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Comment Data Object
 *
 * @author zhou
 * @date 2021/9/28
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class CommentDO implements Serializable {

    /**
     * ID of the comment
     */
    private Integer id;

    /**
     * content
     */
    private String comment;

    /**
     * ID of the bookmark
     */
    private Integer bookmarkId;

    /**
     * Username
     */
    private String username;

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
    private Integer replyToCommentId;

    private static final long serialVersionUID = 1L;
}