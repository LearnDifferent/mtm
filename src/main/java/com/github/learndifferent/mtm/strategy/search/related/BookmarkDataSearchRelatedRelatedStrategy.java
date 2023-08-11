package com.github.learndifferent.mtm.strategy.search.related;

import com.github.learndifferent.mtm.constant.consist.SearchConstant;
import com.github.learndifferent.mtm.dto.search.WebForSearchDTO;
import com.github.learndifferent.mtm.manager.SearchManager;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.utils.JsonUtils;
import java.util.List;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.springframework.stereotype.Component;

/**
 * Bookmark data search-related strategy
 *
 * @author zhou
 * @date 2023/8/8
 */
@Component(SearchConstant.SEARCH_RELATED_STRATEGY_BEAN_NAME_PREFIX + SearchConstant.INDEX_WEB)
@RequiredArgsConstructor
public class BookmarkDataSearchRelatedRelatedStrategy implements DataSearchRelatedStrategy {

    private final SearchManager searchManager;
    private final BookmarkMapper bookmarkMapper;

    @Override
    public boolean verifyDataExistenceInElasticsearch() {
        return searchManager.existsIndex(SearchConstant.INDEX_WEB);
    }

    @Override
    public boolean checkAndDeleteIndex() {
        return searchManager.checkAndDeleteIndex(SearchConstant.INDEX_WEB);
    }

    @Override
    public boolean generateDataForSearch() {
        return generateData();
    }

    private boolean generateData() {

        searchManager.throwExceptionIfFailToDeleteIndex(SearchConstant.INDEX_WEB);

        List<WebForSearchDTO> data = bookmarkMapper.getAllPublicBasicWebDataForSearch();

        BulkRequest bulkRequest = new BulkRequest();
        data.forEach(b ->
                searchManager.updateBulkRequest(bulkRequest, SearchConstant.INDEX_WEB, b.getUrl(),
                        JsonUtils.toJson(b)));
        return searchManager.sendBulkRequest(bulkRequest);
    }

    @Override
    public boolean checkDatabaseElasticsearchDataDifference() {
        Future<Long> countEsDocsResult = searchManager.countDocsAsync(SearchConstant.INDEX_WEB);
        long databaseCount = bookmarkMapper.countDistinctPublicUrl();

        return getEsCountAsyncAndCompareDifference(countEsDocsResult, databaseCount);
    }
}
