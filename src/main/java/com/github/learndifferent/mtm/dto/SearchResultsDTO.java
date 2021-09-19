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
     * 查询到的所有结果的总数
     */
    private Long totalCount;
    /**
     * 搜索结果的总页数
     */
    private Integer totalPage;
    /**
     * 搜索结果在分页后得到的网页数据
     */
    private List<WebForSearchDTO> webs;

    private static final long serialVersionUID = 1L;
}