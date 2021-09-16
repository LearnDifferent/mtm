package com.github.learndifferent.mtm.annotation.modify.webdata;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.utils.CleanUrlUtil;
import com.github.learndifferent.mtm.utils.ShortenUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 清理 Website 数据中 title 和 desc 的长度，判断 url 格式是否正确，以及清理 url
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

                // 检查 url 和 img 链接的正确性
                testIfUrl(url);
                testIfUrl(img);

                // 清理 URL
                url = CleanUrlUtil.cleanup(url);

                // 如果 title 或 desc 为空，替换为 URL 作为内容填充
                if (StringUtils.isEmpty(title)) {
                    title = url;
                }
                if (StringUtils.isEmpty(desc)) {
                    desc = url;
                }

                // 缩短长度
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
