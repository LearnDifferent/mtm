package com.github.learndifferent.mtm.strategy.search.main;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.SearchMode;
import com.github.learndifferent.mtm.dto.search.SearchResultsDTO;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.io.IOException;
import org.elasticsearch.search.SearchHits;

/**
 * Data search strategy
 *
 * @author zhou
 * @date 2023/8/10
 */
public interface DataSearchStrategy {

    /**
     * Search
     *
     * @param keyword   keyword
     * @param from      from
     * @param size      size
     * @param rangeFrom lower range value for range query if the search mode is {@link SearchMode#TAG}. Null indicates
     *                  unbounded.
     * @param rangeTo   upper range value for range query if the search mode is {@link SearchMode#TAG}. Null indicates
     *                  unbounded.
     * @return {@link SearchResultsDTO} Search results
     * @throws IOException IO Exception
     */
    SearchResultsDTO search(String keyword, int from, int size, Integer rangeFrom, Integer rangeTo) throws IOException;

    /**
     * Get total number of hits
     *
     * @param hits hits
     * @return total count
     */
    default long getTotalCount(SearchHits hits) {
        long totalCount = hits.getTotalHits().value;
        // check total number
        ThrowExceptionUtils.throwIfTrue(totalCount <= 0, ResultCode.NO_RESULTS_FOUND);
        return totalCount;
    }
}