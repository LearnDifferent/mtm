package com.github.learndifferent.mtm.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Website Data View Data Object
 *
 * @author zhou
 * @date 2022/3/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class WebDataViewDO implements Serializable {

    /**
     * ID of the website data
     */
    private Integer webId;

    /**
     * The number of views of the website data
     */
    private Integer views;

    private static final long serialVersionUID = 1L;
}