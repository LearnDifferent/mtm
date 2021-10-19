package com.github.learndifferent.mtm.utils;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.exception.ServiceException;

/**
 * Assertion utility class that can throw {@link ServiceException}
 *
 * @author zhou
 * @date 2021/10/17
 */
public class AssertUtils {

    public static void notNull(Object object, ResultCode resultCode) {
        if (object == null) {
            throw new ServiceException(resultCode);
        }
    }

    public static void isNull(Object object, ResultCode resultCode) {
        if (object != null) {
            throw new ServiceException(resultCode);
        }
    }

    public static void isTrue(boolean expression, ResultCode resultCode) {
        if (!expression) {
            throw new ServiceException(resultCode);
        }
    }
}
