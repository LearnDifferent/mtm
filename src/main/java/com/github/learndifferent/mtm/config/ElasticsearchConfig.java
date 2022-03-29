package com.github.learndifferent.mtm.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Elasticsearch Configuration
 *
 * @author zhou
 * @date 2021/09/05
 */
@Configuration
public class ElasticsearchConfig {

    private final ElasticsearchConfigProperties elasticsearchConfigProperties;

    @Autowired
    public ElasticsearchConfig(ElasticsearchConfigProperties elasticsearchConfigProperties) {
        this.elasticsearchConfigProperties = elasticsearchConfigProperties;
    }

    @Bean(name = "restHighLevelClient", destroyMethod = "close")
    RestHighLevelClient client() {
        return getClient();
    }

    private RestHighLevelClient getClient() {
        String host = elasticsearchConfigProperties.getHost();
        int port = elasticsearchConfigProperties.getPort();
        String schemeName = elasticsearchConfigProperties.getSchemeName();

        return new RestHighLevelClient(
                RestClient.builder(new HttpHost(host, port, schemeName))
                        .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder)
                        .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder)
        );
    }
}
