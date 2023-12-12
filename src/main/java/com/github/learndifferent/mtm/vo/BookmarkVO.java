package com.github.learndifferent.mtm.vo;

import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Bookmark View Object / Value Object
 *
 * @author zhou
 * @date 2022/4/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkVO implements Serializable {

    /**
     * ID of the bookmark
     */
    private Long id;
    /**
     * ID of the owner of the bookmark
     */
    private Long userId;
    /**
     * Username of the owner of the bookmark
     */
    private String userName;
    /**
     * title of the bookmarked website
     */
    private String title;
    /**
     * URL of the bookmarked website
     */
    private String url;
    /**
     * Image of the bookmarked website
     */
    private String img;
    /**
     * Description of the bookmarked website
     */
    private String desc;
    /**
     * Creation time
     */
    private Instant createTime;

    /**
     * True if this is a public bookmark
     */
    private Boolean isPublic;

    private static final long serialVersionUID = 1L;
}