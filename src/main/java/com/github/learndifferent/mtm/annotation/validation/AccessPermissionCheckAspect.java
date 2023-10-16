package com.github.learndifferent.mtm.annotation.validation;

import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.BookmarkId;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.Comment;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.CommentId;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.DataAccessType;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.ReplyToCommentId;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.Tag;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.UserId;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.query.PermissionCheckRequest;
import com.github.learndifferent.mtm.strategy.permission.PermissionCheckStrategy;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * Data access permission check
 *
 * @author zhou
 * @date 2023/10/12
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AccessPermissionCheckAspect {

    private final Map<String, PermissionCheckStrategy> strategies;

    @Before("@annotation(accessPermissionCheck)")
    public void check(JoinPoint joinPoint, AccessPermissionCheck accessPermissionCheck) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object[] args = joinPoint.getArgs();

        long bookmarkId = -1L;
        long userId = -1L;

        String tag = "";
        long commentId = -1L;
        String comment = "";
        Long replyToCommentId = null;

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                Object curArg = args[i];
                if (bookmarkId < 0L
                        && annotation instanceof BookmarkId
                        && Objects.nonNull(curArg)
                        && Long.class.isAssignableFrom(curArg.getClass())) {
                    bookmarkId = (long) curArg;
                }
                if (userId < 0L
                        && annotation instanceof UserId
                        && Objects.nonNull(curArg)
                        && Long.class.isAssignableFrom(curArg.getClass())) {
                    userId = (long) curArg;
                }
                if (commentId < 0L
                        && annotation instanceof CommentId
                        && Objects.nonNull(curArg)
                        && Long.class.isAssignableFrom(curArg.getClass())) {
                    commentId = (long) curArg;
                }
                if (StringUtils.isBlank(tag)
                        && annotation instanceof Tag
                        && Objects.nonNull(curArg)
                        && String.class.isAssignableFrom(curArg.getClass())) {
                    tag = (String) curArg;
                }
                if (StringUtils.isBlank(comment)
                        && annotation instanceof Comment
                        && Objects.nonNull(curArg)
                        && String.class.isAssignableFrom(curArg.getClass())) {
                    comment = (String) curArg;
                }
                if (annotation instanceof ReplyToCommentId
                        // The ReplyToCommentId can be null, but since its initial value is already null,
                        // here we only consider the case when it has a long value.
                        && Objects.nonNull(curArg)
                        && Long.class.isAssignableFrom(curArg.getClass())) {
                    replyToCommentId = (Long) curArg;
                }
            }
        }

        ThrowExceptionUtils.throwIfTrue(bookmarkId < 0L, "Can't find the ID");
        ThrowExceptionUtils.throwIfTrue(userId < 0L, "Can't find the User ID");

        DataAccessType dataAccessType = accessPermissionCheck.dataAccessType();
        String typeName = dataAccessType.getName();

        log.info("Checking permission. Type: {}, ID: {}, User ID: {}",
                typeName, bookmarkId, userId);

        boolean hasNoStrategy = !strategies.containsKey(typeName);
        if (hasNoStrategy) {
            log.warn("No permission check strategy found. Strategy: {}", typeName);
            throw new ServiceException("No permission check strategy");
        }

        PermissionCheckRequest request = new PermissionCheckRequest(
                bookmarkId, userId, tag, commentId, comment, replyToCommentId);
        // check
        strategies.get(typeName).checkPermission(request);
        log.info("Permission check passed. Type: {}, ID: {}, User ID: {}", typeName, bookmarkId, userId);
    }
}