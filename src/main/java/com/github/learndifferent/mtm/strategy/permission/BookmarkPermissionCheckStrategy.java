package com.github.learndifferent.mtm.strategy.permission;

import com.github.learndifferent.mtm.constant.consist.PermissionCheckConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.query.PermissionCheckRequest;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
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

    private final BookmarkMapper bookmarkMapper;

    @Override
    public void check(PermissionCheckRequest permissionCheckRequest) {
        Long bookmarkId = permissionCheckRequest.getBookmarkId();
        Long userId = permissionCheckRequest.getUserId();
        boolean hasNoPermission = !bookmarkMapper.checkModificationPermission(bookmarkId, userId);
        ThrowExceptionUtils.throwIfTrue(hasNoPermission, ResultCode.PERMISSION_DENIED);
    }
}
