package com.github.learndifferent.mtm.vo;

import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhou
 * @date 2022/4/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkVO implements Serializable {

    /**
     * ID of the bookmarked website data
     */
    private Integer webId;
    /**
     * name of the user who bookmarked the website
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