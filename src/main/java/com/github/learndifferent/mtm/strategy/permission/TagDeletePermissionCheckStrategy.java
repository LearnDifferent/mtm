package com.github.learndifferent.mtm.strategy.permission;

import com.github.learndifferent.mtm.constant.consist.PermissionCheckConstant;
import com.github.learndifferent.mtm.manager.PermissionManager;
import com.github.learndifferent.mtm.query.PermissionCheckRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Check the permission to delete a tag
 *
 * @author zhou
 * @date 2023/10/13
 */
@Component(PermissionCheckConstant.TAG_DELETE)
@RequiredArgsConstructor
@Slf4j
public class TagDeletePermissionCheckStrategy implements PermissionCheckStrategy {

    private final PermissionManager permissionManager;

    @Override
    public void checkPermission(PermissionCheckRequest permissionCheckRequest) {
        Long bookmarkId = permissionCheckRequest.getBookmarkId();
        Long userId = permissionCheckRequest.getUserId();

        permissionManager.checkIfOwner(bookmarkId, userId);
    }
}