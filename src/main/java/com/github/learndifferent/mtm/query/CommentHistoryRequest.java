package com.github.learndifferent.mtm.query;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body that contains ID of the comment and ID of the bookmarked website data
 *
 * @author zhou
 * @date 2022/4/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentHistoryRequest implements Serializable {

    /**
     * ID of the comment
     */
    private Integer commentId;

    /**
     * ID of the bookmarked website data
     */
    private Integer webId;

    private static final long serialVersionUID = 1L;
}