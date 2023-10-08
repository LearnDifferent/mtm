package com.github.learndifferent.mtm.strategy.timeline;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.learndifferent.mtm.vo.BookmarksAndTotalPagesVO;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author zhou
 * @date 2023/8/10
 */
@Component
@RequiredArgsConstructor
public class HomeTimelineStrategyContext {

    private final Map<String, HomeTimelineStrategy> strategies;

    public BookmarksAndTotalPagesVO getHomeTimeline(String strategyName,
                                                    long currentUserId,
                                                    Long requestedUserId,
                                                    int from,
                                                    int size) {
        boolean hasNoStrategy = !strategies.containsKey(strategyName);
        ThrowExceptionUtils.throwIfTrue(hasNoStrategy, ResultCode.NO_RESULTS_FOUND);

        HomeTimelineStrategy strategy = strategies.get(strategyName);
        return strategy.getHomeTimeline(currentUserId, requestedUserId, from, size);
    }
}