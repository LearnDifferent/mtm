package com.github.learndifferent.mtm.annotation.modify.string;

import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck.DefaultValueIfEmpty;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck.ExceptionIfEmpty;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.exception.ServiceException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Throw an exception or replace it with default value if the string is empty
 *
 * @author zhou
 * @date 2021/09/05
 */
@Aspect
@Component
public class EmptyStringCheckAspect {

    @Pointcut("@annotation(com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck)")
    public void pointcuts() {
    }

    @Around("pointcuts()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] annotations = method.getParameterAnnotations();

        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < annotations.length; i++) {
            for (Annotation annotation : annotations[i]) {

                if (annotation instanceof DefaultValueIfEmpty
                        && String.class.isAssignableFrom(parameterTypes[i])) {

                    // Check if the string is empty and replace it with default string value if it's empty
                    args[i] = replaceIfEmpty((String) args[i], ((DefaultValueIfEmpty) annotation).value());
                    // Priority: @DefaultValueIfEmpty > @ExceptionIfEmpty
                    break;
                }

                if (annotation instanceof ExceptionIfEmpty
                        && String.class.isAssignableFrom(parameterTypes[i])) {

                    throwExceptionIfEmpty((String) args[i], (ExceptionIfEmpty) annotation);
                    break;
                }
            }
        }

        return joinPoint.proceed(args);
    }

    private String replaceIfEmpty(String str, String defString) {

        if (StringUtils.isEmpty(str)) {
            return defString;
        }

        return str;
    }

    private void throwExceptionIfEmpty(String str, ExceptionIfEmpty exceptionInfo) {

        if (StringUtils.isEmpty(str)) {
            ResultCode resultCode = exceptionInfo.resultCode();
            String errorMessage = exceptionInfo.errorMessage();
            throwExceptionWithInfo(resultCode, errorMessage);
        }
    }

    private void throwExceptionWithInfo(ResultCode resultCode, String errorMessage) {

        if (StringUtils.isEmpty(errorMessage)) {
            throw new ServiceException(resultCode);
        }

        throw new ServiceException(resultCode, errorMessage);
    }
}
