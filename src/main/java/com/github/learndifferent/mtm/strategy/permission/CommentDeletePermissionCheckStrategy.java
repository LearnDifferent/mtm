package com.github.learndifferent.mtm.strategy.permission;

import com.github.learndifferent.mtm.annotation.common.AnnotationHelper;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.CommentId;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.UserId;
import com.github.learndifferent.mtm.constant.consist.PermissionCheckConstant;
import com.github.learndifferent.mtm.manager.PermissionManager;
import java.lang.annotation.Annotation;
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

    private final PermissionManager permissionManager;

    @Override
    public void checkPermission(Annotation[][] parameterAnnotations, Object[] args) {
        long userId = -1L;
        long commentId = -1L;

        AnnotationHelper helper = new AnnotationHelper(UserId.class, CommentId.class);

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                Object curArg = args[i];
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

            }
            if (helper.hasFoundAllRequiredAnnotations()) {
                break;
            }
        }

        helper.checkIfFoundAllRequiredAnnotations();

        permissionManager.checkIfCommentPresentAndUserPermissionGranted(commentId, userId);
    }
}
