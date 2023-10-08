package com.github.learndifferent.mtm.chain;

import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jsoup.nodes.Document;

/**
 * {@link AbstractWebScraperProcessor} request
 *
 * @author zhou
 * @date 2023/8/15
 */
@Data
@Accessors(chain = true)
public class WebScraperRequest {

    private WebScraperRequest(String requestedUrl,
                              Long userId,
                              Document document,
                              BasicWebDataDTO data) {
        this.requestedUrl = requestedUrl;
        this.userId = userId;
        this.document = document;
        this.data = data;
    }

    public static WebScraperRequest initRequest(String requestedUrl, long userId) {
        return new WebScraperRequest(requestedUrl, userId, null, null);
    }

    private String requestedUrl;
    private Long userId;
    private Document document;
    private BasicWebDataDTO data;
}