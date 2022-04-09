package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.dto.PageInfoDTO;
import java.util.List;

/**
 * View Counter Service
 *
 * @author zhou
 * @date 2022/3/24
 */
public interface ViewCounterService {

    /**
     * Increase the number of views of a bookmark.
     * <p>
     * This will increment the number of views of the bookmark that stores in Redis
     * and will add the key that stores this view data to a set in Redis.
     * </p>
     *
     * @param webId ID of the bookmarked website data
     */
    void increaseViewsAndAddToSet(Integer webId);

    /**
     * Count the number of views of a bookmark stored in Redis
     *
     * @param webId ID of the bookmarked website data
     * @return views
     */
    int countViews(Integer webId);

    /**
     * Save the numbers of views from Redis to the database,
     * or add the view data from database to Redis if the Redis has no view data.
     * <p>
     * This will also delete the value of the key "bookmarks:visited" stored in cache
     * if the method saved the numbers of views from Redis to the database
     * </p>
     *
     * @return Return a list of keys that failed to save
     * @see WebsiteService#getVisitedBookmarks(PageInfoDTO)
     */
    List<String> updateViewsAndReturnFailKeys();

    /**
     * A scheduled task to run {@link #updateViewsAndReturnFailKeys()} every 12 hours
     */
    void updateViewsScheduledTask();
}