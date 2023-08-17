package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck.ExceptionIfEmpty;
import com.github.learndifferent.mtm.constant.consist.SearchConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.SearchMode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.search.SearchResultsDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.manager.TrendingManager;
import com.github.learndifferent.mtm.service.SearchService;
import com.github.learndifferent.mtm.strategy.search.main.DataSearchStrategyContext;
import com.github.learndifferent.mtm.strategy.search.related.DataSearchRelatedStrategyContext;
import java.io.IOException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Search and trending searches
 *
 * @author zhou
 * @date 2021/9/21
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {

    private final TrendingManager trendingManager;
    private final DataSearchRelatedStrategyContext dataSearchRelatedStrategyContext;
    private final DataSearchStrategyContext dataSearchStrategyContext;

    @Override
    public boolean verifyDataExistenceInElasticsearch(SearchMode mode) {
        return dataSearchRelatedStrategyContext.verifyDataExistenceInElasticsearch(mode);
    }

    @Override
    public boolean checkDatabaseElasticsearchDataDifference(SearchMode mode, boolean hasIndex) {

        boolean hasNoIndex = !hasIndex;

        // if the index does not exist, return true, which means it has changes
        // if it exists, check if data in database is different from data in Elasticsearch
        return hasNoIndex || dataSearchRelatedStrategyContext.checkDatabaseElasticsearchDataDifference(mode);
    }

    @Override
    public boolean checkAndDeleteIndexInElasticsearch(SearchMode mode) {
        return dataSearchRelatedStrategyContext.checkAndDeleteIndexInElasticsearch(mode);
    }

    @Override
    public boolean generateDataForElasticsearchBasedOnDatabase(SearchMode mode) {
        return dataSearchRelatedStrategyContext.generateDataForElasticsearchBasedOnDatabase(mode);
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
            log.error("IO Exception when searching the keyword {} in mode {}", keyword, mode, e);
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
