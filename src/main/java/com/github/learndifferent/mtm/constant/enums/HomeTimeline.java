package com.github.learndifferent.mtm.constant.enums;

/**
 * About how to displays a stream of bookmarks on the home page.
 *
 * @author zhou
 * @date 2021/09/05
 * @see com.github.learndifferent.mtm.config.mvc.ConvertByNamesConverterFactory
 */
public enum HomeTimeline implements ConvertByNames {

    /**
     * Default: Reverse-chronological order
     */
    LATEST("latest"),
    /**
     * Bookmarked by the user
     */
    USER("user"),
    /**
     * All bookmarks except those that are bookmarked by specific user
     */
    BLOCK("block");

    private final String timeline;

    HomeTimeline(final String timeline) {
        this.timeline = timeline;
    }

    public String timeline() {
        return this.timeline;
    }

    @Override
    public String[] namesForConverter() {
        return new String[]{this.timeline, this.name()};
    }
}