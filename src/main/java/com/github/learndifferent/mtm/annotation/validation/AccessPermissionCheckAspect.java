package com.github.learndifferent.mtm.annotation.validation;

import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.DataAccessType;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.strategy.permission.PermissionCheckStrategy;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        DataAccessType dataAccessType = accessPermissionCheck.dataAccessType();
        String typeName = dataAccessType.getName();

        boolean hasNoStrategy = !strategies.containsKey(typeName);
        if (hasNoStrategy) {
            log.warn("No permission check strategy found. Strategy: {}", typeName);
            throw new ServiceException("No permission check strategy");
        }
        // check permission
        PermissionCheckStrategy strategy = strategies.get(typeName);
        log.info("Checking permission using strategy {}", strategy);
        strategy.checkPermission(parameterAnnotations, args);
        log.info("Checked permission using strategy {}", strategy);
    }
}