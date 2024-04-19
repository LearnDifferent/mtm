package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * System Menu Data Transfer Object for creating a new menu
 *
 * @author zhou
 * @date 2024/4/17
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysMenuDTO implements Serializable {

    private Long id;

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
