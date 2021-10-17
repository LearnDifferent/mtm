package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.constant.enums.SearchMode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.search.SearchResultsDTO;
import com.github.learndifferent.mtm.manager.ElasticsearchManager;
import com.github.learndifferent.mtm.manager.TrendsManager;
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
    private final TrendsManager trendsManager;

    @Autowired
    public SearchServiceImpl(ElasticsearchManager elasticsearchManager, TrendsManager trendsManager) {
        this.elasticsearchManager = elasticsearchManager;
        this.trendsManager = trendsManager;
    }

    @Override
    public boolean hasIndexOrCreate(String indexName) {
        return elasticsearchManager.hasIndexOrCreate(indexName);
    }

    @Override
    public boolean existsIndex(String indexName) {
        return elasticsearchManager.existsIndex(indexName);
    }

    @Override
    public boolean websiteDataDiffFromDatabase(boolean existIndex) {
        return elasticsearchManager.websiteDataDiffFromDatabase(existIndex);
    }

    @Override
    public boolean checkAndDeleteIndex(String indexName) {
        return elasticsearchManager.checkAndDeleteIndex(indexName);
    }

    @Override
    public boolean generateDataForSearch(SearchMode mode) {
        switch (mode) {
            case USER:
                return elasticsearchManager.generateUserDataForSearch();
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
            case WEB:
            default:
                return elasticsearchManager.searchWebsiteData(keyword, from, size);
        }
    }

    @Override
    public Set<String> getTrends() {
        return trendsManager.getTrends();
    }

    @Override
    public boolean deleteTrendsByWord(String word) {
        return trendsManager.deleteTrendsByWord(word);
    }

    @Override
    public boolean deleteAllTrends() {
        return trendsManager.deleteAllTrends();
    }
}
