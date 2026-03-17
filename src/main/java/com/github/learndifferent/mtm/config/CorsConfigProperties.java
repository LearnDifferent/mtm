package com.github.learndifferent.mtm.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * CORS configuration properties.
 */
@Data
@ConfigurationProperties(prefix = "cors")
public class CorsConfigProperties {

    private List<String> allowedOrigins = new ArrayList<>();

    private boolean allowCredentials = true;
}
