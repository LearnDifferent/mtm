package com.github.learndifferent.mtm.utils;

import org.springframework.util.StringUtils;

/**
 * {@link String} utility methods
 *
 * @author zhou
 * @date 2021/09/05
 */
public class CustomStringUtils {

    private CustomStringUtils() {
    }

    public static boolean notEmpty(String str) {
        return !StringUtils.isEmpty(str);
    }

    public static boolean notEqualsIgnoreCase(String baseString, String anotherString) {
        if (baseString == null) {
            // If the string is null, return true to indicate that they are not equal
            return true;
        }

        return !baseString.equalsIgnoreCase(anotherString);
    }
}
