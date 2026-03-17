package com.github.learndifferent.mtm.config;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 *
 * @author zhou
 * @date 2021/09/05
 */
@Configuration
@EnableConfigurationProperties(CorsConfigProperties.class)
public class CorsConfig {

    private final CorsConfigProperties corsConfigProperties;

    public CorsConfig(CorsConfigProperties corsConfigProperties) {
        this.corsConfigProperties = corsConfigProperties;
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        List<String> allowedOrigins = corsConfigProperties.getAllowedOrigins()
                .stream()
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .collect(Collectors.toList());

        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowedMethods(java.util.Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE",
                "OPTIONS"));
        corsConfiguration.setAllowedOrigins(allowedOrigins);
        corsConfiguration.setAllowCredentials(corsConfigProperties.isAllowCredentials());
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}
