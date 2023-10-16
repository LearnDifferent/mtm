package com.github.learndifferent.mtm.strategy.permission;

import com.github.learndifferent.mtm.constant.consist.PermissionCheckConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
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

    private final BookmarkMapper bookmarkMapper;

    @Override
    public void checkPermission(PermissionCheckRequest permissionCheckRequest) {
        Long bookmarkId = permissionCheckRequest.getBookmarkId();
        Long userId = permissionCheckRequest.getUserId();

        checkPermission(bookmarkId, userId);
    }

    private void checkPermission(Long bookmarkId, Long userId) {
        log.info("Checking permission. Bookmark ID: {}, User ID: {}", bookmarkId, userId);
        boolean hasNoBookmarkPermission = !bookmarkMapper.checkModificationPermission(bookmarkId, userId);
        if (hasNoBookmarkPermission) {
            log.info("User {} has no permission to modify tag (bookmark ID: {})", userId, bookmarkId);
            throw new ServiceException(ResultCode.PERMISSION_DENIED);
        }
        log.info("User {} has permission to modify tag (bookmark ID: {})", userId, bookmarkId);
    }
}