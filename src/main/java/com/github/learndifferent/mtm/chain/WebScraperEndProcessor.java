package com.github.learndifferent.mtm.chain;

import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import org.jetbrains.annotations.NotNull;

/**
 * End of the processor chain
 *
 * @author zhou
 * @date 2023/8/14
 */
public class WebScraperEndProcessor extends AbstractWebScraperProcessor {


    @Override
    public BasicWebDataDTO process(@NotNull WebScraperRequest request) {
        return request.getData();
    }
}
