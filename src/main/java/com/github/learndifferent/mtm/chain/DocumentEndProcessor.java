package com.github.learndifferent.mtm.chain;

import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import org.jsoup.nodes.Document;

/**
 * End of the processor chain
 *
 * @author zhou
 * @date 2023/8/14
 */
public class DocumentEndProcessor extends AbstractDocumentProcessor {

    @Override
    public BasicWebDataDTO process(Document document, BasicWebDataDTO data) {
        return checkAndReturn(data);
    }
}
