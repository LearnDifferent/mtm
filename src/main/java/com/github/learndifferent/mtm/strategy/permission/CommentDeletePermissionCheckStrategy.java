package com.github.learndifferent.mtm.strategy.permission;

import com.github.learndifferent.mtm.constant.consist.PermissionCheckConstant;
import com.github.learndifferent.mtm.manager.PermissionManager;
import com.github.learndifferent.mtm.query.PermissionCheckRequest;
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

    private final PermissionManager permissionManager;

    @Override
    public void checkPermission(PermissionCheckRequest permissionCheckRequest) {
        Long userId = permissionCheckRequest.getUserId();
        Long commentId = permissionCheckRequest.getCommentId();
        permissionManager.checkIfCommentPresentAndUserPermissionGranted(commentId, userId);
    }
}
