package com.github.learndifferent.mtm.strategy.permission;

import com.github.learndifferent.mtm.annotation.common.AnnotationHelper;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.BookmarkId;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.Comment;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.CommentId;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.ReplyToCommentId;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.UserId;
import com.github.learndifferent.mtm.constant.consist.PermissionCheckConstant;
import com.github.learndifferent.mtm.manager.PermissionManager;
import java.lang.annotation.Annotation;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Check if the user has permission to update the comment
 *
 * @author zhou
 * @date 2023/10/13
 */
@Component(PermissionCheckConstant.COMMENT_UPDATE)
@RequiredArgsConstructor
@Slf4j
public class CommentUpdatePermissionCheckStrategy implements PermissionCheckStrategy {

    private final PermissionManager permissionManager;

    @Override
    public void checkPermission(Annotation[][] parameterAnnotations, Object[] args) {
        long bookmarkId = -1L;
        long userId = -1L;
        long commentId = -1L;
        String comment = "";
        Long replyToCommentId = null;

        AnnotationHelper helper = new AnnotationHelper(
                BookmarkId.class, UserId.class, CommentId.class, Comment.class, ReplyToCommentId.class);

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                Object curArg = args[i];
                if (helper.hasNotFoundAnnotation(BookmarkId.class)
                        && annotation instanceof BookmarkId
                        && Objects.nonNull(curArg)
                        && Long.class.isAssignableFrom(curArg.getClass())) {
                    bookmarkId = (long) curArg;
                    helper.findAnnotation(BookmarkId.class);
                    break;
                }
                if (helper.hasNotFoundAnnotation(UserId.class)
                        && annotation instanceof UserId
                        && Objects.nonNull(curArg)
                        && Long.class.isAssignableFrom(curArg.getClass())) {
                    userId = (long) curArg;
                    helper.findAnnotation(UserId.class);
                    break;
                }
                if (helper.hasNotFoundAnnotation(CommentId.class)
                        && annotation instanceof CommentId
                        && Objects.nonNull(curArg)
                        && Long.class.isAssignableFrom(curArg.getClass())) {
                    commentId = (long) curArg;
                    helper.findAnnotation(CommentId.class);
                    break;
                }
                if (helper.hasNotFoundAnnotation(Comment.class)
                        && annotation instanceof Comment
                        && Objects.nonNull(curArg)
                        && String.class.isAssignableFrom(curArg.getClass())) {
                    comment = (String) curArg;
                    helper.findAnnotation(Comment.class);
                    break;
                }
                if (helper.hasNotFoundAnnotation(ReplyToCommentId.class)
                        && annotation instanceof ReplyToCommentId) {
                    helper.findAnnotation(ReplyToCommentId.class);

                    if (Objects.nonNull(curArg)
                            && Long.class.isAssignableFrom(curArg.getClass())) {
                        // The ReplyToCommentId can be null, but since its initial value is already null,
                        // here we only consider the case when it has a long value.
                        replyToCommentId = (Long) curArg;
                    }
                    break;
                }
            }
            if (helper.hasFoundAllRequiredAnnotations()) {
                break;
            }
        }

        helper.checkIfFoundAllRequiredAnnotations();

        check(bookmarkId, userId, commentId, comment, replyToCommentId);
    }

    private void check(long bookmarkId, long userId, long commentId, String comment, Long replyToCommentId) {
        permissionManager.checkIfCommentValid(comment);

        log.info("Checking comment access permission. Bookmark ID: {}, User ID: {}", bookmarkId, userId);
        permissionManager.checkUserAccessBookmarkPermission(bookmarkId, userId);
        log.info("Checked comment access permission. Bookmark ID: {}, User ID: {}", bookmarkId, userId);

        permissionManager.checkIfCommentDuplicate(comment, bookmarkId, userId);

        permissionManager.checkIfReplyToCommentPresent(replyToCommentId);

        permissionManager.checkIfCommentPresentAndUserPermissionGranted(commentId, userId);
    }
}