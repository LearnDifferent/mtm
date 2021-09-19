package com.github.learndifferent.mtm.config;

import com.github.learndifferent.mtm.filter.RequestBodyCacheFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 过滤器配置
 *
 * @author zhou
 * @date 2021/09/05
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<RequestBodyCacheFilter> registerRequestBodyCacheFilter() {

        // 配置自定义的 Request Body 的过滤器
        FilterRegistrationBean<RequestBodyCacheFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new RequestBodyCacheFilter());
        bean.setOrder(1);
        bean.addUrlPatterns("/log/in");

        return bean;
    }
}
