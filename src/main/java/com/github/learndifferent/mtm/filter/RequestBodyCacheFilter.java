package com.github.learndifferent.mtm.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * Request Body 的过滤器。
 * <p>配置完成后会加入到 FilterConfig 中，让 Request Body 可以被重复使用</p>
 *
 * @author zhou
 * @date 2021/09/05
 */
public class RequestBodyCacheFilter extends GenericFilterBean {

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
