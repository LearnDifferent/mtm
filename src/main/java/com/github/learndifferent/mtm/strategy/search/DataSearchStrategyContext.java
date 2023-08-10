package com.github.learndifferent.mtm.strategy.search;

import com.github.learndifferent.mtm.constant.consist.EsConstant;
import com.github.learndifferent.mtm.constant.enums.SearchMode;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Data search strategy context
 *
 * @author zhou
 * @date 2023/8/8
 */
@Component
public class DataSearchStrategyContext {

    private final Map<SearchMode, DataSearchStrategy> strategies;

    public DataSearchStrategyContext(Map<String, DataSearchStrategy> map) {
        // Map<String, DataSearchStrategy> map: key -> bean name; value -> bean
        strategies = new HashMap<>();
        map.forEach((k, v) -> {
            String strategyBeanNameReplacement = k.replaceFirst(EsConstant.STRATEGY_BEAN_NAME_PREFIX, "");
            String modeName = strategyBeanNameReplacement.toUpperCase();
            SearchMode searchMode = SearchMode.valueOf(modeName);
            strategies.put(searchMode, v);
        });
    }

    private DataSearchStrategy getStrategy(SearchMode mode) {
        if (strategies.containsKey(mode)) {
            return strategies.get(mode);
        }

        // return default strategy if not found
        return strategies.get(SearchMode.WEB);
    }

    public boolean verifyDataExistence(SearchMode mode) {
        DataSearchStrategy strategy = getStrategy(mode);
        return strategy.verifyDataExistence();
    }

    public boolean checkAndDeleteIndex(SearchMode mode) {
        DataSearchStrategy strategy = getStrategy(mode);
        return strategy.checkAndDeleteIndex();
    }

    public boolean generateDataForSearch(SearchMode mode) {
        return getStrategy(mode).generateDataForSearch();
    }
}