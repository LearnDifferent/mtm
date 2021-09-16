package com.github.learndifferent.mtm.interceptor;

import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 利用 SaToken，规定需要登录验证的路径。
 * <p>这里为了方便，所以按照黑名单的模式拦截需要被拦截的路径。
 * 实际使用的时候，应该使用拦截所有路径，然后放行个别路径的白名单模式。</p>
 *
 * @author zhou
 * @date 2021/09/05
 */
@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaRouteInterceptor())
                .addPathPatterns("/mypage", "/find", "/home/load", "/admin/info");
    }
}
