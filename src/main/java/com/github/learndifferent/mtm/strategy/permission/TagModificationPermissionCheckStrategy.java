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
 * @author zhou
 * @date 2023/10/13
 */
@Component(PermissionCheckConstant.TAG)
@RequiredArgsConstructor
@Slf4j
public class TagModificationPermissionCheckStrategy implements ModificationPermissionCheckStrategy {

    private final BookmarkMapper bookmarkMapper;
    private final TagMapper tagMapper;

    @Override
    public void check(PermissionCheckRequest permissionCheckRequest) {
        Long id = permissionCheckRequest.getId();
        Long userId = permissionCheckRequest.getUserId();
        String tag = permissionCheckRequest.getTag();

        log.info("Check if the tag is valid: {}", tag);
        boolean isBlank = StringUtils.isBlank(tag);
        ThrowExceptionUtils.throwIfTrue(isBlank, ResultCode.TAG_NOT_EXISTS);

        boolean hasExceeded = tag.length() > ConstraintConstant.TAG_MAX_LENGTH;
        ThrowExceptionUtils.throwIfTrue(hasExceeded, ResultCode.TAG_TOO_LONG);

        boolean isTooShort = tag.length() < ConstraintConstant.TAG_MIN_LENGTH;
        ThrowExceptionUtils.throwIfTrue(isTooShort, ResultCode.TAG_TOO_SHORT);
        log.info("Tag {} is valid", tag);

        log.info("Checking permission. Bookmark ID: {}, User ID: {}", id, userId);
        boolean hasNoBookmarkPermission = !bookmarkMapper.checkModificationPermission(id, userId);
        if (hasNoBookmarkPermission) {
            log.info("User {} has no permission to modify tag (bookmark ID: {})", userId, id);
            throw new ServiceException(ResultCode.PERMISSION_DENIED);
        }
        log.info("User {} has permission to modify tag (bookmark ID: {})", userId, id);

        log.info("Checking if the tag already exists. Tag: {}, Bookmark ID: {}", tag, id);
        boolean isPresent = tagMapper.checkIfTagExists(tag, id);
        ThrowExceptionUtils.throwIfTrue(isPresent, ResultCode.TAG_EXISTS);
        log.info("Tag {} is checked. Bookmark ID: {}.", tag, id);
    }
}
