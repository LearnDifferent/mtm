package com.github.learndifferent.mtm.utils;

/**
 * Utility for cleaning URLs before storage
 * Removes trailing slashes and query parameters
 *
 * @author zhou
 * @date 2021/09/05
 */
public class CleanUrlUtil {

    private CleanUrlUtil() {
    }

    public static String cleanup(String url) {

        // Trim whitespace
        url = url.trim();

        // Remove query parameters
        String questionMark = "?";
        if (url.contains(questionMark)) {
            url = url.split("[?]")[0];
        }

        // Remove trailing slash
        String slash = "/";
        if (url.endsWith(slash)) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
}
