package com.github.learndifferent.mtm.strategy.permission;

import com.github.learndifferent.mtm.constant.consist.PermissionCheckConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.query.PermissionCheckRequest;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Check if the user has permission to modify the bookmark
 *
 * @author zhou
 * @date 2023/10/12
 */
@Component(PermissionCheckConstant.BOOKMARK)
@RequiredArgsConstructor
public class BookmarkModificationPermissionCheckStrategy implements ModificationPermissionCheckStrategy {

    private final BookmarkMapper bookmarkMapper;

    @Override
    public void check(PermissionCheckRequest permissionCheckRequest) {
        Long id = permissionCheckRequest.getId();
        Long userId = permissionCheckRequest.getUserId();
        boolean hasNoPermission = !bookmarkMapper.checkModificationPermission(id, userId);
        ThrowExceptionUtils.throwIfTrue(hasNoPermission, ResultCode.PERMISSION_DENIED);
    }
}
