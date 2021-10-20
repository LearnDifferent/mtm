package com.github.learndifferent.mtm.utils;


/**
 * An utility class that can compare two strings
 *
 * @author zhou
 * @date 2021/09/05
 */
public class CompareStringUtil {

    private CompareStringUtil() {
    }

    public static boolean notEqualsIgnoreCase(String baseString, String anotherString) {
        if (baseString == null) {
            // 如果传入的 String 为 null，直接返回 true 表示不相等
            return true;
        }
        return !baseString.equalsIgnoreCase(anotherString);
    }
}
