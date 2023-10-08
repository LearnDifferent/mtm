package com.github.learndifferent.mtm.vo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Visited Bookmark
 *
 * @author zhou
 * @date 2022/3/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class VisitedBookmarkVO implements Serializable {

    /**
     * User ID
     */
    private Long userId;

    /**
     * Username
     */
    private String username;

    /**
     * Title
     */
    private String title;

    /**
     * Url
     */
    private String url;

    /**
     * ID of the bookmark
     */
    private Integer bookmarkId;

    /**
     * True if this is a public bookmark
     */
    private Boolean isPublic;

    /**
     * The number of views of this bookmark
     */
    private Integer views;

    private static final long serialVersionUID = 1L;
}