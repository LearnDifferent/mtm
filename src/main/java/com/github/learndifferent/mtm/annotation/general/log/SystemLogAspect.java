package com.github.learndifferent.mtm.annotation.general.log;

import com.github.learndifferent.mtm.constant.enums.LogStatus;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.manager.AsyncLogManager;
import com.github.learndifferent.mtm.vo.SysLog;
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
 * 系统日志
 *
 * @author zhou
 * @date 2021/09/05
 */
@Slf4j
@Aspect
@Component
public class SystemLogAspect {

    private final AsyncLogManager asyncLogManager;

    @Autowired
    public SystemLogAspect(AsyncLogManager asyncLogManager) {
        this.asyncLogManager = asyncLogManager;
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
            // 如果没有 Title，就让类名作为 title
            title = pjp.getTarget().getClass().getSimpleName();
        }
        sysLog.title(title);

        sysLog.status(LogStatus.NORMAL.status())
                .msg(LogStatus.NORMAL.name());

        try {
            Object result = pjp.proceed();
            asyncLogManager.saveSysLogAsync(sysLog.build());
            return result;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            sysLog.status(LogStatus.ERROR.status())
                    .msg(throwable.getMessage());
            asyncLogManager.saveSysLogAsync(sysLog.build());
            // 包装为 RuntimeException 并抛出
            throw new RuntimeException(throwable);
        }
    }
}
