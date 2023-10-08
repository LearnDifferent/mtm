package com.github.learndifferent.mtm.vo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Home Page
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class HomePageVO implements Serializable {

    /**
     * Username of the user that is currently logged in
     */
    private String currentUsername;
    /**
     * ID of the user that is currently logged in
     */
    private Long currentUserId;
    /**
     * Paginated bookmarks and total pages
     */
    private BookmarksAndTotalPagesVO bookmarksAndTotalPages;
    /**
     * ID of the user whose data is being requested
     */
    private Long requestedUserId;

    private static final long serialVersionUID = 1L;
}