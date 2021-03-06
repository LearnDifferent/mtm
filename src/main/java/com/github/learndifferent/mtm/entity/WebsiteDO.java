package com.github.learndifferent.mtm.entity;

import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Website Data Object
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class WebsiteDO implements Serializable {

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