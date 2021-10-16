package com.github.learndifferent.mtm.constant.enums;

/**
 * 展示网页的模式
 *
 * @author zhou
 * @date 2021/09/05
 */
public enum ShowPattern {

    /**
     * 按照从新到旧展示
     */
    DEFAULT("recent"),
    /**
     * 展示已经收藏的
     */
    MARKED("marked"),
    /**
     * 展示某用户的页面
     */
    USER_PAGE("userPage"),
    /**
     * 展示除了该用户的页面
     */
    WITHOUT_USER_PAGE("withoutUserPage");

    private final String pattern;

    ShowPattern(final String pattern) {
        this.pattern = pattern;
    }

    public String pattern() {
        return this.pattern;
    }
}
