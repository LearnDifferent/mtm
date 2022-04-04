package com.github.learndifferent.mtm.query;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Request body containing the comment information to update
 *
 * @author zhou
 * @date 2021/9/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class UpdateCommentRequest implements Serializable {

    /**
     * Comment ID
     */
    private Integer commentId;

    /**
     * New comment
     */
    private String comment;

    /**
     * ID of the bookmarked website data
     */
    private Integer webId;

    private static final long serialVersionUID = 1L;
}