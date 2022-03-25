package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Visited Bookmarks Data Transfer Object
 *
 * @author zhou
 * @date 2022/3/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class VisitedBookmarksDTO implements Serializable {

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
     * ID of the website data
     */
    private Integer webId;

    /**
     * True if this is a public bookmarks
     */
    private Boolean isPublic;

    /**
     * The number of views of the website data
     */
    private Integer views;

    private static final long serialVersionUID = 1L;
}