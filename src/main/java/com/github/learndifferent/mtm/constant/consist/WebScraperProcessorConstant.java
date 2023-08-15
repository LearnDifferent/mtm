package com.github.learndifferent.mtm.constant.consist;

/**
 * Constants related to {@link com.github.learndifferent.mtm.chain.AbstractWebScraperProcessor}
 *
 * @author zhou
 * @date 2023/8/15
 */
public final class WebScraperProcessorConstant {

    private WebScraperProcessorConstant() {
    }

    public static final int PRE_CHECK_ORDER = 300;

    public static final int SCRAPE_ORDER = PRE_CHECK_ORDER + 1;

    public static final int TITLE_ORDER = SCRAPE_ORDER + 1;

    public static final int DESCRIPTION_ORDER = TITLE_ORDER + 1;

    public static final int IMAGE_ORDER = DESCRIPTION_ORDER + 1;
}