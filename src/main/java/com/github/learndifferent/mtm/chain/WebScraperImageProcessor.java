package com.github.learndifferent.mtm.chain;

import com.github.learndifferent.mtm.constant.consist.WebScraperProcessorConstant;
import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Get the image from document
 *
 * @author zhou
 * @date 2023/8/14
 */
@Component
@Order(WebScraperProcessorConstant.IMAGE_ORDER)
public class WebScraperImageProcessor extends AbstractWebScraperProcessor {

    @Override
    public BasicWebDataDTO process(@NotNull WebScraperRequest request) {
        Document document = request.getDocument();
        BasicWebDataDTO data = request.getData();

        Elements images = document.select("img[src]");

        boolean hasImage = images.size() > 0;
        // get the first image or a default image if there is no image available
        String img = hasImage ? images.get(0).attr("abs:src")
                : "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fbpic.588ku.com%2Felement_origin_min_pic%2F00%2F93%2F63%2F2656f2a6a663e1c.jpg&refer=http%3A%2F%2Fbpic.588ku.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1618635152&t=e26535a2d80f40281592178ee20ee656";
        data.setImg(img);

        return this.next.process(request);
    }

}
