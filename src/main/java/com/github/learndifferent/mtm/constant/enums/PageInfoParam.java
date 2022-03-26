package com.github.learndifferent.mtm.constant.enums;

/**
 * The parameter related to
 * {@link com.github.learndifferent.mtm.annotation.general.page.PageInfo @PageInfo} annotation
 *
 * @author zhou
 * @date 2021/09/05
 * @see com.github.learndifferent.mtm.annotation.general.page.PageInfo
 * @see com.github.learndifferent.mtm.dto.PageInfoDTO
 * @see com.github.learndifferent.mtm.annotation.general.page.PageInfoMethodArgumentResolver
 */
public enum PageInfoParam {

    /**
     * the parameter is "currentPage"
     */
    CURRENT_PAGE("currentPage"),
    /**
     * the parameter is "from"
     */
    FROM("from");

    private final String paramName;

    PageInfoParam(final String paramName) {
        this.paramName = paramName;
    }

    public String paramName() {
        return this.paramName;
    }
}
