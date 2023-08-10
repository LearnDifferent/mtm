package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.annotation.modify.webdata.WebsiteDataClean;
import com.github.learndifferent.mtm.constant.consist.SearchConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.SearchMode;
import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import com.github.learndifferent.mtm.dto.search.TagForSearchDTO;
import com.github.learndifferent.mtm.dto.search.UserForSearchDTO;
import com.github.learndifferent.mtm.dto.search.WebForSearchDTO;
import com.github.learndifferent.mtm.entity.TagAndCountDO;
import com.github.learndifferent.mtm.entity.UserDO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.mapper.TagMapper;
import com.github.learndifferent.mtm.mapper.UserMapper;
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
public class ElasticsearchManager {

    private final RestHighLevelClient client;
    private final BookmarkMapper bookmarkMapper;
    private final TrendingManager trendingManager;
    private final UserMapper userMapper;
    private final TagMapper tagMapper;
    private final LanguageDetector languageDetector;

    public ElasticsearchManager(@Qualifier("restHighLevelClient") RestHighLevelClient client,
                                BookmarkMapper bookmarkMapper,
                                TrendingManager trendingManager,
                                UserMapper userMapper,
                                TagMapper tagMapper, LanguageDetector languageDetector) {
        this.client = client;
        this.bookmarkMapper = bookmarkMapper;
        this.trendingManager = trendingManager;
        this.userMapper = userMapper;
        this.tagMapper = tagMapper;
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
            e.printStackTrace();
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    /**
     * Check if data in database is different from data in Elasticsearch.
     *
     * @param mode          Check user data if {@link SearchMode#USER},
     *                      bookmark data if {@link SearchMode#WEB}
     *                      and tag data if {@link SearchMode#TAG}
     * @param notExistIndex true if the index does not exist
     * @return Return true if detect a difference.
     * <p>If the index does not exist, return true. If can't find the {@link SearchMode} ,
     * or it's null, return false.</p>
     */
    public boolean dataInDatabaseDiffFromElasticsearch(SearchMode mode, boolean notExistIndex) {

        if (notExistIndex) {
            // if the index does not exist, return true, which means it has changes
            return true;
        }

        Future<Long> countEsDocsResult;
        long databaseCount;

        switch (mode) {
            case USER:
                countEsDocsResult = countDocsAsync(SearchConstant.INDEX_USER);
                databaseCount = userMapper.countUsers();
                break;
            case TAG:
                countEsDocsResult = countDocsAsync(SearchConstant.INDEX_TAG);
                databaseCount = tagMapper.countDistinctTags();
                break;
            case WEB:
                countEsDocsResult = countDocsAsync(SearchConstant.INDEX_WEB);
                databaseCount = bookmarkMapper.countDistinctPublicUrl();
                break;
            default:
                // return false if can't find the search mode, which means no changes
                return false;
        }

        return getAndCompare(countEsDocsResult, databaseCount);
    }

    private boolean getAndCompare(Future<Long> countEsDocsResult, long databaseCount) {
        Long elasticsearchDocCount = null;
        try {
            elasticsearchDocCount = countEsDocsResult.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        long esCount = Optional.ofNullable(elasticsearchDocCount).orElse(0L);

        // 如果数量不相同，代表有变化；如果数量相同，代表没有变化
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
            log.error("IOException while counting. Return 0.");
            e.printStackTrace();
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
                    + "Dropped this data because it can be added to Elasticsearch later manually.");
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    /**
     * Tag Data generation for Elasticsearch based on database.
     * Remember to clear all tag data before generation.
     *
     * @return true if success
     */
    public boolean generateTagData() {
        throwIfNotClear(SearchConstant.INDEX_TAG);

        List<TagAndCountDO> data = tagMapper.getAllTagsAndCountOfPublicBookmarks();
        List<TagForSearchDTO> tcs = DozerUtils.convertList(data, TagForSearchDTO.class);

        BulkRequest bulkRequest = new BulkRequest();
        tcs.forEach(tc -> updateBulkRequest(bulkRequest, SearchConstant.INDEX_TAG, tc.getTag(), JsonUtils.toJson(tc)));

        return sendBulkRequest(bulkRequest);
    }

    /**
     * User Data generation for Elasticsearch based on database
     *
     * @return true if success
     */
    public boolean generateUserData() {

        throwIfNotClear(SearchConstant.INDEX_USER);

        List<UserDO> us = userMapper.getUsers(null, null);
        List<UserForSearchDTO> users = DozerUtils.convertList(us, UserForSearchDTO.class);

        BulkRequest bulkRequest = new BulkRequest();
        users.forEach(u -> updateBulkRequest(bulkRequest,
                SearchConstant.INDEX_USER, String.valueOf(u.getId()), JsonUtils.toJson(u)));

        return sendBulkRequest(bulkRequest);
    }

    public boolean generateBasicWebData() {

        throwIfNotClear(SearchConstant.INDEX_WEB);

        List<WebForSearchDTO> data = bookmarkMapper.getAllPublicBasicWebDataForSearch();

        BulkRequest bulkRequest = new BulkRequest();
        data.forEach(b -> updateBulkRequest(bulkRequest, SearchConstant.INDEX_WEB, b.getUrl(), JsonUtils.toJson(b)));
        return sendBulkRequest(bulkRequest);
    }

    private void throwIfNotClear(String indexTag) {
        boolean notClear = !checkAndDeleteIndex(indexTag);
        ThrowExceptionUtils.throwIfTrue(notClear, ResultCode.FAILED);
    }

    private void updateBulkRequest(BulkRequest bulkRequest, String index, String id, String json) {
        IndexRequest request = new IndexRequest(index);
        request.id(id);
        request.source(json, XContentType.JSON);
        bulkRequest.add(request);
    }

    private boolean sendBulkRequest(BulkRequest bulkRequest) {
        try {
            BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            return !response.hasFailures();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    @Async("asyncTaskExecutor")
    public void removeUserFromElasticsearchAsync(int id) {

        DeleteRequest request = new DeleteRequest(SearchConstant.INDEX_USER, String.valueOf(id));
        try {
            client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
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
            log.info("IOException while adding data to trending list. "
                    + "Dropped this data because it's not important.");
            e.printStackTrace();
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
        ElasticsearchManager elasticsearchManager = ApplicationContextUtils.getBean(ElasticsearchManager.class);
        elasticsearchManager.analyzeAndAddTrendingAsync(keyword, analyzer);
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