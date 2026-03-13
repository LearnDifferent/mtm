package com.github.learndifferent.mtm.config.mvc;

import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import com.github.learndifferent.mtm.annotation.general.page.PageInfoMethodArgumentResolver;
import com.github.learndifferent.mtm.utils.EnvCheckUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Configuration
public class CustomWebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PageInfoMethodArgumentResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (EnvCheckUtil.containTestEnv()) {
            log.info("Contain test environment, terminate token validation");
            return;
        }

        registry.addInterceptor(new SaRouteInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/verification/**", "/login", "/user",
                        "/", "/css/**", "/js/**", "/img/**", "/favicon.ico", "/index.html");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new ConvertByNamesConverterFactory());
    }

}
