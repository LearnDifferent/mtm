package com.github.learndifferent.mtm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * WebsiteWithCount 的 dto
 *
 * @author zhou
 * @date 2021/09/05
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteWithCountDTO extends WebsiteDTO {

    /**
     * 新增：有多少人收藏了这个 URL
     */
    private Integer count;
}
