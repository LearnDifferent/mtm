package com.github.learndifferent.mtm.vo;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Comment View Object / Value Object
 *
 * @author zhou
 * @date 2021/9/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class CommentVO implements Serializable {

    /**
     * ID of the comment
     */
    private Integer commentId;

    /**
     * Content
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
     * Null if this is not a reply
     * </p>
     */
    private Integer replyToCommentId;

    /**
     * Edit history of the comment
     * <p>If the comment has not been edited, this will be an empty list</p>
     */
    private List<CommentHistoryVO> history;

    private static final long serialVersionUID = 1L;
}