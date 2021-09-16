package com.github.learndifferent.mtm.utils;

/**
 * 用于缩短字符长度（用户名缩短为 20，标题缩短为 47，简介压平并缩短为 260）
 *
 * @author zhou
 * @date 2021/09/05
 */
public class ShortenUtils {

    /**
     * 缩减换行等
     *
     * @param contents 原字符串
     * @return 缩短后的字符串
     */
    public static String flatten(String contents) {
        return contents.replaceAll("\r\n|\r|\n", "");
    }

    public static String shorten(String content, int length) {
        if (content.length() <= length) {
            return content;
        }
        return content.substring(0, length - 1) + "...";
    }
}
