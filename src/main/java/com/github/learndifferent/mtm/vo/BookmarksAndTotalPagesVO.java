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
 * Paginated bookmarks and total Pages
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class BookmarksAndTotalPagesVO implements Serializable {

    /**
     * Paginated bookmarks
     */
    private List<? extends WebsiteDTO> bookmarks;
    /**
     * Total pages
     */
    private Integer totalPages;

    private static final long serialVersionUID = 1L;
}
