package com.github.learndifferent.mtm.constant.enums;

/**
 * 选择传入的值表示的是 Current Page 还是 from
 *
 * @author zhou
 * @date 2021/09/05
 */
public enum PageInfoMode {

    /**
     * 传入的值表示 Current Page，以 CurrentPage 的形式获取分页信息
     */
    CURRENT_PAGE("currentPage"),
    /**
     * 传入的值表示 from，以 from 的形式获取分页信息
     */
    FROM("from");

    private final String paramName;

    private PageInfoMode(final String paramName) {
        this.paramName = paramName;
    }

    public String paramName() {
        return this.paramName;
    }
}
