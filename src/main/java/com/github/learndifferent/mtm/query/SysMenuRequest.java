package com.github.learndifferent.mtm.query;

import com.github.learndifferent.mtm.validationgroup.OnCreation;
import com.github.learndifferent.mtm.validationgroup.OnUpdate;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
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

    @Positive(groups = OnUpdate.class, message = "Menu ID must be a positive number")
    @NotNull(groups = OnUpdate.class, message = "Menu ID must not be null")
    private Long id;

    @NotBlank(groups = OnCreation.class, message = "Menu name must not be blank")
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
