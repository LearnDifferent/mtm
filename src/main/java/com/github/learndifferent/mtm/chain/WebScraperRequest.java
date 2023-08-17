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
                              String username,
                              Document document,
                              BasicWebDataDTO data) {
        this.requestedUrl = requestedUrl;
        this.username = username;
        this.document = document;
        this.data = data;
    }

    public static WebScraperRequest initRequest(String requestedUrl, String username) {
        return new WebScraperRequest(requestedUrl, username, null, null);
    }

    private String requestedUrl;
    private String username;
    private Document document;
    private BasicWebDataDTO data;
}