package com.github.learndifferent.mtm.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Comment History
 *
 * @author zhou
 * @date 2022/4/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentHistoryDO {

    /**
     * ID of the comment
     */
    private Integer commentId;

    /**
     * Comment
     */
    private String comment;

    /**
     * Creation time
     */
    private Instant creationTime;

    private static final long serialVersionUID = 1L;
}