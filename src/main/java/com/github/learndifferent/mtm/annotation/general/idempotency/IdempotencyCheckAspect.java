package com.github.learndifferent.mtm.annotation.general.idempotency;

import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.service.SystemLogService;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Idempotence ensures that the same request
 * leads to the same system state,
 * and no action is unintentionally executed more than once
 *
 * @author zhou
 * @date 2023/7/10
 */
@Aspect
@Order(0)
@Component
@RequiredArgsConstructor
public class IdempotencyCheckAspect {

    /**
     * Timeout (default 3s)
     */
    @Value("${idempotency-config.timeout:3}")
    private int timeout;

    /**
     * This is used to retrieve the value from the request header
     */
    @Value("${idempotency-config.key}")
    private String idempotencyKeyHeader;

    private final StringRedisTemplate redisTemplate;

    private final SystemLogService logService;

    @Before("@annotation(annotation)")
    public void check(JoinPoint jp, IdempotencyCheck annotation) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // get the key from the header
        String key = request.getHeader(idempotencyKeyHeader);

        // check if the key is blank
        boolean isKeyBlank = StringUtils.isBlank(key);
        ThrowExceptionUtils.throwIfTrue(isKeyBlank, ResultCode.IDEMPOTENCY_KEY_BLANK, key);

        // check if the key already exists in Redis
        String redisKey = KeyConstant.IDEMPOTENCY_KEY_PREFIX + request.getContextPath() + key;
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(redisKey, "", timeout, TimeUnit.SECONDS);
        // throw an exception if the key conflicts
        ThrowExceptionUtils.throwIfTrue(
                BooleanUtils.isNotTrue(success),
                ResultCode.IDEMPOTENCY_KEY_CONFLICT,
                key);
    }
}