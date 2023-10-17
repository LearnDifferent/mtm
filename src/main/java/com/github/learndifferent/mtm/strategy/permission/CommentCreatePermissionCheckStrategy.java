package com.github.learndifferent.mtm.strategy.permission;

import com.github.learndifferent.mtm.constant.consist.PermissionCheckConstant;
import com.github.learndifferent.mtm.manager.PermissionManager;
import com.github.learndifferent.mtm.query.PermissionCheckRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Check if the user has permission to create the comment
 *
 * @author zhou
 * @date 2023/10/13
 */
@Component(PermissionCheckConstant.COMMENT_CREATE)
@RequiredArgsConstructor
@Slf4j
public class CommentCreatePermissionCheckStrategy implements PermissionCheckStrategy {

    private final PermissionManager permissionManager;

    @Override
    public void checkPermission(PermissionCheckRequest permissionCheckRequest) {
        Long bookmarkId = permissionCheckRequest.getBookmarkId();
        Long userId = permissionCheckRequest.getUserId();
        String comment = permissionCheckRequest.getComment();
        Long replyToCommentId = permissionCheckRequest.getReplyToCommentId();

        permissionManager.checkIfCommentValid(comment);

        log.info("Checking comment access permission. Bookmark ID: {}, User ID: {}", bookmarkId, userId);
        permissionManager.checkUserAccessBookmarkPermission(bookmarkId, userId);
        log.info("Checked comment access permission. Bookmark ID: {}, User ID: {}", bookmarkId, userId);

        permissionManager.checkIfCommentDuplicate(comment, bookmarkId, userId);

        permissionManager.checkIfReplyToCommentPresent(replyToCommentId);
    }
}
