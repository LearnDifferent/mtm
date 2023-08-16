package com.github.learndifferent.mtm.annotation.general.log;

import com.github.learndifferent.mtm.constant.enums.LogStatus;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.entity.SysLog;
import com.github.learndifferent.mtm.exception.IdempotencyException;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.service.SystemLogService;
import java.lang.reflect.Method;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * System Log
 *
 * @author zhou
 * @date 2021/09/05
 */
@Aspect
@Component
@RequiredArgsConstructor
public class SystemLogAspect {

    private final SystemLogService logService;

    @Around("@annotation(annotation)")
    public Object around(ProceedingJoinPoint pjp, SystemLog annotation) throws Throwable {

        SysLog.SysLogBuilder sysLog = SysLog.builder();
        OptsType optsType = annotation.optsType();
        sysLog.optType(optsType.value()).optTime(Instant.now());

        MethodSignature signature = (MethodSignature) pjp.getSignature();

        Method method = signature.getMethod();
        String methodName = method.getName();
        sysLog.method(methodName + "()");

        String title = annotation.title();
        if (StringUtils.isBlank(title)) {
            // If no title available, use class name as tile
            title = pjp.getTarget().getClass().getSimpleName();
        }
        // set title
        sysLog.title(title);

        try {
            Object result = pjp.proceed();
            // add log
            sysLog.status(LogStatus.NORMAL.status()).msg(LogStatus.NORMAL.name());
            return result;
        } catch (Throwable e) {
            String detailMessage = e.getMessage();
            StringBuilder sb = new StringBuilder();

            if (e instanceof ServiceException) {
                ServiceException se = (ServiceException) e;
                ResultCode resultCode = se.getResultCode();
                sb.append("Service Exception - Result Code: ")
                        .append(resultCode.value())
                        .append(" - Result Msg: ")
                        .append(resultCode.msg())
                        .append(" - Throwable Detail Msg: ");
            }

            if (e instanceof IdempotencyException) {
                IdempotencyException ie = (IdempotencyException) e;
                String idempotencyKey = ie.getIdempotencyKey();
                ResultCode resultCode = ie.getResultCode();
                sb.append("Idempotency Exception - Result Code: ")
                        .append(resultCode.value())
                        .append(" - Result Msg: ")
                        .append(resultCode.msg())
                        .append(" - Key：")
                        .append(idempotencyKey)
                        .append(" - Throwable Detail Msg: ");
            }

            String msg = sb.append(detailMessage).toString();
            // add log
            sysLog.status(LogStatus.ERROR.status()).msg(msg);

            // throw the origin exception
            throw e;

        } finally {
            logService.saveSystemLogAsync(sysLog.build());
        }
    }
}
