package com.github.learndifferent.mtm.strategy;

import com.github.learndifferent.mtm.constant.consist.EsConstant;
import com.github.learndifferent.mtm.manager.ElasticsearchManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Bookmark data search strategy
 *
 * @author zhou
 * @date 2023/8/8
 */
@Component(EsConstant.INDEX_WEB)
@RequiredArgsConstructor
public class BookmarkDataSearchStrategy implements DataSearchStrategy {

    private final ElasticsearchManager elasticsearchManager;

    @Override
    public boolean verifyDataExistence() {
        return elasticsearchManager.existsIndex(EsConstant.INDEX_WEB);
    }

    @Override
    public boolean checkAndDeleteIndex() {
        return elasticsearchManager.checkAndDeleteIndex(EsConstant.INDEX_WEB);
    }
}
