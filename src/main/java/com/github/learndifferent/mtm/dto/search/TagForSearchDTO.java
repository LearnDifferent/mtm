package com.github.learndifferent.mtm.dto.search;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Tag Data for Search
 *
 * @author zhou
 * @date 2022/4/5
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class TagForSearchDTO implements Serializable, SearchResults {

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