package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Additional information of a bookmarked website
 *
 * @author zhou
 * @date 2022/4/3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class WebMoreInfoDTO implements Serializable {

    /**
     * A tag of the bookmarked website
     */
    private String tag;

    /**
     * Number of comments of the bookmarked website
     */
    private Integer commentCount;
}
