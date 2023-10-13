package com.github.learndifferent.mtm.annotation.validation;

import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.CheckType;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.Id;
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

        long id = -1L;
        long userId = -1L;

        String tag = "";

        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                Object curArg = args[i];
                if (id < 0L
                        && annotation instanceof Id
                        && Objects.nonNull(curArg)
                        && Long.class.isAssignableFrom(curArg.getClass())) {
                    id = (long) curArg;
                }
                if (userId < 0L
                        && annotation instanceof UserId
                        && Objects.nonNull(curArg)
                        && Long.class.isAssignableFrom(curArg.getClass())) {
                    userId = (long) curArg;
                }
                if (StringUtils.isBlank(tag)
                        && annotation instanceof Tag
                        && Objects.nonNull(curArg)
                        && String.class.isAssignableFrom(curArg.getClass())) {
                    tag = (String) curArg;
                }
            }
        }

        ThrowExceptionUtils.throwIfTrue(id < 0L, "Can't find the ID");
        ThrowExceptionUtils.throwIfTrue(userId < 0L, "Can't find the User ID");

        CheckType type = accessPermissionCheck.type();
        String typeName = type.getName();

        log.info("Checking permission. Type: {}, ID: {}, User ID: {}", typeName, id, userId);

        boolean hasNoStrategy = !strategies.containsKey(typeName);
        if (hasNoStrategy) {
            log.warn("No modification permission check strategy found. Strategy: {}", typeName);
            throw new ServiceException("No modification permission check strategy");
        }

        PermissionCheckRequest request = new PermissionCheckRequest(id, userId, tag);
        // check
        strategies.get(typeName).check(request);
        log.info("Permission check passed. Type: {}, ID: {}, User ID: {}", typeName, id, userId);
    }
}