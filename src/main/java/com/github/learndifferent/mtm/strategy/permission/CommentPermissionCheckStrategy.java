package com.github.learndifferent.mtm.strategy.permission;

import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.ActionType;
import com.github.learndifferent.mtm.constant.consist.ConstraintConstant;
import com.github.learndifferent.mtm.constant.consist.PermissionCheckConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.entity.BookmarkDO;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.mapper.CommentMapper;
import com.github.learndifferent.mtm.query.PermissionCheckRequest;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    private final CommentMapper commentMapper;

    @Override
    public void check(PermissionCheckRequest permissionCheckRequest) {
        Long bookmarkId = permissionCheckRequest.getBookmarkId();
        Long userId = permissionCheckRequest.getUserId();
        Long commentId = permissionCheckRequest.getCommentId();
        ActionType actionType = permissionCheckRequest.getActionType();
        String comment = permissionCheckRequest.getComment();
        Long replyToCommentId = permissionCheckRequest.getReplyToCommentId();

        switch (actionType) {
            case READ:
                checkPermissionByBookmarkAndUser(bookmarkId, userId);
                break;
            case CREATE:
                checkIfNewCommentValid(comment);
                checkPermissionByBookmarkAndUser(bookmarkId, userId);
                checkIfDuplicate(comment, bookmarkId, userId);
                checkIfReplyToCommentPresent(replyToCommentId);
                break;
            case UPDATE:
                checkIfNewCommentValid(comment);
                checkPermissionByBookmarkAndUser(bookmarkId, userId);
                checkIfDuplicate(comment, bookmarkId, userId);
                checkIfReplyToCommentPresent(replyToCommentId);
                // Check if the comment is present and if the user has permission to access it
                checkIfCommentPresentAndUserPermission(commentId, userId);
                break;
            default:
                log.warn("Can't find the valid action type: {}", actionType);
        }
    }

    private void checkPermissionByBookmarkAndUser(long bookmarkId, long userId) {
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

    private void checkIfNewCommentValid(String comment) {
        log.info("Check if the comment is valid: {}", comment);
        boolean isBlank = StringUtils.isBlank(comment);
        ThrowExceptionUtils.throwIfTrue(isBlank, ResultCode.COMMENT_EMPTY);

        boolean isTooLong = comment.length() > ConstraintConstant.COMMENT_MAX_LENGTH;
        ThrowExceptionUtils.throwIfTrue(isTooLong, ResultCode.COMMENT_TOO_LONG);
        log.info("Comment {} is valid", comment);
    }

    private void checkIfDuplicate(String comment, long bookmarkId, long userId) {
        log.info("Check if the comment is duplicate: {}, Bookmark ID: {}, User ID: {}", comment, bookmarkId, userId);
        boolean isPresent = commentMapper.checkIfCommentPresent(comment, bookmarkId, userId);
        ThrowExceptionUtils.throwIfTrue(isPresent, ResultCode.COMMENT_EXISTS);
        log.info("Comment {} is not duplicate, Bookmark ID: {}, User ID: {}", comment, bookmarkId, userId);
    }

    private void checkIfReplyToCommentPresent(Long replyToCommentId) {
        log.info("Check if the reply to comment is present: {}", replyToCommentId);
        if (Objects.nonNull(replyToCommentId)) {
            log.info("This is no a reply, it's a comment");
        }
        boolean isPresent = commentMapper.checkIfCommentPresentById(replyToCommentId);
        ThrowExceptionUtils.throwIfTrue(isPresent, ResultCode.COMMENT_NOT_EXISTS);
        log.info("Reply to comment is present: {}", replyToCommentId);
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
