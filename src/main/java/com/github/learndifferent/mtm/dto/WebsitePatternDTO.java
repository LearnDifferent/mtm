package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * WebsitePattern 的 dto
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class WebsitePatternDTO implements Serializable {

    /**
     * Paginated website data
     */
    private List<? extends WebsiteDTO> webs;
    /**
     * Total pages
     */
    Integer totalPage;

    private static final long serialVersionUID = 1L;
}
