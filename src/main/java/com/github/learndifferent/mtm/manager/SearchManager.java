package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.annotation.modify.webdata.WebsiteDataClean;
import com.github.learndifferent.mtm.constant.consist.SearchConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import com.github.learndifferent.mtm.dto.search.UserForSearchDTO;
import com.github.learndifferent.mtm.dto.search.WebForSearchDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.utils.ApplicationContextUtils;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.JsonUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
import org.elasticsearch.client.indices.AnalyzeResponse.AnalyzeToken;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Elasticsearch Manager
 *
 * @author zhou
 * @date 2021/09/05
 */
@Slf4j
@Component
public class SearchManager {

    private final RestHighLevelClient client;
    private final TrendingManager trendingManager;
    private final LanguageDetector languageDetector;

    public SearchManager(@Qualifier("restHighLevelClient") RestHighLevelClient client,
                         TrendingManager trendingManager,
                         LanguageDetector languageDetector) {
        this.client = client;
        this.trendingManager = trendingManager;
        this.languageDetector = languageDetector;
    }

    /**
     * Check the existent of data
     *
     * @param indexName name of the index
     * @return true if exists
     * @throws ServiceException in case unable to connect to Elasticsearch
     */
    public boolean existsIndex(String indexName) {
        try {
            GetIndexRequest request = new GetIndexRequest(indexName);
            return client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("Unable to connect to Elasticsearch", e);
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    /**
     * Get the count of Elasticsearch documents asynchronously and compare the difference
     *
     * @param countEsDocsResult {@link Future<Long>} Elasticsearch document count
     * @param databaseCount     database count
     * @return true if detect a difference
     */
    public boolean getEsCountAsyncAndCompareDifference(Future<Long> countEsDocsResult, long databaseCount) {
        Long elasticsearchDocCount = null;
        try {
            elasticsearchDocCount = countEsDocsResult.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Unable to get Elasticsearch document count", e);
        }

        long esCount = Optional.ofNullable(elasticsearchDocCount).orElse(0L);

        // If the count of Elasticsearch documents is different from the database, return true
        return databaseCount != esCount;
    }

    @Async("asyncTaskExecutor")
    public Future<Long> countDocsAsync(String index) {
        CountRequest request = new CountRequest(index);
        try {
            CountResponse countResponse = client.count(request, RequestOptions.DEFAULT);
            long count = countResponse.getCount();
            return AsyncResult.forValue(count);
        } catch (IOException | ElasticsearchStatusException e) {
            return analyzeAndReturnZero(e);
        }
    }

    private ListenableFuture<Long> analyzeAndReturnZero(Exception e) {
        if (e instanceof ElasticsearchStatusException) {
            log.warn("ElasticsearchStatusException while counting, "
                    + "which means the Index has been deleted. Return 0.");
        } else {
            log.error("IOException while counting. Return 0.", e);
        }
        return AsyncResult.forValue(0L);
    }

    @Async("asyncTaskExecutor")
    @WebsiteDataClean
    public Future<Boolean> saveToElasticsearchAsync(BasicWebDataDTO data) {

        IndexRequest request = getIndexRequest(data);
        boolean success = false;

        try {
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            int status = response.status().getStatus();
            if (RestStatus.OK.getStatus() <= status
                    && RestStatus.ACCEPTED.getStatus() >= status) {
                // OK(200) 到 ACCEPTED(202) 之间就算成功
                // 成功包括了：创建成功和之前已存在
                success = true;
            }
        } catch (IOException e) {
            // 如果无法存放，就放弃存放
            log.error("IOException while saving document to Elasticsearch. "
                    + "Dropped this data because it can be added to Elasticsearch later manually.", e);
        }
        return AsyncResult.forValue(success);
    }

    private IndexRequest getIndexRequest(BasicWebDataDTO bookmark) {

        WebForSearchDTO data = DozerUtils.convert(bookmark, WebForSearchDTO.class);
        String json = JsonUtils.toJson(data);
        IndexRequest request = new IndexRequest(SearchConstant.INDEX_WEB);
        request.id(data.getUrl());
        request.timeout(new TimeValue(8, TimeUnit.SECONDS));
        request.source(json, XContentType.JSON);
        return request;
    }

    @Async("asyncTaskExecutor")
    public void saveToElasticsearchAsync(UserForSearchDTO user) {

        IndexRequest request = getIndexRequest(user);
        try {
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("IOException while saving document to Elasticsearch. ", e);
        }
    }

    private IndexRequest getIndexRequest(UserForSearchDTO user) {
        IndexRequest request = new IndexRequest(SearchConstant.INDEX_USER);
        Integer id = user.getId();
        String json = JsonUtils.toJson(user);
        request.id(String.valueOf(id)).source(json, XContentType.JSON);
        return request;
    }

    /**
     * Check if the index exists. If the index does not exist, return true.
     * If the index exists, delete it and return whether the deletion was successful.
     *
     * @return true if deleted
     */
    public boolean checkAndDeleteIndex(String indexName) {

        return !existsIndex(indexName) || deleteIndex(indexName);
    }

    private boolean deleteIndex(String indexName) {
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        try {
            AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException e) {
            log.error("IOException while deleting index. ", e);
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    public void throwExceptionIfFailToDeleteIndex(String indexTag) {
        boolean cantDelete = !checkAndDeleteIndex(indexTag);
        ThrowExceptionUtils.throwIfTrue(cantDelete, ResultCode.FAILED);
    }

    public void updateBulkRequest(BulkRequest bulkRequest, String index, String id, String json) {
        IndexRequest request = new IndexRequest(index);
        request.id(id);
        request.source(json, XContentType.JSON);
        bulkRequest.add(request);
    }

    public boolean sendBulkRequest(BulkRequest bulkRequest) {
        try {
            BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            return !response.hasFailures();
        } catch (IOException e) {
            log.error("IOException while sending bulk request. ", e);
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    @Async("asyncTaskExecutor")
    public void removeUserFromElasticsearchAsync(int id) {

        DeleteRequest request = new DeleteRequest(SearchConstant.INDEX_USER, String.valueOf(id));
        try {
            client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("IOException while deleting user data from Elasticsearch. ", e);
        }
    }

    public SearchHits searchAndGetHits(SearchRequest searchRequest) throws IOException {
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        return response.getHits();
    }

    /**
     * 异步分解搜索的关键词，并加入到热搜列表中
     *
     * @param keyword  没有进行分词处理和语言识别的搜索词
     * @param analyzer 分词器
     */
    @Async("asyncTaskExecutor")
    public void analyzeAndAddTrendingAsync(String keyword, String analyzer) {

        if (StringUtils.isEmpty(keyword)) {
            return;
        }

        AnalyzeRequest request = AnalyzeRequest.withIndexAnalyzer(SearchConstant.INDEX_WEB, analyzer, keyword);
        try {
            AnalyzeResponse analyze = client.indices().analyze(request, RequestOptions.DEFAULT);
            List<AnalyzeResponse.AnalyzeToken> tokens = analyze.getTokens();
            tokens.forEach(this::addTrending);
        } catch (IOException e) {
            log.error("IOException while adding data to trending list. "
                    + "Dropped this data because it's not important.", e);
        }
    }

    private void addTrending(AnalyzeToken token) {
        String val = token.getTerm();
        if (val.length() > 1) {
            // 统计字节数大于 1 的关键词，出现一次就加 1 个 score
            trendingManager.addToTrendingList(val);
        }
    }

    public void addToTrendingList(String keyword) {
        // 检测 keyword 的语言并选择合适的分词器
        String analyzer = detectLanguageAndGetAnalyzer(keyword);
        // 将搜索词分词后放入热搜统计
        SearchManager searchManager = ApplicationContextUtils.getBean(SearchManager.class);
        searchManager.analyzeAndAddTrendingAsync(keyword, analyzer);
    }

    /**
     * 识别是哪国的语言，然后返回需要的 ES 分词器（目前支持英语、中文和日语）
     *
     * @param keyword 被检测的关键词
     * @return 需要的分词器
     */
    private String detectLanguageAndGetAnalyzer(String keyword) {

        Language lan = this.languageDetector.detectLanguageOf(keyword);

        // 默认使用英文分词器
        String analyzer = "english";

        if (Language.JAPANESE.equals(lan)) {
            // 如果是日语，使用日语的分词器
            analyzer = SearchConstant.ANALYZER_JAPANESE;
        }

        if (Language.CHINESE.equals(lan)) {
            // 如果是中文，使用中文的分词器
            analyzer = SearchConstant.ANALYZER_CHINESE;
        }

        return analyzer;
    }
}