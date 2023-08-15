package com.github.learndifferent.mtm.chain;

import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Scrape data from the web
 *
 * @author zhou
 * @date 2023/8/14
 */
@Component
public class WebScraperProcessorFacade {

    /**
     * First document processor
     */
    private final AbstractWebScraperProcessor chainHead;

    /**
     * Set the steps
     *
     * @param steps The order of the list will be arranged based on the injection order
     *              specified by the {@link org.springframework.core.annotation.Order} annotation.
     */
    public WebScraperProcessorFacade(List<AbstractWebScraperProcessor> steps) {
        if (CollectionUtils.isEmpty(steps)) {
            chainHead = new WebScraperEndProcessor();
            return;
        }

        // set next
        for (int i = 0; i < steps.size(); i++) {
            AbstractWebScraperProcessor cur = steps.get(i);

            int last = steps.size() - 1;
            boolean notEnd = i < last;
            AbstractWebScraperProcessor next =
                    notEnd ? steps.get(i + 1) : new WebScraperEndProcessor();

            cur.setNext(next);
        }

        // set head
        chainHead = steps.get(0);
    }

    public BasicWebDataDTO process(@NotNull WebScraperRequest request) {
        return chainHead.process(request);
    }
}