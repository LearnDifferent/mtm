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
public class FindPageInitVO implements Serializable {

    /**
     * 热搜数据
     */
    private Set<String> trendingList;

    /**
     * 是否存在可供搜索的数据
     */
    private Boolean dataStatus;

    /**
     * 是否存在新的更新，
     * 也就是数据库中的 distinct url 的数量是否等于 Elasticsearch 中的数据数量
     */
    private Boolean hasNewUpdate;

    private static final long serialVersionUID = 1L;
}