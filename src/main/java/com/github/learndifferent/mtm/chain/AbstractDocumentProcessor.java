package com.github.learndifferent.mtm.chain;

import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import java.util.Objects;
import org.jsoup.nodes.Document;

/**
 * Collect data from document
 *
 * @author zhou
 * @date 2023/8/14
 */
public abstract class AbstractDocumentProcessor {

    protected AbstractDocumentProcessor next;

    public void setNext(AbstractDocumentProcessor next) {
        this.next = next;
    }

    /**
     * Process
     *
     * @param document {@link Document}
     * @param data     {@link BasicWebDataDTO}
     * @return Get the data from document and update {@link BasicWebDataDTO}
     */
    public abstract BasicWebDataDTO process(Document document, BasicWebDataDTO data);

    /**
     * If the data is null, return a new data
     *
     * @param data {@link BasicWebDataDTO}
     * @return {@link BasicWebDataDTO}
     */
    public BasicWebDataDTO checkAndReturn(BasicWebDataDTO data) {
        return Objects.isNull(data) ? BasicWebDataDTO.builder().build() : data;
    }
}