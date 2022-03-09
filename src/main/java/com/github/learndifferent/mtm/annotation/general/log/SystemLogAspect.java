package com.github.learndifferent.mtm.annotation.general.log;

import com.github.learndifferent.mtm.constant.enums.LogStatus;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.entity.SysLog;
import com.github.learndifferent.mtm.service.SystemLogService;
import java.lang.reflect.Method;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * System Log
 *
 * @author zhou
 * @date 2021/09/05
 */
@Slf4j
@Aspect
@Component
public class SystemLogAspect {

    private final SystemLogService logService;

    @Autowired
    public SystemLogAspect(SystemLogService logService) {
        this.logService = logService;
    }

    @Around("@annotation(annotation)")
    public Object around(ProceedingJoinPoint pjp, SystemLog annotation) {

        SysLog.SysLogBuilder sysLog = SysLog.builder();
        OptsType optsType = annotation.optsType();
        sysLog.optType(optsType.value()).optTime(new Date());

        MethodSignature signature = (MethodSignature) pjp.getSignature();

        Method method = signature.getMethod();
        String methodName = method.getName();
        sysLog.method(methodName + "()");

        String title = annotation.title();
        if (StringUtils.isEmpty(title)) {
            // If no title available, use class name as tile
            title = pjp.getTarget().getClass().getSimpleName();
        }
        sysLog.title(title);

        sysLog.status(LogStatus.NORMAL.status())
                .msg(LogStatus.NORMAL.name());

        try {
            Object result = pjp.proceed();
            logService.saveSystemLogAsync(sysLog.build());
            return result;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            sysLog.status(LogStatus.ERROR.status())
                    .msg(throwable.getMessage());
            logService.saveSystemLogAsync(sysLog.build());

            throw new RuntimeException(throwable);
        }
    }
}
