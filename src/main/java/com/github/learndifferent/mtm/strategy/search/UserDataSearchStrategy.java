package com.github.learndifferent.mtm.strategy.search;

import com.github.learndifferent.mtm.constant.consist.EsConstant;
import com.github.learndifferent.mtm.manager.ElasticsearchManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User data search strategy
 *
 * @author zhou
 * @date 2023/8/8
 */
@Component(EsConstant.STRATEGY_BEAN_NAME_PREFIX + EsConstant.INDEX_USER)
@RequiredArgsConstructor
public class UserDataSearchStrategy implements DataSearchStrategy {

    private ElasticsearchManager elasticsearchManager;

    @Autowired
    public void setElasticsearchManager(ElasticsearchManager elasticsearchManager) {
        this.elasticsearchManager = elasticsearchManager;
    }

    @Override
    public boolean verifyDataExistence() {
        return elasticsearchManager.existsIndex(EsConstant.INDEX_USER);
    }

    @Override
    public boolean checkAndDeleteIndex() {
        return elasticsearchManager.checkAndDeleteIndex(EsConstant.INDEX_USER);
    }

    @Override
    public boolean generateDataForSearch() {
        return elasticsearchManager.generateUserData();
    }
}
