package com.github.learndifferent.mtm.vo;

import com.github.learndifferent.mtm.dto.WebsiteDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 根据过滤器，查询出来的需要展示的网页数据及其个数
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class WebsByFilterVO implements Serializable {

    /**
     * 筛选出来的网页（筛选条件包括了分页）
     */
    List<WebsiteDTO> webs;
    /**
     * 筛选出来的结果的条数（前端用来和之前的结果做对比）
     */
    Integer count;

    private static final long serialVersionUID = 1L;
}