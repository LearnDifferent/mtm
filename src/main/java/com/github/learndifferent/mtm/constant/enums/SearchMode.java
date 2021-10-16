package com.github.learndifferent.mtm.constant.enums;

import com.github.learndifferent.mtm.constant.consist.EsConstant;

/**
 * Search Mode
 *
 * @author zhou
 * @date 2021/10/16
 */
public enum SearchMode {

    /**
     * Search for website data
     */
    WEB(EsConstant.INDEX_WEB),
    /**
     * Search for user data
     */
    USER(EsConstant.INDEX_USER);

    private final String mode;

    private SearchMode(final String mode) {
        this.mode = mode;
    }

    public String mode() {
        return mode;
    }
}