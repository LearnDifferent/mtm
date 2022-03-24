package com.github.learndifferent.mtm.service;

/**
 * View Counter Service
 *
 * @author zhou
 * @date 2022/3/24
 */
public interface ViewCounterService {

    /**
     * Increase the number of views of a website data
     *
     * @param webId ID of the website data
     */
    void increaseViews(Integer webId);

    /**
     * Count the number of views of a website data
     *
     * @param webId ID of the website data
     * @return views
     */
    int countViews(Integer webId);
}
