package com.github.learndifferent.mtm.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * elasticsearch 配置
 *
 * @author zhou
 * @date 2021/09/05
 */
@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.host}")
    private String host;

    @Value("${elasticsearch.port}")
    private Integer port;

    @Value("${elasticsearch.isHttps}")
    private Boolean isHttps;

    @Bean(name = "restHighLevelClient", destroyMethod = "close")
    RestHighLevelClient client() {
        return getClient();
    }

    private RestHighLevelClient getClient() {
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost(host, port, isHttps ? "https" : "http"))
                        .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder)
                        .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder)
        );
    }
}
