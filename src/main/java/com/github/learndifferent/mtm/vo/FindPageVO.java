package com.github.learndifferent.mtm.vo;

import java.io.Serializable;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Find页面初始化。
 * <p>进入 Find 页面的时候，需要展示的数据</p>
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class FindPageVO implements Serializable {

    /**
     * Trending searches
     */
    private Set<String> trendingList;

    /**
     * True if search data is available
     */
    private Boolean dataStatus;

    /**
     * True if the data in database is different from the data in Elasticsearch
     */
    private Boolean hasNewUpdate;

    private static final long serialVersionUID = 1L;
}