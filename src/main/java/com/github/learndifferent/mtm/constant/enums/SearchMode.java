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
     * Search for bookmarked websites in Elasticsearch
     */
    WEB(EsConstant.INDEX_WEB),
    /**
     * Search for tags in Elasticsearch
     */
    TAG(EsConstant.INDEX_TAG),
    /**
     * Search for users in Elasticsearch
     */
    USER(EsConstant.INDEX_USER),
    /**
     * Search for bookmarks in MySQL
     */
    BOOKMARK_MYSQL("bookmark_mysql"),
    /**
     * Search for tags in MySQL
     */
    TAG_MYSQL("tag_mysql"),
    /**
     * Search for users in MySQL
     */
    USER_MYSQL("user_mysql");

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