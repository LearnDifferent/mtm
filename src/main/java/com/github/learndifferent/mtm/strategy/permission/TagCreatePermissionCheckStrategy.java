package com.github.learndifferent.mtm.strategy.permission;

import com.github.learndifferent.mtm.annotation.common.AnnotationHelper;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.BookmarkId;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.Tag;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.UserId;
import com.github.learndifferent.mtm.constant.consist.PermissionCheckConstant;
import com.github.learndifferent.mtm.manager.PermissionManager;
import java.lang.annotation.Annotation;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final PermissionManager permissionManager;

    @Override
    public void checkPermission(Annotation[][] parameterAnnotations, Object[] args) {
        long bookmarkId = -1L;
        long userId = -1L;
        String tag = "";

        AnnotationHelper helper = new AnnotationHelper(BookmarkId.class, UserId.class, Tag.class);

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
                if (helper.hasNotFoundAnnotation(Tag.class)
                        && annotation instanceof Tag
                        && Objects.nonNull(curArg)
                        && String.class.isAssignableFrom(curArg.getClass())) {
                    tag = (String) curArg;
                    helper.findAnnotation(Tag.class);
                    break;
                }
            }
            if (helper.hasFoundAllRequiredAnnotations()) {
                break;
            }
        }

        helper.checkIfFoundAllRequiredAnnotations();

        check(bookmarkId, userId, tag);
    }

    private void check(long bookmarkId, long userId, String tag) {
        permissionManager.checkIfTagValid(tag);
        permissionManager.checkIfOwner(bookmarkId, userId);

        log.info("Checking permission to add new tag. Bookmark ID: {}, User ID: {}", bookmarkId, userId);
        permissionManager.checkIfOwner(bookmarkId, userId);
        log.info("User {} has permission to modify tag (bookmark ID: {})", userId, bookmarkId);

        permissionManager.checkIfTagPresent(bookmarkId, tag);
    }
}