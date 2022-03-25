package com.github.learndifferent.mtm.service;

import java.util.List;

/**
 * View Counter Service
 *
 * @author zhou
 * @date 2022/3/24
 */
public interface ViewCounterService {

    /**
     * Increase the number of views of a website data.
     * This will increment the number stored at the key of views in Redis
     * and will add the key to a set that stores all the keys of views as well.
     *
     * @param webId ID of the website data
     */
    void increaseViewsAndAddToSet(Integer webId);

    /**
     * Count the number of views of a website data
     *
     * @param webId ID of the website data
     * @return views
     */
    int countViews(Integer webId);

    /**
     * Save the numbers of views to the database
     * and return a list of the keys that failed to save
     *
     * @return the list of the keys that failed to save
     * @throws com.github.learndifferent.mtm.exception.ServiceException an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#UPDATE_FAILED}
     *                                                                  will be thrown if no data available
     */
    List<String> saveViewsToDbAndReturnFailKeys();


    /**
     * A scheduled task to run {@link #saveViewsToDbAndReturnFailKeys()} every 12 hours
     */
    void saveViewsToDatabaseScheduledTask();
}
