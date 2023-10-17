package com.github.learndifferent.mtm.utils;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.exception.IdempotencyException;
import com.github.learndifferent.mtm.exception.ServiceException;

/**
 * A utility class that can throw {@link ServiceException} and {@link IdempotencyException}
 *
 * @author zhou
 * @date 2021/10/19
 */
public class ThrowExceptionUtils {

    private ThrowExceptionUtils() {
    }

    /**
     * Throw an exception if the object is null
     *
     * @param object     object
     * @param resultCode {@link ResultCode}
     */
    public static void throwIfNull(Object object, ResultCode resultCode) {
        if (object == null) {
            throw new ServiceException(resultCode);
        }
    }

    /**
     * Throw an exception if the object is not null
     *
     * @param object     object
     * @param resultCode {@link ResultCode}
     */
    public static void throwIfNotNull(Object object, ResultCode resultCode) {
        if (object != null) {
            throw new ServiceException(resultCode);
        }
    }

    /**
     * Throw an exception if the expression is true
     *
     * @param expression expression
     * @param resultCode {@link ResultCode}
     */
    public static void throwIfTrue(boolean expression, ResultCode resultCode) {
        if (expression) {
            throw new ServiceException(resultCode);
        }
    }

    public static void throwIfTrue(boolean expression, String message) {
        if (expression) {
            throw new ServiceException(message);
        }
    }

    /**
     * Throw a {@link IdempotencyException} if the expression is true
     *
     * @param expression expression
     * @param resultCode {@link ResultCode}
     */
    public static void throwIfTrue(boolean expression, ResultCode resultCode, String idempotencyKey) {
        if (expression) {
            throw new IdempotencyException(resultCode, idempotencyKey);
        }
    }
}
