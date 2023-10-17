package com.github.learndifferent.mtm.strategy.permission;

import com.github.learndifferent.mtm.constant.consist.PermissionCheckConstant;
import com.github.learndifferent.mtm.manager.PermissionManager;
import com.github.learndifferent.mtm.query.PermissionCheckRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Check if the user has permission to access the bookmark
 *
 * @author zhou
 * @date 2023/10/12
 */
@Component(PermissionCheckConstant.BOOKMARK)
@RequiredArgsConstructor
public class BookmarkPermissionCheckStrategy implements PermissionCheckStrategy {

    private final PermissionManager permissionManager;

    @Override
    public void checkPermission(PermissionCheckRequest permissionCheckRequest) {
        Long bookmarkId = permissionCheckRequest.getBookmarkId();
        Long userId = permissionCheckRequest.getUserId();
        permissionManager.checkIfOwner(bookmarkId, userId);
    }
}
