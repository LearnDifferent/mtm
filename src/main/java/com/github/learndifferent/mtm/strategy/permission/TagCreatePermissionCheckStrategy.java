package com.github.learndifferent.mtm.strategy.permission;

import com.github.learndifferent.mtm.constant.consist.PermissionCheckConstant;
import com.github.learndifferent.mtm.manager.PermissionManager;
import com.github.learndifferent.mtm.query.PermissionCheckRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Check the permission to access a tag and validate if the tag is valid
 *
 * @author zhou
 * @date 2023/10/13
 */
@Component(PermissionCheckConstant.TAG_CREATE)
@RequiredArgsConstructor
@Slf4j
public class TagCreatePermissionCheckStrategy implements PermissionCheckStrategy {

    private final PermissionManager permissionManager;

    @Override
    public void checkPermission(PermissionCheckRequest permissionCheckRequest) {
        Long bookmarkId = permissionCheckRequest.getBookmarkId();
        Long userId = permissionCheckRequest.getUserId();
        String tag = permissionCheckRequest.getTag();

        permissionManager.checkIfTagValid(tag);
        permissionManager.checkIfOwner(bookmarkId, userId);

        log.info("Checking permission to add new tag. Bookmark ID: {}, User ID: {}", bookmarkId, userId);
        permissionManager.checkIfOwner(bookmarkId, userId);
        log.info("User {} has permission to modify tag (bookmark ID: {})", userId, bookmarkId);

        permissionManager.checkIfTagPresent(bookmarkId, tag);
    }

}