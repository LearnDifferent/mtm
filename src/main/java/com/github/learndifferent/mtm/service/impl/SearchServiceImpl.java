package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck.ExceptionIfEmpty;
import com.github.learndifferent.mtm.constant.consist.SearchConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.SearchMode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.search.SearchResultsDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.manager.SearchManager;
import com.github.learndifferent.mtm.manager.TrendingManager;
import com.github.learndifferent.mtm.service.SearchService;
import com.github.learndifferent.mtm.strategy.search.main.DataSearchStrategyContext;
import com.github.learndifferent.mtm.strategy.search.related.DataSearchRelatedStrategyContext;
import java.io.IOException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Search and trending searches
 *
 * @author zhou
 * @date 2021/9/21
 */
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final SearchManager searchManager;
    private final TrendingManager trendingManager;
    private final DataSearchRelatedStrategyContext dataSearchRelatedStrategyContext;
    private final DataSearchStrategyContext dataSearchStrategyContext;

    @Override
    public boolean existsData(SearchMode mode) {
        return dataSearchRelatedStrategyContext.verifyDataExistence(mode);
    }

    @Override
    public boolean checkDatabaseElasticsearchDataDifference(SearchMode mode, boolean hasIndex) {

        boolean hasNoIndex = !hasIndex;
        if (hasNoIndex) {
            // if the index does not exist, return true, which means it has changes
            return true;
        }

        return dataSearchRelatedStrategyContext.checkDatabaseElasticsearchDataDifference(mode);
    }

    @Override
    public boolean checkAndDeleteIndex(SearchMode mode) {
        return dataSearchRelatedStrategyContext.checkAndDeleteIndex(mode);
    }

    @Override
    public boolean generateDataForSearch(SearchMode mode) {
        return dataSearchRelatedStrategyContext.generateDataForSearch(mode);
    }

    @Override
    @EmptyStringCheck
    public SearchResultsDTO search(SearchMode mode,
                                   @ExceptionIfEmpty(resultCode = ResultCode.NO_RESULTS_FOUND) String keyword,
                                   PageInfoDTO pageInfo,
                                   Integer rangeFrom,
                                   Integer rangeTo) {
        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();
        try {
            String strategyName = SearchConstant.SEARCH_STRATEGY_BEAN_NAME_PREFIX + mode.mode();
            return this.dataSearchStrategyContext.search(strategyName, keyword.trim(), from, size, rangeFrom, rangeTo);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    @Override
    public Set<String> getTop20Trending() {
        return trendingManager.getTop20Trending();
    }

    @Override
    public boolean deleteTrendingWord(String word) {
        return trendingManager.deleteTrendingWord(word);
    }

    @Override
    public boolean deleteTrending() {
        return trendingManager.deleteTrending();
    }
}
