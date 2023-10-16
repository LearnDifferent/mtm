package com.github.learndifferent.mtm.strategy.permission;

import com.github.learndifferent.mtm.query.PermissionCheckRequest;

/**
 * Data access permission check strategy
 *
 * @author zhou
 * @date 2023/10/12
 */
public interface PermissionCheckStrategy {

    /**
     * Check data access permission
     *
     * @param permissionCheckRequest permission check request
     */
    void checkPermission(PermissionCheckRequest permissionCheckRequest);
}