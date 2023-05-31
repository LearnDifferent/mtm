package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Popular Bookmark
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PopularBookmarkDTO implements Serializable {

    /**
     * Title
     */
    private String title;
    /**
     * Url
     */
    private String url;
    /**
     * Image
     */
    private String img;

    /**
     * Description
     */
    private String desc;

    /**
     * The number of users who bookmarked the website
     */
    private Integer count;

    private static final long serialVersionUID = 1L;
}