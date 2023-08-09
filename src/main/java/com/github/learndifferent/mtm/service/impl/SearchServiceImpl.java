package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.constant.enums.SearchMode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.search.SearchResultsDTO;
import com.github.learndifferent.mtm.manager.ElasticsearchManager;
import com.github.learndifferent.mtm.manager.TrendingManager;
import com.github.learndifferent.mtm.service.SearchService;
import com.github.learndifferent.mtm.strategy.DataSearchStrategyContext;
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

    private final ElasticsearchManager elasticsearchManager;
    private final TrendingManager trendingManager;
    private final DataSearchStrategyContext dataSearchStrategyContext;

    @Override
    public boolean existsData(SearchMode mode) {
        return dataSearchStrategyContext.verifyDataExistence(mode);
    }

    @Override
    public boolean dataInDatabaseDiffFromElasticsearch(SearchMode mode, boolean existIndex) {
        return elasticsearchManager.dataInDatabaseDiffFromElasticsearch(mode, !existIndex);
    }

    @Override
    public boolean checkAndDeleteIndex(SearchMode mode) {
        return dataSearchStrategyContext.checkAndDeleteIndex(mode);
    }

    @Override
    public boolean generateDataForSearch(SearchMode mode) {
        return dataSearchStrategyContext.generateDataForSearch(mode);
    }

    @Override
    public SearchResultsDTO search(SearchMode mode,
                                   String keyword,
                                   PageInfoDTO pageInfo,
                                   Integer rangeFrom,
                                   Integer rangeTo) {
        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();
        return elasticsearchManager.search(keyword, from, size, mode, rangeFrom, rangeTo);
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
