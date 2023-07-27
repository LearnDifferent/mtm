package com.github.learndifferent.mtm.annotation.general.idempotency;

import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Logic:
 * Generate a key when logging in and pass the key to the frontend.
 * The frontend stores the key in the header.
 * When a request needs to be idempotent,
 * first verify whether the key in the header is generated by the backend,
 * and then check whether there are duplicate requests in a short period of time (using a lock).
 * If there are no duplicate requests, the verification process is completed.
 * Finally, as long as the request is executed,
 * regardless of whether an exception is thrown or not, release the lock.
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
     * Timeout (default 30s)
     */
    @Value("${idempotency-config.timeout:30}")
    private int timeout;

    /**
     * This is used to retrieve the value from the request header
     */
    @Value("${idempotency-config.key}")
    private String idempotencyKeyHeader;

    private final StringRedisTemplate redisTemplate;

    @Around("@annotation(annotation)")
    public Object check(ProceedingJoinPoint joinPoint, IdempotencyCheck annotation) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // get the key from the header
        String key = request.getHeader(idempotencyKeyHeader);

        // check if the key is valid
        checkIfKeyValid(key);

        // Check if the key conflicts with an existing one in Redis,
        // and returns the Redis key if not.
        // This will throw an IdempotencyException if the key conflicts.
        String redisKey = generateNonConflictingRedisKey(request, key);

        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            if (e instanceof ServiceException) {
                ServiceException se = (ServiceException) e;
                throw new ServiceException(
                        e,
                        se.getResultCode(),
                        se.getMessage(),
                        se.getData());
            } else {
                throw new ServiceException(e);
            }
        } finally {
            // Release the lock: delete the Redis key
            redisTemplate.delete(redisKey);
        }
    }

    /**
     * Check if the key conflicts with an existing one in Redis,
     * and returns the Redis key if not
     *
     * @param request request
     * @param key     the key to check
     * @return the Redis key
     * @throws com.github.learndifferent.mtm.exception.IdempotencyException indicating a key conflict with {@link
     *                                                                      ResultCode#IDEMPOTENCY_KEY_CONFLICT}
     */
    private String generateNonConflictingRedisKey(HttpServletRequest request, String key) {
        // method: get, post....
        String method = request.getMethod();
        // request path
        String path = request.getServletPath();
        // request params
        StringBuilder params = new StringBuilder(":");
        request.getParameterMap()
                .forEach((k, v) ->
                        params.append(k).append("-").append(Arrays.toString(v)));
        params.append(":");

        String redisKey = KeyConstant.IDEMPOTENCY_CHECK_PREFIX
                + method + path + params + key;
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(redisKey, "", timeout, TimeUnit.SECONDS);

        // throw an IdempotencyException if the key conflicts
        ThrowExceptionUtils.throwIfTrue(
                BooleanUtils.isNotTrue(success),
                ResultCode.IDEMPOTENCY_KEY_CONFLICT,
                key);

        return redisKey;
    }

    private void checkIfKeyValid(String key) {
        boolean isKeyBlank = StringUtils.isBlank(key);
        ThrowExceptionUtils.throwIfTrue(isKeyBlank, ResultCode.IDEMPOTENCY_KEY_BLANK, key);

        String redisKey = KeyConstant.IDEMPOTENCY_KEY_PREFIX + key;
        Boolean hasKey = redisTemplate.hasKey(redisKey);
        boolean notValid = BooleanUtils.isNotTrue(hasKey);
        ThrowExceptionUtils.throwIfTrue(
                notValid,
                ResultCode.IDEMPOTENCY_KEY_NOT_VALID,
                key);
    }
}