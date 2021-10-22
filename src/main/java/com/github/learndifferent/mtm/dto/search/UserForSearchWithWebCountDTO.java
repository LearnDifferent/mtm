package com.github.learndifferent.mtm.dto.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * User Data for search with the amount of public website data that user owns
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
public class UserForSearchWithWebCountDTO extends UserForSearchDTO implements SearchResults {

    /**
     * The amount of public website data that user owns
     */
    private Integer webCount;
}
