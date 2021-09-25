package com.github.learndifferent.mtm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * WebsiteWithCount 的 dto
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
