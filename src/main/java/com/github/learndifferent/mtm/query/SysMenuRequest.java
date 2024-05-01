package com.github.learndifferent.mtm.query;

import com.github.learndifferent.mtm.constant.consist.ErrorInfoConstant;
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

    @Positive(groups = OnUpdate.class, message = ErrorInfoConstant.MENU_ID_NOT_POSITIVE)
    @NotNull(groups = OnUpdate.class, message = ErrorInfoConstant.MENU_ID_BLANK)
    private Long id;

    @NotBlank(groups = OnCreation.class, message = ErrorInfoConstant.MENU_NAME_BLANK)
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
