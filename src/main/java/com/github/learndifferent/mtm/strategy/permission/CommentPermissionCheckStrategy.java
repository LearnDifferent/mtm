package com.github.learndifferent.mtm.strategy.permission;

import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.ActionType;
import com.github.learndifferent.mtm.constant.consist.PermissionCheckConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.entity.BookmarkDO;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.query.PermissionCheckRequest;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Check if the user has permission to access the bookmark
 *
 * @author zhou
 * @date 2023/10/13
 */
@Component(PermissionCheckConstant.COMMENT)
@RequiredArgsConstructor
@Slf4j
public class CommentPermissionCheckStrategy implements PermissionCheckStrategy {

    private final BookmarkMapper bookmarkMapper;

    @Override
    public void check(PermissionCheckRequest permissionCheckRequest) {
        Long id = permissionCheckRequest.getId();
        Long userId = permissionCheckRequest.getUserId();
        ActionType actionType = permissionCheckRequest.getActionType();

        if (ActionType.READ.equals(actionType)) {
            checkBookmark(id, userId);
        }
    }

    private void checkBookmark(long bookmarkId, long userId) {
        log.info("Checking comment access permission. Bookmark ID: {}, User ID: {}", bookmarkId, userId);
        BookmarkDO bookmark = bookmarkMapper.getBookmarkById(bookmarkId);
        ThrowExceptionUtils.throwIfNull(bookmark, ResultCode.WEBSITE_DATA_NOT_EXISTS);
        log.info("Bookmark is present: {}", bookmark);

        Boolean isPublic = bookmark.getIsPublic();
        boolean isPrivate = Boolean.FALSE.equals(isPublic);
        log.info("Bookmark is private: {}", isPrivate);

        long ownerUserId = bookmark.getUserId();
        log.info("Bookmark owner user ID: {}, User ID: {}", ownerUserId, userId);

        boolean hasNoPermission = isPrivate && ownerUserId != userId;
        ThrowExceptionUtils.throwIfTrue(hasNoPermission, ResultCode.PERMISSION_DENIED);
    }
}
