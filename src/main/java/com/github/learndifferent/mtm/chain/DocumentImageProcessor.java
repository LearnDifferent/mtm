package com.github.learndifferent.mtm.chain;

import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

/**
 * Get the image
 *
 * @author zhou
 * @date 2023/8/14
 */
@Component
public class DocumentImageProcessor extends AbstractDocumentProcessor {

    @Override
    public BasicWebDataDTO process(Document document, BasicWebDataDTO data) {
        Elements images = document.select("img[src]");

        boolean hasImage = images.size() > 0;
        // get the first image or a default image if there is no image available
        String img = hasImage ? images.get(0).attr("abs:src")
                : "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fbpic.588ku.com%2Felement_origin_min_pic%2F00%2F93%2F63%2F2656f2a6a663e1c.jpg&refer=http%3A%2F%2Fbpic.588ku.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1618635152&t=e26535a2d80f40281592178ee20ee656";

        BasicWebDataDTO d = checkAndReturn(data);

        return this.next.process(document, d.setImg(img));
    }
}
