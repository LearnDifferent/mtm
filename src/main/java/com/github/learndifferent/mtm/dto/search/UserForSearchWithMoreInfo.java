package com.github.learndifferent.mtm.dto.search;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * User Data for search with the number of websites bookmarked by the user
 * and highlighted fields
 *
 * @author zhou
 * @date 2021/10/21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Accessors(chain = true)
public class UserForSearchWithMoreInfo extends UserForSearchDTO implements SearchResults {

    /**
     * The number of websites bookmarked by the user
     */
    private Integer webCount;

    /**
     * Highlighted fields
     */
    private List<String> highlightedFields;
}
