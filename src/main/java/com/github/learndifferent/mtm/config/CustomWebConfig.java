package com.github.learndifferent.mtm.config;

import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy;
import com.github.learndifferent.mtm.annotation.general.page.PageInfoMethodArgumentResolver;
import com.github.learndifferent.mtm.constant.enums.SearchMode;
import com.github.learndifferent.mtm.constant.enums.ShowPattern;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 自定义 web 配置
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
     * 利用 SaToken，规定需要登录验证的路径。
     * <p>这里为了方便，所以按照黑名单的模式拦截需要被拦截的路径。
     * 实际使用的时候，应该使用拦截所有路径，然后放行个别路径的白名单模式。</p>
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
        registry.addConverter(new ShowPatternConverter());
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
            e.printStackTrace();
            // Returns default mode if exception
            return SearchMode.WEB;
        }
    }
}

/**
 * Convert string to {@link ShowPattern}
 *
 * @author zhou
 * @date 2021/10/21
 */
class ShowPatternConverter implements Converter<String, ShowPattern> {

    @Override
    public ShowPattern convert(String source) {
        try {
            String snakeValue = new SnakeCaseStrategy().translate(source);
            return ShowPattern.valueOf(snakeValue.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            e.printStackTrace();
            // Returns default pattern if exception
            return ShowPattern.DEFAULT;
        }
    }
}
