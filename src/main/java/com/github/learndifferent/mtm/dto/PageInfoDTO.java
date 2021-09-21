package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 页面信息。
 * 通过 {@link com.github.learndifferent.mtm.annotation.general.page.PageInfoMethodArgumentResolver} 从 Request 中获取
 *
 * @author zhou
 * @date 2021/09/05
 * @see com.github.learndifferent.mtm.annotation.general.page.PageInfoMethodArgumentResolver
 * @see com.github.learndifferent.mtm.annotation.general.page.PageInfo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class PageInfoDTO implements Serializable {

    private Integer from;

    private Integer size;

    private static final long serialVersionUID = 1L;
}
