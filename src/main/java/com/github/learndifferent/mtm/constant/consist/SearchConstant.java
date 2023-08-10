package com.github.learndifferent.mtm.constant.consist;

/**
 * Constants related to search functionality
 *
 * @author zhou
 * @date 2021/09/05
 */
public final class SearchConstant {

    private SearchConstant() {
    }

    public static final String SEARCH_STRATEGY_BEAN_NAME_PREFIX = "search-";

    public static final String SEARCH_RELATED_STRATEGY_BEAN_NAME_PREFIX = "search-related-";

    public static final String SEARCH_BOOKMARK_IN_MYSQL = "bookmark_mysql";

    public static final String SEARCH_TAG_IN_MYSQL = "tag_mysql";

    public static final String SEARCH_USER_IN_MYSQL = "user_mysql";

    public static final String INDEX_WEB = "web";

    public static final String INDEX_USER = "user";

    public static final String INDEX_TAG = "tag";

    public static final String TAG_NAME = "tag";

    public static final String TAG_NUMBER = "number";

    public static final String USER_ID = "userId";

    public static final String USER_NAME = "userName";

    public static final String ROLE = "role";

    public static final String CREATION_TIME = "createTime";

    public static final String DESC = "desc";

    public static final String TITLE = "title";

    public static final String URL = "url";

    public static final String IMG = "img";

    public static final String PRE_TAGS = "<span style='color:red'>";

    public static final String POST_TAGS = "</span>";

    public static final String TRENDING = "trending";

    public static final String ANALYZER_JAPANESE = "kuromoji";

    public static final String ANALYZER_CHINESE = "ik_smart";
}
