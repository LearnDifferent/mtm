package com.github.learndifferent.mtm.strategy.permission;

import com.github.learndifferent.mtm.constant.consist.ConstraintConstant;
import com.github.learndifferent.mtm.constant.consist.PermissionCheckConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.mapper.TagMapper;
import com.github.learndifferent.mtm.query.PermissionCheckRequest;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    private final BookmarkMapper bookmarkMapper;
    private final TagMapper tagMapper;

    @Override
    public void checkPermission(PermissionCheckRequest permissionCheckRequest) {
        Long bookmarkId = permissionCheckRequest.getBookmarkId();
        Long userId = permissionCheckRequest.getUserId();
        String tag = permissionCheckRequest.getTag();

        checkIsValid(tag);
        checkPermission(bookmarkId, userId);
        checkIsPresent(bookmarkId, tag);
    }

    private void checkIsValid(String tag) {
        log.info("Check if the tag is valid: {}", tag);
        boolean isBlank = StringUtils.isBlank(tag);
        ThrowExceptionUtils.throwIfTrue(isBlank, ResultCode.TAG_NOT_EXISTS);

        boolean hasExceeded = tag.length() > ConstraintConstant.TAG_MAX_LENGTH;
        ThrowExceptionUtils.throwIfTrue(hasExceeded, ResultCode.TAG_TOO_LONG);

        boolean isTooShort = tag.length() < ConstraintConstant.TAG_MIN_LENGTH;
        ThrowExceptionUtils.throwIfTrue(isTooShort, ResultCode.TAG_TOO_SHORT);
        log.info("Tag {} is valid", tag);
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

    private void checkIsPresent(Long bookmarkId, String tag) {
        log.info("Checking if the tag already exists. Tag: {}, Bookmark ID: {}", tag, bookmarkId);
        boolean isPresent = tagMapper.checkIfTagExists(tag, bookmarkId);
        ThrowExceptionUtils.throwIfTrue(isPresent, ResultCode.TAG_EXISTS);
        log.info("Tag {} is checked. Bookmark ID: {}.", tag, bookmarkId);
    }

}