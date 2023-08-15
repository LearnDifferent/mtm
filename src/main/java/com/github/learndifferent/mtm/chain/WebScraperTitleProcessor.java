package com.github.learndifferent.mtm.chain;

import com.github.learndifferent.mtm.constant.consist.WebScraperProcessorConstant;
import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Get the title from document
 *
 * @author zhou
 * @date 2023/8/14
 */
@Component
@Order(WebScraperProcessorConstant.TITLE_ORDER)
public class WebScraperTitleProcessor extends AbstractWebScraperProcessor {

    @Override
    public BasicWebDataDTO process(@NotNull WebScraperRequest request) {
        Document document = request.getDocument();
        BasicWebDataDTO data = request.getData();

        String title = document.title();
        data.setTitle(title);

        return this.next.process(request);
    }
}
