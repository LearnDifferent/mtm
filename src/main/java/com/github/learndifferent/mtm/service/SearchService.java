package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.constant.enums.SearchMode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.search.SearchResultsDTO;
import java.util.Set;

/**
 * Search and trending searches
 *
 * @author zhou
 * @date 2021/09/21
 */
public interface SearchService {

    /**
     * Check whether the index exists.
     * If not exists, create a new index.
     *
     * @param indexName name of the index
     * @return whether the index exists
     * @throws com.github.learndifferent.mtm.exception.ServiceException Connection exception
     */
    boolean hasIndexOrCreate(String indexName);

    /**
     * Check the existent of data
     *
     * @param mode Check user data if {@link SearchMode#USER} and check website data if {@link SearchMode#WEB}
     * @return true if exists
     */
    boolean existsData(SearchMode mode);

    /**
     * Check if data in database is different from data in Elasticsearch.
     *
     * @param mode       Check user data if {@link SearchMode#USER} and check website data if {@link SearchMode#WEB}
     * @param existIndex Index exists or not
     * @return Returns true if detect a difference.
     * <p>If the index does not exist, returns true. If the {@link SearchMode} is not {@link SearchMode#USER} or
     * {@link SearchMode#WEB}, or it's null, returns false.</p>
     */
    boolean dataInDatabaseDiffFromElasticsearch(SearchMode mode, boolean existIndex);

    /**
     * Check whether the index exists.
     * If not exists, return true.
     * If exists, delete the index and return whether the deletion is success.
     *
     * @param mode Delete user data if {@link SearchMode#USER} and delete website data if {@link SearchMode#WEB}
     * @return true if deleted
     */
    boolean checkAndDeleteIndex(SearchMode mode);

    /**
     * Data generation for Elasticsearch based on database
     *
     * @param mode If the value is {@link SearchMode#USER}, generate user data.
     *             If the value is {@link SearchMode#WEB}, generate website data.
     *             The default mode is {@link SearchMode#WEB}.
     * @return success or failure
     */
    boolean generateDataForSearch(SearchMode mode);

    /**
     * Search and add the keyword to trending list
     *
     * @param mode     Search user data if {@link SearchMode#USER} and search website data if {@link SearchMode#WEB}
     * @param keyword  keyword
     * @param pageInfo pagination info
     * @return {@link SearchResultsDTO} Search results
     * @throws com.github.learndifferent.mtm.exception.ServiceException an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#NO_RESULTS_FOUND}
     *                                                                  will be thrown if there are no results that
     *                                                                  match the keyword
     */
    SearchResultsDTO search(SearchMode mode, String keyword, PageInfoDTO pageInfo);

    /**
     * Get Top 20 Trends
     *
     * @return Top 20 Trends
     */
    Set<String> getTrends();

    /**
     * Delete a Trending Word
     *
     * @param word Trending Word to Delete
     * @return true if success
     * @throws com.github.learndifferent.mtm.exception.ServiceException If the {@code word} is empty, throw an exception
     */
    boolean deleteTrendsByWord(String word);

    /**
     * Delete All Trending Words
     *
     * @return true if success
     */
    boolean deleteAllTrends();
}