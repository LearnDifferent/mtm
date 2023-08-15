package com.github.learndifferent.mtm.chain;

import com.github.learndifferent.mtm.constant.consist.WebScraperProcessorConstant;
import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Get the description from document
 *
 * @author zhou
 * @date 2023/8/14
 */
@Component
@Order(WebScraperProcessorConstant.DESCRIPTION_ORDER)
public class WebScraperDescriptionProcessor extends AbstractWebScraperProcessor {

    @Override
    public BasicWebDataDTO process(@NotNull WebScraperRequest request) {
        Document document = request.getDocument();
        BasicWebDataDTO data = request.getData();

        String desc = document.body().text();
        data.setDesc(desc);

        return this.next.process(request);
    }
}
