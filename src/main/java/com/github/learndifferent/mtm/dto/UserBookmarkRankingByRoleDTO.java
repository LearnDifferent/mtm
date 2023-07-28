package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * The ranking of the number of bookmarks for each role
 *
 * @author zhou
 * @date 2023/3/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class UserBookmarkRankingByRoleDTO implements Serializable {

    /**
     * Ranking
     */
    private Integer ranking;

    /**
     * Role of the user
     */
    private String role;

    /**
     * Number of bookmarks
     */
    private Integer bookmarkNumber;

    /**
     * Username of the user
     */
    private String userName;

    private static final long serialVersionUID = 1L;
}