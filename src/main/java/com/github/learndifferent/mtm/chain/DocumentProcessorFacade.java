package com.github.learndifferent.mtm.chain;

import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import java.util.List;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Collect data from document
 *
 * @author zhou
 * @date 2023/8/14
 */
@Component
public class DocumentProcessorFacade {

    /**
     * First document processor
     */
    private final AbstractDocumentProcessor chainHead;

    public DocumentProcessorFacade(List<AbstractDocumentProcessor> steps) {
        if (CollectionUtils.isEmpty(steps)) {
            chainHead = new DocumentEndProcessor();
            return;
        }

        // set next
        for (int i = 0; i < steps.size(); i++) {
            AbstractDocumentProcessor cur = steps.get(i);

            int last = steps.size() - 1;
            boolean notEnd = i < last;
            AbstractDocumentProcessor next = notEnd ? steps.get(i + 1)
                    : new DocumentEndProcessor();

            cur.setNext(next);
        }

        // set head
        chainHead = steps.get(0);
    }

    public BasicWebDataDTO process(Document document) {
        return chainHead.process(document, null);
    }
}