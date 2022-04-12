package com.github.learndifferent.mtm.vo;

import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Comment history
 *
 * @author zhou
 * @date 2022/4/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentHistoryVO implements Serializable {

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