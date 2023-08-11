package com.github.learndifferent.mtm.strategy.search.related;

import com.github.learndifferent.mtm.constant.consist.SearchConstant;
import com.github.learndifferent.mtm.dto.search.TagForSearchDTO;
import com.github.learndifferent.mtm.entity.TagAndCountDO;
import com.github.learndifferent.mtm.manager.SearchManager;
import com.github.learndifferent.mtm.mapper.TagMapper;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.JsonUtils;
import java.util.List;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.bulk.BulkRequest;
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
    public boolean verifyDataExistenceInElasticsearch() {
        return searchManager.existsIndex(SearchConstant.INDEX_TAG);
    }

    @Override
    public boolean checkAndDeleteIndexInElasticsearch() {
        return searchManager.checkAndDeleteIndex(SearchConstant.INDEX_TAG);
    }

    /**
     * Tag Data generation for Elasticsearch based on database.
     * Remember to clear all tag data before generation.
     *
     * @return true if success
     */
    @Override
    public boolean generateDataForElasticsearchBasedOnDatabase() {
        this.searchManager.throwExceptionIfFailToDeleteIndex(SearchConstant.INDEX_TAG);

        List<TagAndCountDO> data = tagMapper.getAllTagsAndCountOfPublicBookmarks();
        List<TagForSearchDTO> tcs = DozerUtils.convertList(data, TagForSearchDTO.class);

        BulkRequest bulkRequest = new BulkRequest();
        tcs.forEach(tc ->
                this.searchManager.updateBulkRequest(bulkRequest, SearchConstant.INDEX_TAG, tc.getTag(),
                        JsonUtils.toJson(tc)));

        return this.searchManager.sendBulkRequest(bulkRequest);
    }

    @Override
    public boolean checkDatabaseElasticsearchDataDifference() {
        Future<Long> countEsDocsResult = this.searchManager.countDocsAsync(SearchConstant.INDEX_TAG);
        long databaseCount = this.tagMapper.countDistinctTags();

        return this.getEsCountAsyncAndCompareDifference(countEsDocsResult, databaseCount);
    }
}