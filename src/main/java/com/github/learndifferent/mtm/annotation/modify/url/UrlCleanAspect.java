package com.github.learndifferent.mtm.annotation.modify.url;

import com.github.learndifferent.mtm.utils.CleanUrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 用于固定 URL 格式
 *
 * @author zhou
 * @date 2021/09/05
 */
@Slf4j
@Aspect
@Component
@Order(1)
public class UrlCleanAspect {

    @Around("@annotation(annotation)")
    public Object around(ProceedingJoinPoint pjp, UrlClean annotation) throws Throwable {

        MethodSignature signature = (MethodSignature) pjp.getSignature();

        String[] parameterNames = signature.getParameterNames();
        Object[] args = pjp.getArgs();
        String urlParamName = annotation.urlParamName();

        // 没有找到 URL
        boolean cantFindUrl = true;

        for (int i = 0; i < parameterNames.length; i++) {
            if (urlParamName.equalsIgnoreCase(parameterNames[i])
                    && ObjectUtils.isNotEmpty(args[i])
                    && String.class.isAssignableFrom(args[i].getClass())) {

                args[i] = CleanUrlUtil.cleanup((String) args[i]);
                cantFindUrl = false;
                break;
            }
        }

        if (cantFindUrl) {
            log.info("没有找到 URL，请检查参数名称是否正确");
        }

        return pjp.proceed(args);
    }
}
