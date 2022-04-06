package com.github.learndifferent.mtm.vo;

import com.github.learndifferent.mtm.dto.WebsiteDTO;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Result of searching bookmarks by a certain tag
 *
 * @author zhou
 * @date 2022/4/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class SearchByTagResultVO implements Serializable {

    /**
     * Paginated bookmarks associated with the chosen tag
     */
    private List<WebsiteDTO> bookmarks;

    /**
     * Total pages
     */
    private Integer totalPages;

    private static final long serialVersionUID = 1L;
}
