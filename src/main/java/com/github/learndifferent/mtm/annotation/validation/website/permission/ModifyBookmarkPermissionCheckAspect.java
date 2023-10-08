package com.github.learndifferent.mtm.annotation.validation.website.permission;

import com.github.learndifferent.mtm.annotation.common.AnnotationHelper;
import com.github.learndifferent.mtm.annotation.common.BookmarkId;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.utils.CustomStringUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.learndifferent.mtm.vo.BookmarkVO;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Verify whether the user has permission to modify the bookmark.
 *
 * @author zhou
 * @date 2021/09/05
 */
@Aspect
@Component
@RequiredArgsConstructor
public class ModifyBookmarkPermissionCheckAspect {

    private final BookmarkMapper bookmarkMapper;

    @Before("@annotation(bookmarkPermission)")
    public void check(JoinPoint joinPoint, ModifyBookmarkPermissionCheck bookmarkPermission) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object[] args = joinPoint.getArgs();

        int bookmarkId = -1;
        String username = "";

        AnnotationHelper helper = new AnnotationHelper(BookmarkId.class, Username.class);

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (helper.hasNotFoundAnnotation(BookmarkId.class)
                        && annotation instanceof BookmarkId
                        && args[i] != null
                        && Integer.class.isAssignableFrom(args[i].getClass())) {
                    bookmarkId = (int) args[i];
                    helper.findAnnotation(BookmarkId.class);
                    break;
                }
                if (helper.hasNotFoundAnnotation(Username.class)
                        && annotation instanceof Username
                        && args[i] != null
                        && String.class.isAssignableFrom(args[i].getClass())) {
                    username = (String) args[i];
                    helper.findAnnotation(Username.class);
                    break;
                }
            }

            if (helper.hasFoundAllRequiredAnnotations()) {
                break;
            }
        }

        ThrowExceptionUtils.throwIfTrue(bookmarkId < 0, ResultCode.WEBSITE_DATA_NOT_EXISTS);

        BookmarkVO bookmark = bookmarkMapper.getBookmarkWithUsernameById(bookmarkId);
        ThrowExceptionUtils.throwIfNull(bookmark, ResultCode.WEBSITE_DATA_NOT_EXISTS);

        boolean emptyUsername = StringUtils.isEmpty(username);
        ThrowExceptionUtils.throwIfTrue(emptyUsername, ResultCode.USER_NOT_EXIST);

        boolean notTheOwner = CustomStringUtils.notEqualsIgnoreCase(username, bookmark.getUserName());
        ThrowExceptionUtils.throwIfTrue(notTheOwner, ResultCode.PERMISSION_DENIED);
    }
}