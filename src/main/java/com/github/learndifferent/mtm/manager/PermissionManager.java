package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.constant.consist.ConstraintConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.entity.BookmarkDO;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.mapper.CommentMapper;
import com.github.learndifferent.mtm.mapper.TagMapper;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Check permissions
 *
 * @author zhou
 * @date 2023/10/16
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionManager {

    private final BookmarkMapper bookmarkMapper;
    private final CommentMapper commentMapper;
    private final TagMapper tagMapper;

    public void checkIfOwner(long bookmarkId, long userId) {
        log.info("Checking if user {} is the owner of bookmark {}", userId, bookmarkId);
        boolean hasNoPermission = !bookmarkMapper.checkIfOwner(bookmarkId, userId);
        ThrowExceptionUtils.throwIfTrue(hasNoPermission, ResultCode.PERMISSION_DENIED);
        log.info("User {} is the owner of bookmark {}", userId, bookmarkId);
    }

    public void checkUserAccessBookmarkPermission(long bookmarkId, long userId) {
        log.info("Checking bookmark access permission. Bookmark ID: {}, User ID: {}", bookmarkId, userId);
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
        log.info("Bookmark access permission granted.");
    }

    public void checkIfCommentValid(String comment) {
        log.info("Checking if the comment is valid: {}", comment);
        boolean isBlank = StringUtils.isBlank(comment);
        ThrowExceptionUtils.throwIfTrue(isBlank, ResultCode.COMMENT_EMPTY);

        boolean isTooLong = comment.length() > ConstraintConstant.COMMENT_MAX_LENGTH;
        ThrowExceptionUtils.throwIfTrue(isTooLong, ResultCode.COMMENT_TOO_LONG);
        log.info("Comment {} is valid", comment);
    }

    public void checkIfCommentDuplicate(String comment, long bookmarkId, long userId) {
        log.info("Checking if the comment is duplicate: {}, Bookmark ID: {}, User ID: {}", comment, bookmarkId, userId);
        boolean isPresent = commentMapper.checkIfCommentPresent(comment, bookmarkId, userId);
        ThrowExceptionUtils.throwIfTrue(isPresent, ResultCode.COMMENT_EXISTS);
        log.info("Comment {} is not duplicate, Bookmark ID: {}, User ID: {}", comment, bookmarkId, userId);
    }

    public void checkIfReplyToCommentPresent(Long replyToCommentId) {
        log.info("Checking if the reply to comment is present: {}", replyToCommentId);
        if (Objects.nonNull(replyToCommentId)) {
            log.info("This is no a reply, it's a comment");
        }
        boolean isPresent = commentMapper.checkIfCommentPresentById(replyToCommentId);
        ThrowExceptionUtils.throwIfTrue(isPresent, ResultCode.COMMENT_NOT_EXISTS);
        log.info("Reply to comment is present: {}", replyToCommentId);
    }

    public void checkIfCommentPresentAndUserPermissionGranted(long commentId, long userId) {
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

    public void checkIfTagValid(String tag) {
        log.info("Checking if the tag is valid: {}", tag);
        boolean isBlank = StringUtils.isBlank(tag);
        ThrowExceptionUtils.throwIfTrue(isBlank, ResultCode.TAG_NOT_EXISTS);

        boolean hasExceeded = tag.length() > ConstraintConstant.TAG_MAX_LENGTH;
        ThrowExceptionUtils.throwIfTrue(hasExceeded, ResultCode.TAG_TOO_LONG);

        boolean isTooShort = tag.length() < ConstraintConstant.TAG_MIN_LENGTH;
        ThrowExceptionUtils.throwIfTrue(isTooShort, ResultCode.TAG_TOO_SHORT);
        log.info("Tag {} is valid", tag);
    }

    public void checkIfTagPresent(Long bookmarkId, String tag) {
        log.info("Checking if the tag already exists. Tag: {}, Bookmark ID: {}", tag, bookmarkId);
        boolean isPresent = tagMapper.checkIfTagExists(tag, bookmarkId);
        ThrowExceptionUtils.throwIfTrue(isPresent, ResultCode.TAG_EXISTS);
        log.info("Tag {} is checked. Bookmark ID: {}.", tag, bookmarkId);
    }
}