package com.github.learndifferent.mtm.chain;

import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import org.jetbrains.annotations.NotNull;

/**
 * Scrape data from the web
 *
 * @author zhou
 * @date 2023/8/14
 */
public abstract class AbstractWebScraperProcessor {

    protected AbstractWebScraperProcessor next;

    public void setNext(AbstractWebScraperProcessor next) {
        this.next = next;
    }

    /**
     * Process
     *
     * @param request {@link WebScraperRequest}
     * @return {@link BasicWebDataDTO}
     */
    public abstract BasicWebDataDTO process(@NotNull WebScraperRequest request);
}