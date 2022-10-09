package com.github.learndifferent.mtm.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * View Data Object
 *
 * @author zhou
 * @date 2022/3/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class ViewDataDO implements Serializable {

    /**
     * ID of the bookmark
     */
    private Integer bookmarkId;

    /**
     * The number of views of the bookmark
     */
    private Integer views;

    private static final long serialVersionUID = 1L;
}