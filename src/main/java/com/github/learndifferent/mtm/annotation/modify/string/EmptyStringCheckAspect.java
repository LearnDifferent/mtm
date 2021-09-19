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
 * 空字符串检查。
 * <p>如果 String 类型的参数为空或 null，进行下一步处理</p>
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
            // 遍历 i 位置的所有注解
            for (Annotation annotation : annotations[i]) {

                if (annotation instanceof DefaultValueIfEmpty
                        && String.class.isAssignableFrom(parameterTypes[i])) {

                    // 检查是否为空字符串或 null，并返回处理过的值
                    args[i] = replaceIfEmpty((String) args[i],
                            (DefaultValueIfEmpty) annotation);
                    // 如果使用了 @DefaultValueIfEmpty 注解，就不能使用 @ExceptionIfEmpty
                    break;
                }

                if (annotation instanceof ExceptionIfEmpty
                        && String.class.isAssignableFrom(parameterTypes[i])) {

                    throwExceptionIfEmpty((String) args[i],
                            (ExceptionIfEmpty) annotation);
                    break;
                }
            }
        }

        return joinPoint.proceed(args);
    }

    private Object replaceIfEmpty(String str, DefaultValueIfEmpty defaultValue) {

        if (StringUtils.isEmpty(str)) {
            // 为空的时候，返回传入的默认值
            return defaultValue.value();
        }

        // 不为空字符串或 null 的时候，不需要进行处理，直接返回即可
        return str;
    }

    private void throwExceptionIfEmpty(String str, ExceptionIfEmpty exceptionInfo) {

        // 为空字符串或 null 的时候，需要抛出异常
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
