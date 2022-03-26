package com.github.learndifferent.mtm.dto;

import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.annotation.general.page.PageInfoMethodArgumentResolver;
import com.github.learndifferent.mtm.constant.enums.PageInfoParam;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Pagination Info.
 * <p>Request parameter annotated with {@link PageInfo} will be converted to {@link PageInfoDTO}
 * by {@link PageInfoMethodArgumentResolver}</p>
 * <p>The value of {@link PageInfo#size()} in {@link PageInfo} annotation will
 * be set to {@link PageInfoDTO#size}.</p>
 * <p>If the {@link PageInfo#paramName()} in {@link PageInfo} is {@link PageInfoParam#CURRENT_PAGE currentPage},
 * then the string value will be recognized as current page and will be calculated to be {@link PageInfoDTO#from}.</p>
 * <p>If the {@link PageInfo#paramName()} is {@link PageInfoParam#FROM from}, it will simply be set to
 * {@link PageInfoDTO#from}</p>
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class PageInfoDTO implements Serializable {

    /**
     * From
     */
    private Integer from;

    /**
     * Size
     */
    private Integer size;

    private static final long serialVersionUID = 1L;
}
