package com.github.learndifferent.mtm.strategy.search.related;

import com.github.learndifferent.mtm.constant.consist.SearchConstant;
import com.github.learndifferent.mtm.manager.SearchManager;
import com.github.learndifferent.mtm.mapper.TagMapper;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Tag data search-related strategy
 *
 * @author zhou
 * @date 2023/8/8
 */
@Component(SearchConstant.SEARCH_RELATED_STRATEGY_BEAN_NAME_PREFIX + SearchConstant.INDEX_TAG)
@RequiredArgsConstructor
public class TagDataSearchRelatedStrategy implements DataSearchRelatedStrategy {

    private final SearchManager searchManager;
    private final TagMapper tagMapper;

    @Override
    public boolean verifyDataExistence() {
        return searchManager.existsIndex(SearchConstant.INDEX_TAG);
    }

    @Override
    public boolean checkAndDeleteIndex() {
        return searchManager.checkAndDeleteIndex(SearchConstant.INDEX_TAG);
    }

    @Override
    public boolean generateDataForSearch() {
        return searchManager.generateTagData();
    }

    @Override
    public boolean checkDatabaseElasticsearchDataDifference() {
        Future<Long> countEsDocsResult = searchManager.countDocsAsync(SearchConstant.INDEX_TAG);
        long databaseCount = tagMapper.countDistinctTags();

        return getEsCountAsyncAndCompareDifference(countEsDocsResult, databaseCount);
    }
}