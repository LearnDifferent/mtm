package com.github.learndifferent.mtm.strategy.search;

import com.github.learndifferent.mtm.constant.consist.EsConstant;
import com.github.learndifferent.mtm.manager.ElasticsearchManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Tag data search strategy
 *
 * @author zhou
 * @date 2023/8/8
 */
@Component(EsConstant.STRATEGY_BEAN_NAME_PREFIX + EsConstant.INDEX_TAG)
@RequiredArgsConstructor
public class TagDataSearchStrategy implements DataSearchStrategy {

    private final ElasticsearchManager elasticsearchManager;

    @Override
    public boolean verifyDataExistence() {
        return elasticsearchManager.existsIndex(EsConstant.INDEX_TAG);
    }

    @Override
    public boolean checkAndDeleteIndex() {
        return elasticsearchManager.checkAndDeleteIndex(EsConstant.INDEX_TAG);
    }

    @Override
    public boolean generateDataForSearch() {
        return elasticsearchManager.generateTagData();
    }
}