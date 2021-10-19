package com.github.learndifferent.mtm.vo;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Status of Data for Search
 *
 * @author zhou
 * @date 2021/10/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class SearchDataStatusVO implements Serializable {

    /**
     * Existence of data
     */
    private Boolean exists;

    /**
     * Changes of data
     */
    private Boolean hasChanges;

    private static final long serialVersionUID = 1L;
}
