package com.github.learndifferent.mtm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Website data with privacy settings and the count of its comments
 *
 * @author zhou
 * @date 2021/9/30
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebWithPrivacyCommentCountDTO extends WebsiteWithPrivacyDTO {

    /**
     * New field: The count of post comments
     */
    private Integer commentCount;
}
