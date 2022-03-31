package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Popular Tag Data Transfer Object
 *
 * @author zhou
 * @date 2022/3/31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class PopularTagDTO implements Serializable {

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
