package com.github.learndifferent.mtm.annotation.modify.url;

import com.github.learndifferent.mtm.annotation.common.Url;
import com.github.learndifferent.mtm.utils.CleanUrlUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Clean up the URL
 *
 * @author zhou
 * @date 2021/09/05
 */
@Slf4j
@Aspect
@Component
@Order(1)
public class UrlCleanAspect {

    @Around("@annotation(urlClean)")
    public Object around(ProceedingJoinPoint pjp, UrlClean urlClean) throws Throwable {

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object[] args = pjp.getArgs();

        boolean cantFindUrl = true;

        outer:
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof Url
                        && ObjectUtils.isNotEmpty(args[i])
                        && String.class.isAssignableFrom(args[i].getClass())) {

                    args[i] = CleanUrlUtil.cleanup((String) args[i]);
                    cantFindUrl = false;
                    break outer;
                }
            }
        }

        if (cantFindUrl) {
            log.warn("Can't find the URL: Please check the parameter name");
        }

        return pjp.proceed(args);
    }
}
