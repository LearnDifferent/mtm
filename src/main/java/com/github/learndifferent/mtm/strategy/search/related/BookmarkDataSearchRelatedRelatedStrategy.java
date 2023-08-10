package com.github.learndifferent.mtm.strategy.search.related;

import com.github.learndifferent.mtm.constant.consist.SearchConstant;
import com.github.learndifferent.mtm.manager.SearchManager;
import lombok.RequiredArgsConstructor;
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

    @Override
    public boolean verifyDataExistence() {
        return searchManager.existsIndex(SearchConstant.INDEX_WEB);
    }

    @Override
    public boolean checkAndDeleteIndex() {
        return searchManager.checkAndDeleteIndex(SearchConstant.INDEX_WEB);
    }

    @Override
    public boolean generateDataForSearch() {
        return searchManager.generateBasicWebData();
    }
}
