package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.constant.consist.EsConstant;
import com.github.learndifferent.mtm.constant.enums.SearchMode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.search.SearchResultsDTO;
import com.github.learndifferent.mtm.manager.ElasticsearchManager;
import com.github.learndifferent.mtm.manager.TrendingManager;
import com.github.learndifferent.mtm.service.SearchService;
import com.github.learndifferent.mtm.strategy.DataExistenceVerification;
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
    private final DataExistenceVerification dataExistenceVerification;

    @Override
    public boolean existsData(SearchMode mode) {
        return dataExistenceVerification.verify(mode);
    }

    @Override
    public boolean dataInDatabaseDiffFromElasticsearch(SearchMode mode, boolean existIndex) {
        return elasticsearchManager.dataInDatabaseDiffFromElasticsearch(mode, !existIndex);
    }

    @Override
    public boolean checkAndDeleteIndex(SearchMode mode) {
        switch (mode) {
            case USER:
                return elasticsearchManager.checkAndDeleteIndex(EsConstant.INDEX_USER);
            case TAG:
                return elasticsearchManager.checkAndDeleteIndex(EsConstant.INDEX_TAG);
            case WEB:
            default:
                return elasticsearchManager.checkAndDeleteIndex(EsConstant.INDEX_WEB);
        }
    }

    @Override
    public boolean generateDataForSearch(SearchMode mode) {
        switch (mode) {
            case USER:
                return elasticsearchManager.generateUserData();
            case TAG:
                return elasticsearchManager.generateTagData();
            case WEB:
            default:
                return elasticsearchManager.generateBasicWebData();
        }
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
