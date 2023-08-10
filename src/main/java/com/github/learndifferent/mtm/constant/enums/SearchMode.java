package com.github.learndifferent.mtm.constant.enums;

import com.github.learndifferent.mtm.constant.consist.SearchConstant;

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
    WEB(SearchConstant.INDEX_WEB),
    /**
     * Search for tags in Elasticsearch
     */
    TAG(SearchConstant.INDEX_TAG),
    /**
     * Search for users in Elasticsearch
     */
    USER(SearchConstant.INDEX_USER),
    /**
     * Search for bookmarks in MySQL
     */
    BOOKMARK_MYSQL(SearchConstant.SEARCH_BOOKMARK_IN_MYSQL),
    /**
     * Search for tags in MySQL
     */
    TAG_MYSQL(SearchConstant.SEARCH_TAG_IN_MYSQL),
    /**
     * Search for users in MySQL
     */
    USER_MYSQL(SearchConstant.SEARCH_USER_IN_MYSQL);

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