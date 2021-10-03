package com.github.learndifferent.mtm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * WebsiteWithCount 的 dto。
 * 实际上只有 count, url, title, desc 和 img 属性，这里为了方便就直接继承了 {@link WebsiteDTO}
 *
 * @author zhou
 * @date 2021/09/05
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteWithCountDTO extends WebsiteDTO {

    /**
     * New field: the number of users who mark the URL
     */
    private Integer count;
}
