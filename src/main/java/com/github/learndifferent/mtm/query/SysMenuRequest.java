package com.github.learndifferent.mtm.query;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * System Menu Request
 *
 * @author zhou
 * @date 2024/4/18
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysMenuRequest implements Serializable {

    private String menuName;
    /**
     * Parent Menu ID
     */
    private Long parentId;
    /**
     * Display Order
     */
    private Integer displayOrder;
    private String permissions;
    /**
     * Routing Address
     */
    private String path;
    /**
     * Vue Component
     */
    private String component;
    /**
     * Vue Component Query (if any)
     */
    private String query;
    private Boolean isExternalLink;
    private Boolean isCache;
    private Boolean isHidden;
    private String materialDesignIcon;
    private String createdBy;
    private String updatedBy;
    private String remark;

    private static final long serialVersionUID = 1L;
}
