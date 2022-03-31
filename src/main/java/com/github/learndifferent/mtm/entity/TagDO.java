package com.github.learndifferent.mtm.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Tag Data Object
 *
 * @author zhou
 * @date 2022/3/31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class TagDO implements Serializable {

    /**
     * Tag
     */
    private String tag;

    /**
     * ID of the bookmarked website data that tag applied to
     */
    private Integer webId;

    private static final long serialVersionUID = 1L;
}