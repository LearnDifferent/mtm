package com.github.learndifferent.mtm.utils;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.exception.ServiceException;
import org.springframework.util.Assert;

/**
 * Assertion utility class that can throw {@link ServiceException}
 *
 * @author zhou
 * @date 2021/10/17
 */
public class AssertUtils {

    public static void notNull(Object object, ResultCode resultCode) {
        try {
            Assert.notNull(object, "");
        } catch (IllegalArgumentException e) {
            throw new ServiceException(resultCode);
        }
    }

    public static void isNull(Object object, ResultCode resultCode) {
        try {
            Assert.isNull(object, "");
        } catch (IllegalArgumentException e) {
            throw new ServiceException(resultCode);
        }
    }

    public static void isTrue(boolean expression, ResultCode resultCode) {
        try {
            Assert.isTrue(expression, "");
        } catch (IllegalArgumentException e) {
            throw new ServiceException(resultCode);
        }
    }
}
