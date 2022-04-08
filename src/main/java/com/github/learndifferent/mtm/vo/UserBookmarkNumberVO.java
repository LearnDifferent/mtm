package com.github.learndifferent.mtm.vo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Username of the user and total number of the user's public bookmarks
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBookmarkNumberVO implements Serializable {

    /**
     * Username
     */
    private String userName;

    /**
     * Total number of the user's public bookmarks
     */
    private Integer bookmarkNumber;

    private static final long serialVersionUID = 1L;
}