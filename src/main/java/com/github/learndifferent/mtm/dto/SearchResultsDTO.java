package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 搜索结果
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class SearchResultsDTO implements Serializable {

    /**
     * Total number of results
     */
    private Long totalCount;
    /**
     * Total pages of results
     */
    private Integer totalPage;
    /**
     * Paginated search results
     */
    private List<WebForSearchDTO> webs;

    private static final long serialVersionUID = 1L;
}