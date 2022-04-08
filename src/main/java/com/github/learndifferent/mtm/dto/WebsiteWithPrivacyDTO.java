package com.github.learndifferent.mtm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Website data with privacy settings
 *
 * @author zhou
 * @date 2021/9/23
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteWithPrivacyDTO extends WebsiteDTO {

    /**
     * New field: True if this is a public post
     */
    private Boolean isPublic;
}