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
 * Paginated public bookmarks of the user and the total pages
 *
 * @author zhou
 * @date 2021/10/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class UserBookmarksVO implements Serializable {

    /**
     * Paginated public bookmarks
     */
    private List<WebsiteDTO> bookmarks;

    /**
     * Total pages
     */
    private Integer totalPages;

    private static final long serialVersionUID = 1L;
}
