package com.github.learndifferent.mtm.strategy.search.related;

import com.github.learndifferent.mtm.constant.consist.SearchConstant;
import com.github.learndifferent.mtm.dto.search.UserForSearchDTO;
import com.github.learndifferent.mtm.entity.UserDO;
import com.github.learndifferent.mtm.manager.SearchManager;
import com.github.learndifferent.mtm.mapper.UserMapper;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.JsonUtils;
import java.util.List;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.springframework.stereotype.Component;

/**
 * User data search-related strategy
 *
 * @author zhou
 * @date 2023/8/8
 */
@Component(SearchConstant.SEARCH_RELATED_STRATEGY_BEAN_NAME_PREFIX + SearchConstant.INDEX_USER)
@RequiredArgsConstructor
public class UserDataSearchRelatedStrategy implements DataSearchRelatedStrategy {

    private final SearchManager searchManager;
    private final UserMapper userMapper;

    @Override
    public boolean verifyDataExistenceInElasticsearch() {
        return searchManager.existsIndex(SearchConstant.INDEX_USER);
    }

    @Override
    public boolean checkAndDeleteIndexInElasticsearch() {
        return searchManager.checkAndDeleteIndex(SearchConstant.INDEX_USER);
    }

    /**
     * User Data generation for Elasticsearch based on database
     *
     * @return true if success
     */
    @Override
    public boolean generateDataForElasticsearchBasedOnDatabase() {
        searchManager.throwExceptionIfFailToDeleteIndex(SearchConstant.INDEX_USER);

        List<UserDO> us = userMapper.getUsers(null, null);
        List<UserForSearchDTO> users = DozerUtils.convertList(us, UserForSearchDTO.class);

        BulkRequest bulkRequest = new BulkRequest();
        users.forEach(u -> searchManager.updateBulkRequest(bulkRequest,
                SearchConstant.INDEX_USER, String.valueOf(u.getId()), JsonUtils.toJson(u)));

        return searchManager.sendBulkRequest(bulkRequest);
    }

    @Override
    public boolean checkDatabaseElasticsearchDataDifference() {
        Future<Long> countEsDocsResult = searchManager.countDocsAsync(SearchConstant.INDEX_USER);
        long databaseCount = userMapper.countUsers();

        return searchManager.getEsCountAsyncAndCompareDifference(countEsDocsResult, databaseCount);
    }
}
