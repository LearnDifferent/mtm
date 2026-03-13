package com.github.learndifferent.mtm.utils;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.exception.IdempotencyException;
import com.github.learndifferent.mtm.exception.ServiceException;

/**
 * Utility class for throwing {@link ServiceException} and {@link IdempotencyException}
 *
 * @author zhou
 * @date 2021/10/19
 */
public class ThrowExceptionUtils {

    private ThrowExceptionUtils() {
    }

    /**
     * Throws an exception if the object is null
     *
     * @param object     Object to check
     * @param resultCode {@link ResultCode} for the exception
     */
    public static void throwIfNull(Object object, ResultCode resultCode) {
        if (object == null) {
            throw new ServiceException(resultCode);
        }
    }

    /**
     * Throws an exception if the object is not null
     *
     * @param object     Object to check
     * @param resultCode {@link ResultCode} for the exception
     */
    public static void throwIfNotNull(Object object, ResultCode resultCode) {
        if (object != null) {
            throw new ServiceException(resultCode);
        }
    }

    /**
     * Throws an exception if the expression is true
     *
     * @param expression Boolean expression to evaluate
     * @param resultCode {@link ResultCode} for the exception
     */
    public static void throwIfTrue(boolean expression, ResultCode resultCode) {
        if (expression) {
            throw new ServiceException(resultCode);
        }
    }

    /**
     * Throws an exception if the expression is true
     *
     * @param expression Boolean expression to evaluate
     * @param message    Error message for the exception
     */
    public static void throwIfTrue(boolean expression, String message) {
        if (expression) {
            throw new ServiceException(message);
        }
    }

    /**
     * Throws an {@link IdempotencyException} if the expression is true
     *
     * @param expression    Boolean expression to evaluate
     * @param resultCode    {@link ResultCode} for the exception
     * @param idempotencyKey Idempotency key for the exception
     */
    public static void throwIfTrue(boolean expression, ResultCode resultCode, String idempotencyKey) {
        if (expression) {
            throw new IdempotencyException(resultCode, idempotencyKey);
        }
    }
}
