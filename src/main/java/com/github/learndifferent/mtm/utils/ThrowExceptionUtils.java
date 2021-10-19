package com.github.learndifferent.mtm.utils;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.exception.ServiceException;

/**
 * An utility class to throw {@link ServiceException}
 *
 * @author zhou
 * @date 2021/10/19
 */
public class ThrowExceptionUtils {

    /**
     * Throw an exception if the object is null
     *
     * @param object     object
     * @param resultCode {@link ResultCode}
     */
    public static void throwIfNull(Object object, ResultCode resultCode) {
        AssertUtils.notNull(object, resultCode);
    }

    /**
     * Throw an exception if the object is not null
     *
     * @param object     object
     * @param resultCode {@link ResultCode}
     */
    public static void throwIfNotNull(Object object, ResultCode resultCode) {
        AssertUtils.isNull(object, resultCode);
    }

    /**
     * Throw an exception if the expression is true
     *
     * @param expression expression
     * @param resultCode {@link ResultCode}
     */
    public static void throwIfTrue(boolean expression, ResultCode resultCode) {
        AssertUtils.isTrue(!expression, resultCode);
    }

    /**
     * Throw an exception if the expression is false
     *
     * @param expression expression
     * @param resultCode {@link ResultCode}
     */
    public static void throwIfFalse(boolean expression, ResultCode resultCode) {
        AssertUtils.isTrue(expression, resultCode);
    }
}
