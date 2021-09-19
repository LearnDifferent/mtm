package com.github.learndifferent.mtm.utils;

/**
 * 第一次存入的时候，需要把结尾的 / 符号去掉
 *
 * @author zhou
 * @date 2021/09/05
 */
public class CleanUrlUtil {

    public static String cleanup(String url) {

        // 存入的时候，只存入地址，不存入参数，所以要去掉「？」
        String questionMark = "?";
        if (url.contains(questionMark)) {
            url = url.split("[?]")[0];
        }

        // 结尾有「/」就去掉
        String slash = "/";
        if (url.endsWith(slash)) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
}
