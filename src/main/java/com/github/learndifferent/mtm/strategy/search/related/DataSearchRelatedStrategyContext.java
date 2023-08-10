package com.github.learndifferent.mtm.strategy.search.related;

import com.github.learndifferent.mtm.constant.consist.SearchConstant;
import com.github.learndifferent.mtm.constant.enums.SearchMode;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Data search-related strategy context
 *
 * @author zhou
 * @date 2023/8/8
 */
@Component
public class DataSearchRelatedStrategyContext {

    private final Map<SearchMode, DataSearchRelatedStrategy> strategies;

    public DataSearchRelatedStrategyContext(Map<String, DataSearchRelatedStrategy> map) {
        // convert Map<String, DataSearchRelatedStrategy> to Map<SearchMode, DataSearchRelatedStrategy>
        // map: key -> bean name; value -> the bean
        strategies = new HashMap<>();
        map.forEach((k, v) -> {
            String strategyBeanNameReplacement = k.replaceFirst(SearchConstant.SEARCH_RELATED_STRATEGY_BEAN_NAME_PREFIX,
                    "");
            String modeName = strategyBeanNameReplacement.toUpperCase();
            SearchMode searchMode = SearchMode.valueOf(modeName);
            strategies.put(searchMode, v);
        });
    }

    private DataSearchRelatedStrategy getStrategy(SearchMode mode) {
        if (strategies.containsKey(mode)) {
            return strategies.get(mode);
        }

        // return default strategy if not found
        return strategies.get(SearchMode.WEB);
    }

    public boolean verifyDataExistence(SearchMode mode) {
        DataSearchRelatedStrategy strategy = getStrategy(mode);
        return strategy.verifyDataExistence();
    }

    public boolean checkAndDeleteIndex(SearchMode mode) {
        DataSearchRelatedStrategy strategy = getStrategy(mode);
        return strategy.checkAndDeleteIndex();
    }

    public boolean generateDataForSearch(SearchMode mode) {
        return getStrategy(mode).generateDataForSearch();
    }
}