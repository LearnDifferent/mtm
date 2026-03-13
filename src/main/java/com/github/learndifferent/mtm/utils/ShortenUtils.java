package com.github.learndifferent.mtm.utils;

/**
 * Utility for shortening text lengths
 * (username: 20 chars, title: 47 chars, description: flattened and shortened to 260 chars)
 *
 * @author zhou
 * @date 2021/09/05
 */
public class ShortenUtils {

    private ShortenUtils() {}

    /**
     * Flattens text by removing line breaks
     *
     * @param contents Original string
     * @return Flattened string
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
