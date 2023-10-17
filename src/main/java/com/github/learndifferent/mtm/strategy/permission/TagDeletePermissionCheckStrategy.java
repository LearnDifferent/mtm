package com.github.learndifferent.mtm.strategy.permission;

import com.github.learndifferent.mtm.annotation.common.AnnotationHelper;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.BookmarkId;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.UserId;
import com.github.learndifferent.mtm.constant.consist.PermissionCheckConstant;
import com.github.learndifferent.mtm.manager.PermissionManager;
import java.lang.annotation.Annotation;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Check the permission to delete a tag
 *
 * @author zhou
 * @date 2023/10/13
 */
@Component(PermissionCheckConstant.TAG_DELETE)
@RequiredArgsConstructor
@Slf4j
public class TagDeletePermissionCheckStrategy implements PermissionCheckStrategy {

    private final PermissionManager permissionManager;

    @Override
    public void checkPermission(Annotation[][] parameterAnnotations, Object[] args) {
        long bookmarkId = -1L;
        long userId = -1L;

        AnnotationHelper helper = new AnnotationHelper(BookmarkId.class, UserId.class);

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
            }
            if (helper.hasFoundAllRequiredAnnotations()) {
                break;
            }
        }

        helper.checkIfFoundAllRequiredAnnotations();

        permissionManager.checkIfOwner(bookmarkId, userId);
    }
}