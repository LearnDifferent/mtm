package com.github.learndifferent.mtm.constant.enums;

import com.github.learndifferent.mtm.constant.consist.HomeTimelineConstant;

/**
 * About how to display a stream of bookmarks on the home page.
 *
 * @author zhou
 * @date 2021/09/05
 * @see com.github.learndifferent.mtm.config.mvc.ConvertByNamesConverterFactory
 */
public enum HomeTimeline implements ConvertByNames {

    /**
     * Default: Reverse-chronological order
     */
    LATEST_TIMELINE(HomeTimelineConstant.LATEST_TIMELINE),
    /**
     * User-specific timeline
     */
    USER_SPECIFIC_TIMELINE(HomeTimelineConstant.USER_SPECIFIC_TIMELINE),
    /**
     * Timeline with blacklist
     */
    TIMELINE_WITH_BLACKLIST(HomeTimelineConstant.TIMELINE_WITH_BLACKLIST);

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