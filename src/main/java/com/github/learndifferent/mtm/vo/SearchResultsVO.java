package com.github.learndifferent.mtm.vo;

import com.github.learndifferent.mtm.dto.WebForSearchDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

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
public class SearchResultsVO implements Serializable {

    private Long totalCount;
    private Integer totalPage;
    /**
     * 需要展示的搜索出来的网页数据（经过了分页）
     */
    private List<WebForSearchDTO> webs;

    private static final long serialVersionUID = 1L;
}