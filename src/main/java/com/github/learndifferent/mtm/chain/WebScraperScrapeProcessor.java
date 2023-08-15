package com.github.learndifferent.mtm.chain;

import com.github.learndifferent.mtm.constant.consist.WebScraperProcessorConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Scrape data from the web
 *
 * @author zhou
 * @date 2023/8/15
 */
@Component
@Order(WebScraperProcessorConstant.SCRAPE_ORDER)
public class WebScraperScrapeProcessor extends AbstractWebScraperProcessor {

    @Override
    public BasicWebDataDTO process(@NotNull WebScraperRequest request) {
        String requestedUrl = request.getRequestedUrl();

        try {
            // get the document
            Document document = Jsoup.parse(new URL(requestedUrl), 3000);
            // set the document
            request.setDocument(document);

            // create the data object
            BasicWebDataDTO data = BasicWebDataDTO.builder().build();
            // set the URL
            data.setUrl(requestedUrl);
            // set the data
            request.setData(data);

            return this.next.process(request);
        } catch (MalformedURLException e) {
            throw new ServiceException(ResultCode.URL_MALFORMED);
        } catch (SocketTimeoutException e) {
            throw new ServiceException(ResultCode.URL_ACCESS_DENIED);
        } catch (IOException e) {
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }
}
