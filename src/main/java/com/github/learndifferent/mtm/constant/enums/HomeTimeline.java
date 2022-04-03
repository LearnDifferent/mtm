package com.github.learndifferent.mtm.constant.enums;

/**
 * About how to displays a stream of bookmarks on the home page.
 * <p>
 * {@code HomeTimelineConverter} in the {@link com.github.learndifferent.mtm.config.CustomWebConfig CustomWebConfig}
 * will set {@link #LATEST} as default value if it can't convert the source object to {@link HomeTimeline}.
 * </p>
 *
 * @author zhou
 * @date 2021/09/05
 */
public enum HomeTimeline {

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
}