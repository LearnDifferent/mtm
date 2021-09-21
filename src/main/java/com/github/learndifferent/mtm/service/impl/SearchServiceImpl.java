package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.SearchResultsDTO;
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
    public boolean hasIndexOrCreate() {
        return elasticsearchManager.hasIndexOrCreate();
    }

    @Override
    public boolean existsIndex() {
        return elasticsearchManager.existsIndex();
    }

    @Override
    public boolean differentFromDatabase(boolean existIndex) {
        return elasticsearchManager.differentFromDatabase(existIndex);
    }

    @Override
    public boolean checkAndDeleteIndex() {
        return elasticsearchManager.checkAndDeleteIndex();
    }

    @Override
    public boolean generateSearchData() {
        return elasticsearchManager.generateSearchData();
    }

    @Override
    public SearchResultsDTO getSearchResult(String keyword, PageInfoDTO pageInfo) {
        return elasticsearchManager.getSearchResult(keyword, pageInfo);
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
