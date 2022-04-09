package com.github.learndifferent.mtm.constant.enums;

import com.github.learndifferent.mtm.constant.consist.EsConstant;

/**
 * Search Mode
 *
 * @author zhou
 * @date 2021/10/16
 * @see com.github.learndifferent.mtm.config.mvc.ConvertByNamesConverterFactory
 */
public enum SearchMode implements ConvertByNames {

    /**
     * Search for bookmarked websites
     */
    WEB(EsConstant.INDEX_WEB),
    /**
     * Search for tags
     */
    TAG(EsConstant.INDEX_TAG),
    /**
     * Search for users
     */
    USER(EsConstant.INDEX_USER);

    private final String mode;

    SearchMode(final String mode) {
        this.mode = mode;
    }

    public String mode() {
        return mode;
    }

    @Override
    public String[] namesForConverter() {
        return new String[]{this.mode, this.name()};
    }
}