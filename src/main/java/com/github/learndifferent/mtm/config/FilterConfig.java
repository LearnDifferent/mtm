package com.github.learndifferent.mtm.config;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.ContentCachingRequestWrapper;

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
/**
 * Request Body 的过滤器。
 * <p>配置完成后会加入到 FilterConfig 中，让 Request Body 可以被重复使用</p>
 *
 * @author zhou
 * @date 2021/09/05
 */
class RequestBodyCacheFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        ContentCachingRequestWrapper wrapperRequest = new ContentCachingRequestWrapper(request);

        filterChain.doFilter(wrapperRequest, servletResponse);
    }
}