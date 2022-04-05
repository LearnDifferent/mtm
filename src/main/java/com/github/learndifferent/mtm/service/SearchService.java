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
     * @param mode Check user data if {@link SearchMode#USER},
     *             tag data if {@link SearchMode#TAG}
     *             and bookmark data if {@link SearchMode#WEB}
     * @return true if exists
     */
    boolean existsData(SearchMode mode);

    /**
     * Check if data in database is different from data in Elasticsearch.
     *
     * @param mode       Check user data if {@link SearchMode#USER},
     *                   bookmark data if {@link SearchMode#WEB}
     *                   and tag data if {@link SearchMode#TAG}
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
     * @param mode delete user data if {@link SearchMode#USER},
     *             bookmark data if {@link SearchMode#WEB}
     *             and tag data if {@link SearchMode#TAG}
     * @return true if deleted
     */
    boolean checkAndDeleteIndex(SearchMode mode);

    /**
     * Data generation for Elasticsearch based on database
     *
     * @param mode generate user data if {@link SearchMode#USER},
     *             generate bookmark data   if t{@link SearchMode#WEB},
     *             and generate tag data if {@link SearchMode#TAG}.
     *             The default mode is {@link SearchMode#WEB}.
     * @return true if success
     */
    boolean generateDataForSearch(SearchMode mode);

    /**
     * Search
     * <p>
     * add the keyword to trending list if the search mode is {@link SearchMode#WEB}
     * </p>
     *
     * @param mode     Search users if the search mode is {@link SearchMode#USER},
     *                 search bookmarks if the search mode is {@link SearchMode#WEB}
     *                 and search tags if the search mode is {@link SearchMode#TAG}
     * @param keyword  keyword
     * @param pageInfo pagination information
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