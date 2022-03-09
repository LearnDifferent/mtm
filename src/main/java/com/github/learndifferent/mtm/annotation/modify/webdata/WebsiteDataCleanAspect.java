package com.github.learndifferent.mtm.annotation.modify.webdata;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.utils.CleanUrlUtil;
import com.github.learndifferent.mtm.utils.ShortenUtils;
import java.net.MalformedURLException;
import java.net.URL;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Clean Up {@link WebWithNoIdentityDTO}'s fields:
 * 1. Check if the URLs are valid
 * 2. Clean up the format of the website URL
 * 3. If the title or description is empty, replace it with URL as content
 * 4. Shorten the title and description if necessary
 *
 * @author zhou
 * @date 2021/09/12
 */
@Component
@Aspect
@Order(3)
public class WebsiteDataCleanAspect {

    @Around("@annotation(annotation)")
    public Object around(ProceedingJoinPoint pjp, WebsiteDataClean annotation) throws Throwable {

        Object[] args = pjp.getArgs();

        for (int i = 0; i < args.length; i++) {
            if (args[i] != null
                    && WebWithNoIdentityDTO.class.isAssignableFrom(args[i].getClass())) {

                WebWithNoIdentityDTO web = (WebWithNoIdentityDTO) args[i];
                String title = web.getTitle();
                String desc = web.getDesc();
                String url = web.getUrl();
                String img = web.getImg();

                // Check if the URLs are valid
                testIfUrl(url);
                testIfUrl(img);

                // Clean up the format of the URL
                url = CleanUrlUtil.cleanup(url);

                // If the title or description is empty, replace it with URL as content
                if (StringUtils.isEmpty(title)) {
                    title = url;
                }
                if (StringUtils.isEmpty(desc)) {
                    desc = url;
                }

                // Shorten the title and description if necessary
                title = ShortenUtils.shorten(title, 47);
                desc = ShortenUtils.shorten(ShortenUtils.flatten(desc), 260);

                args[i] = web.setTitle(title).setDesc(desc).setUrl(url).setImg(img);
                break;
            }
        }

        return pjp.proceed(args);
    }

    private void testIfUrl(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new ServiceException(ResultCode.URL_MALFORMED);
        }
    }
}
