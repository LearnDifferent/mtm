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
     * Check the existent of data in Elasticsearch
     *
     * @param mode Check user data if {@link SearchMode#USER},
     *             tag data if {@link SearchMode#TAG}
     *             and bookmark data if {@link SearchMode#WEB}
     * @return Return true if it exists
     * @throws com.github.learndifferent.mtm.exception.ServiceException in case unable to connect to Elasticsearch
     */
    boolean verifyDataExistenceInElasticsearch(SearchMode mode);

    /**
     * Check if data in database is different from data in Elasticsearch.
     *
     * @param mode     Check user data if {@link SearchMode#USER},
     *                 bookmark data if {@link SearchMode#WEB}
     *                 and tag data if {@link SearchMode#TAG}
     * @param hasIndex true if the index do exist
     * @return Return true if detect a difference.
     * <p>If the index does not exist, return true. If can't find the {@link SearchMode} ,
     * or it's null, return false.</p>
     */
    boolean checkDatabaseElasticsearchDataDifference(SearchMode mode, boolean hasIndex);

    /**
     * Check if the index exists in Elasticsearch.
     * If the index does not exist, return true.
     * If the index exists, delete it and return whether the deletion was successful.
     *
     * @param mode delete user data if {@link SearchMode#USER},
     *             bookmark data if {@link SearchMode#WEB}
     *             and tag data if {@link SearchMode#TAG}
     * @return true if deleted
     */
    boolean checkAndDeleteIndexInElasticsearch(SearchMode mode);

    /**
     * Data generation for Elasticsearch based on database
     *
     * @param mode generate user data if {@link SearchMode#USER},
     *             generate bookmark data   if t{@link SearchMode#WEB},
     *             and generate tag data if {@link SearchMode#TAG}.
     *             The default mode is {@link SearchMode#WEB}.
     * @return true if success
     */
    boolean generateDataForElasticsearchBasedOnDatabase(SearchMode mode);

    /**
     * Search
     * <p>
     * Add the keyword to trending list if the search mode is {@link SearchMode#WEB}
     * or {@link SearchMode#BOOKMARK_MYSQL}.
     * </p>
     *
     * @param mode      Search for users in Elasticsearch if the search mode is {@link SearchMode#USER},
     *                  search for bookmarked websites in Elasticsearch if the search mode is {@link SearchMode#WEB},
     *                  search for tags in Elasticsearch if the search mode is {@link SearchMode#TAG},
     *                  search for bookmarks in MySQL if the search mode is {@link SearchMode#BOOKMARK_MYSQL},
     *                  search for tags in MySQL if the search mode is {@link SearchMode#TAG_MYSQL},
     *                  and search for users in MySQL if the search mode is {@link SearchMode#USER_MYSQL}.
     * @param keyword   keyword
     * @param pageInfo  pagination information
     * @param rangeFrom lower range value for range query if the search mode is {@link SearchMode#TAG}. Null indicates
     *                  unbounded.
     * @param rangeTo   upper range value for range query if the search mode is {@link SearchMode#TAG}. Null indicates
     *                  unbounded.
     * @return {@link SearchResultsDTO} Search results
     * @throws com.github.learndifferent.mtm.exception.ServiceException an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#NO_RESULTS_FOUND}
     *                                                                  will be thrown if there are no results that
     *                                                                  match the keyword
     */
    SearchResultsDTO search(SearchMode mode, String keyword, PageInfoDTO pageInfo, Integer rangeFrom, Integer rangeTo);

    /**
     * Get top 20 trending keywords
     *
     * @return Top 20 trending keywords
     */
    Set<String> getTop20Trending();

    /**
     * Delete a specific trending keyword
     *
     * @param word Trending keyword to delete
     * @return true if success
     * @throws com.github.learndifferent.mtm.exception.ServiceException If the {@code word} is empty, throw an exception
     */
    boolean deleteTrendingWord(String word);

    /**
     * Delete all trending keywords
     *
     * @return true if success
     */
    boolean deleteTrending();
}