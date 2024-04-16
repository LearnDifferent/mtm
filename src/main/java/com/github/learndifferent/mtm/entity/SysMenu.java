package com.github.learndifferent.mtm.entity;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * System Menu
 *
 * @author zhou
 * @date 2024/4/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysMenu {

    private Long id;
    private String menuName;
    /**
     * Parent Menu ID (Not Null)
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
    private Instant creationTime;
    private String updatedBy;
    private Instant updateTime;
    private String remark;

    /**
     * Children Menu
     */
    private List<SysMenu> children;
}
