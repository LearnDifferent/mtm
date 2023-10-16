package com.github.learndifferent.mtm.strategy.permission;

import com.github.learndifferent.mtm.constant.consist.PermissionCheckConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.mapper.CommentMapper;
import com.github.learndifferent.mtm.query.PermissionCheckRequest;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Check if the user has permission to delete the comment
 *
 * @author zhou
 * @date 2023/10/13
 */
@Component(PermissionCheckConstant.COMMENT_DELETE)
@RequiredArgsConstructor
@Slf4j
public class CommentDeletePermissionCheckStrategy implements PermissionCheckStrategy {

    private final CommentMapper commentMapper;

    @Override
    public void checkPermission(PermissionCheckRequest permissionCheckRequest) {
        Long userId = permissionCheckRequest.getUserId();
        Long commentId = permissionCheckRequest.getCommentId();

        checkIfCommentPresentAndUserPermission(commentId, userId);
    }

    private void checkIfCommentPresentAndUserPermission(long commentId, long userId) {
        log.info("Check if the comment is present: {}", commentId);
        Long commentUserId = commentMapper.getCommentUserIdByCommentId(commentId);
        boolean isNotPresent = Objects.isNull(commentUserId);
        ThrowExceptionUtils.throwIfTrue(isNotPresent, ResultCode.COMMENT_NOT_EXISTS);
        log.info("Comment is present: {}", commentId);

        log.info("Check if the user has permission to access the comment: {}, User ID: {}", commentId, userId);
        boolean hasNoPermission = !commentUserId.equals(userId);
        ThrowExceptionUtils.throwIfTrue(hasNoPermission, ResultCode.PERMISSION_DENIED);
        log.info("User has permission to access the comment: {}, User ID: {}", commentId, userId);
    }
}
