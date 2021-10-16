package com.github.learndifferent.mtm.constant.consist;

/**
 * Elasticsearch 的 index
 *
 * @author zhou
 * @date 2021/09/05
 */
public final class EsConstant {

    private EsConstant() {
    }

    public static final String INDEX_WEB = "web";

    public static final String INDEX_USER = "user";

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
