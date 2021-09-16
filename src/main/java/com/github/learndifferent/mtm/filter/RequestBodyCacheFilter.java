package com.github.learndifferent.mtm.filter;

import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * request 过滤器
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
