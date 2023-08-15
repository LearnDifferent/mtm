package com.github.learndifferent.mtm.chain;

import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jsoup.nodes.Document;

/**
 * {@link AbstractWebScraperProcessor} request
 *
 * @author zhou
 * @date 2023/8/15
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Accessors(chain = true)
public class WebScraperRequest {

    private String requestedUrl;
    private String username;
    private Document document;
    private BasicWebDataDTO data;
}