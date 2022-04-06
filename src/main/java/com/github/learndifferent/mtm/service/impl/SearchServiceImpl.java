package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.constant.consist.EsConstant;
import com.github.learndifferent.mtm.constant.enums.SearchMode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.search.SearchResultsDTO;
import com.github.learndifferent.mtm.manager.ElasticsearchManager;
import com.github.learndifferent.mtm.manager.TrendingManager;
import com.github.learndifferent.mtm.service.SearchService;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Search and trending searches
 *
 * @author zhou
 * @date 2021/9/21
 */
@Service
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchManager elasticsearchManager;
    private final TrendingManager trendingManager;

    @Autowired
    public SearchServiceImpl(ElasticsearchManager elasticsearchManager, TrendingManager trendingManager) {
        this.elasticsearchManager = elasticsearchManager;
        this.trendingManager = trendingManager;
    }

    @Override
    public boolean existsData(SearchMode mode) {
        switch (mode) {
            case USER:
                return elasticsearchManager.existsIndex(EsConstant.INDEX_USER);
            case TAG:
                return elasticsearchManager.existsIndex(EsConstant.INDEX_TAG);
            case WEB:
            default:
                return elasticsearchManager.existsIndex(EsConstant.INDEX_WEB);
        }
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
                return elasticsearchManager.generateUserDataForSearch();
            case TAG:
                return elasticsearchManager.generateTagDataForSearch();
            case WEB:
            default:
                return elasticsearchManager.generateWebsiteDataForSearch();
        }
    }

    @Override
    public SearchResultsDTO search(SearchMode mode, String keyword, PageInfoDTO pageInfo) {
        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();
        switch (mode) {
            case USER:
                return elasticsearchManager.searchUserData(keyword, from, size);
            case TAG:
                return elasticsearchManager.searchTagData(keyword, from, size);
            case WEB:
            default:
                return elasticsearchManager.searchWebsiteData(keyword, from, size);
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


    @Override
    public boolean hasIndexOrCreate(String indexName) {
        return elasticsearchManager.hasIndexOrCreate(indexName);
    }
}
