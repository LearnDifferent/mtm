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
public class BookmarkCommentVO implements Serializable {

    /**
     * ID of the comment
     */
    private Long id;

    /**
     * Content
     */
    private String comment;

    /**
     * User ID
     */
    private Long userId;

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
    private Long replyToCommentId;

    /**
     * Count of the replies from this comment
     */
    private Long repliesCount;

    /**
     * Edit history of the comment
     * <p>If the comment has not been edited, this will be an empty list</p>
     */
    private List<CommentHistoryVO> history;

    private static final long serialVersionUID = 1L;
}