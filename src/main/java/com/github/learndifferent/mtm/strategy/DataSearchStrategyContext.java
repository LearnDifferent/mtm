package com.github.learndifferent.mtm.strategy;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.SearchMode;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
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
            SearchMode searchMode = SearchMode.valueOf(k.toUpperCase());
            strategies.put(searchMode, v);
        });
    }

    private DataSearchStrategy verifyAndGetStrategy(SearchMode mode) {
        boolean notSupport = !strategies.containsKey(mode);
        ThrowExceptionUtils.throwIfTrue(notSupport, ResultCode.NO_RESULTS_FOUND);
        return strategies.get(mode);
    }

    public boolean verifyDataExistence(SearchMode mode) {
        DataSearchStrategy strategy = verifyAndGetStrategy(mode);
        return strategy.verifyDataExistence();
    }

    public boolean checkAndDeleteIndex(SearchMode mode) {
        DataSearchStrategy strategy = verifyAndGetStrategy(mode);
        return strategy.checkAndDeleteIndex();
    }
}