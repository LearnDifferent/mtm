package com.github.learndifferent.mtm.config;

import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.github.learndifferent.mtm.annotation.general.page.PageInfoMethodArgumentResolver;
import com.github.learndifferent.mtm.constant.enums.HomeTimeline;
import com.github.learndifferent.mtm.constant.enums.SearchMode;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC configuration
 *
 * @author zhou
 * @date 2021/09/05
 */
@Configuration
public class CustomWebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PageInfoMethodArgumentResolver());
    }

    /**
     * Intercepts all the requests that are sent to the these paths
     * for checking whether the user is logged in by SaToken (SaRouteInterceptor).
     *
     * @param registry Interceptor Registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaRouteInterceptor())
                .addPathPatterns("/mypage", "/find/load", "/home/load", "/admin");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new SearchModeConverter());
        registry.addConverter(new HomeTimelineConverter());
    }

}

/**
 * Convert string to {@link SearchMode}
 *
 * @author zhou
 * @date 2021/10/16
 */
class SearchModeConverter implements Converter<String, SearchMode> {

    @Override
    public SearchMode convert(String source) {
        try {
            return SearchMode.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {

            // Returns default mode if exception
            return SearchMode.WEB;
        }
    }
}

/**
 * Convert string to {@link HomeTimeline}
 *
 * @author zhou
 * @date 2021/10/21
 */
@Slf4j
class HomeTimelineConverter implements Converter<String, HomeTimeline> {

    @Override
    public HomeTimeline convert(String source) {
        try {
            String snakeValue = new SnakeCaseStrategy().translate(source);
            return HomeTimeline.valueOf(snakeValue.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn("Return 'latest' as default");
            return HomeTimeline.LATEST;
        }
    }
}
