package com.github.learndifferent.mtm.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Check permission
 *
 * @author zhou
 * @date 2023/10/12
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PermissionCheckRequest {

    /**
     * ID
     */
    private Long id;

    /**
     * User ID
     */
    private Long userId;

    /**
     * Tag
     */
    private String tag;
}