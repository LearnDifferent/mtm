package com.github.learndifferent.mtm.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Tag and count the number of bookmarks of this tag
 *
 * @author zhou
 * @date 2022/4/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class TagAndCountDO implements Serializable {

    /**
     * Tag
     */
    String tag;

    /**
     * Count the number of bookmarks of this tag
     */
    Integer number;

    private static final long serialVersionUID = 1L;
}
