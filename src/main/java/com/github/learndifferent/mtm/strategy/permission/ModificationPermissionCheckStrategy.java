package com.github.learndifferent.mtm.strategy.permission;

import com.github.learndifferent.mtm.query.PermissionCheckRequest;

/**
 * Data modification permission check strategy
 *
 * @author zhou
 * @date 2023/10/12
 */
public interface ModificationPermissionCheckStrategy {

    /**
     * Check data modification permission
     *
     * @param permissionCheckRequest permission check request
     */
    void check(PermissionCheckRequest permissionCheckRequest);
}