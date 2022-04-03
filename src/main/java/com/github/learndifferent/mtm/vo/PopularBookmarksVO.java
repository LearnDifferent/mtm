package com.github.learndifferent.mtm.vo;

import com.github.learndifferent.mtm.dto.PopularBookmarkDTO;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Popular bookmarks and total pages
 *
 * @author zhou
 * @date 2022/4/3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class PopularBookmarksVO implements Serializable {

    /**
     * Popular bookmarks
     */
    private List<PopularBookmarkDTO> bookmarks;
    /**
     * Total pages
     */
    Integer totalPages;

    private static final long serialVersionUID = 1L;
}
