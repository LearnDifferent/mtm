package com.github.learndifferent.mtm.config.mvc;

import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import com.github.learndifferent.mtm.annotation.general.page.PageInfoMethodArgumentResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
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
                .addPathPatterns("/search/load", "/home", "/admin");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new ConvertByNamesConverterFactory());
    }

}