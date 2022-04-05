package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Comment data of a bookmark
 *
 * @author zhou
 * @date 2021/9/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class BookmarkCommentDTO implements Serializable {

    /**
     * Comment ID
     */
    private Integer commentId;

    /**
     * Content
     */
    private String comment;

    /**
     * Username
     */
    private String username;

    /**
     * Creation time
     */
    private Instant creationTime;

    /**
     * Reply to another comment (null if it's not a reply)
     */
    private Integer replyToCommentId;

    /**
     * Count of the replies from this comment
     */
    private Integer repliesCount;

    private static final long serialVersionUID = 1L;
}