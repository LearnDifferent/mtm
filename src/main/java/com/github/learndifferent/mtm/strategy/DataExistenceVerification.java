package com.github.learndifferent.mtm.strategy;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.SearchMode;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Data existence verification
 *
 * @author zhou
 * @date 2023/8/8
 */
@Component
public class DataExistenceVerification {

    private final Map<SearchMode, DataExistenceVerificationStrategy> strategies;

    public DataExistenceVerification(Map<String, DataExistenceVerificationStrategy> map) {
        // Map<String, DataExistenceVerificationStrategy> map: key -> bean name; value -> bean
        strategies = new HashMap<>();
        map.forEach((k, v) -> {
            SearchMode searchMode = SearchMode.valueOf(k.toUpperCase());
            strategies.put(searchMode, v);
        });
    }

    public boolean verify(SearchMode mode) {
        boolean notSupport = !strategies.containsKey(mode);
        ThrowExceptionUtils.throwIfTrue(notSupport, ResultCode.NO_RESULTS_FOUND);

        DataExistenceVerificationStrategy strategy = strategies.get(mode);
        return strategy.verifyDataExistence();
    }
}