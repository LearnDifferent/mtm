package com.github.learndifferent.mtm.strategy.search.main;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.search.SearchResultsDTO;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author zhou
 * @date 2023/8/10
 */
@Component
@RequiredArgsConstructor
public class DataSearchStrategyContext {

    private final Map<String, DataSearchStrategy> strategies;

    public SearchResultsDTO search(String strategyName,
                                   String keyword,
                                   int from,
                                   int size,
                                   Integer rangeFrom,
                                   Integer rangeTo) throws IOException {

        boolean hasNotContains = !strategies.containsKey(strategyName);
        ThrowExceptionUtils.throwIfTrue(hasNotContains, ResultCode.NO_RESULTS_FOUND);

        return this.strategies.get(strategyName).search(keyword, from, size, rangeFrom, rangeTo);
    }
}
