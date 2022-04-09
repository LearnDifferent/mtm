package com.github.learndifferent.mtm.vo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Visited Bookmarks
 *
 * @author zhou
 * @date 2022/3/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class VisitedBookmarksVO implements Serializable {

    /**
     * Username
     */
    private String userName;

    /**
     * Title
     */
    private String title;

    /**
     * Url
     */
    private String url;

    /**
     * ID
     */
    private Integer webId;

    /**
     * True if this is a public bookmarks
     */
    private Boolean isPublic;

    /**
     * The number of views of this bookmark
     */
    private Integer views;

    private static final long serialVersionUID = 1L;
}