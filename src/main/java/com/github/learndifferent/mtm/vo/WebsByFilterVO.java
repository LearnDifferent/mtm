package com.github.learndifferent.mtm.vo;

import com.github.learndifferent.mtm.dto.WebsiteDTO;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
     * Filtered Paginated Website Data
     */
    List<WebsiteDTO> webs;
    /**
     * Total number of filtered data
     * (frontend will compare it with the previous result)
     */
    Integer count;

    private static final long serialVersionUID = 1L;
}