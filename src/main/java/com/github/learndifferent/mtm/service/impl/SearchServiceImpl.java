package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.annotation.validation.user.role.admin.AdminValidation;
import com.github.learndifferent.mtm.constant.consist.EsConstant;
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
    public boolean existsData(SearchMode mode) {
        switch (mode) {
            case USER:
                return elasticsearchManager.existsIndex(EsConstant.INDEX_USER);
            case WEB:
            default:
                return elasticsearchManager.existsIndex(EsConstant.INDEX_WEB);
        }
    }

    @Override
    public boolean dataInDatabaseDiffFromElasticsearch(SearchMode mode, boolean existIndex) {
        switch (mode) {
            case USER:
                return elasticsearchManager.userDataDiffFromDatabase(existIndex);
            case WEB:
            default:
                return elasticsearchManager.websiteDataDiffFromDatabase(existIndex);
        }

    }

    @Override
    @AdminValidation
    public boolean checkAndDeleteIndex(SearchMode mode) {
        switch (mode) {
            case USER:
                return elasticsearchManager.checkAndDeleteIndex(EsConstant.INDEX_USER);
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
