package com.github.learndifferent.mtm.chain;

import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/**
 * Get the title
 *
 * @author zhou
 * @date 2023/8/14
 */
@Component
public class DocumentTitleProcessor extends AbstractDocumentProcessor {

    @Override
    public BasicWebDataDTO process(Document document, BasicWebDataDTO data) {
        String title = document.title();

        BasicWebDataDTO d = checkAndReturn(data);

        return this.next.process(document, d.setTitle(title));
    }
}
