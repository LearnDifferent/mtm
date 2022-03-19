package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck.ExceptionIfEmpty;
import com.github.learndifferent.mtm.annotation.modify.webdata.WebsiteDataClean;
import com.github.learndifferent.mtm.constant.consist.EsConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.SearchMode;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.dto.search.SearchResultsDTO;
import com.github.learndifferent.mtm.dto.search.UserForSearchDTO;
import com.github.learndifferent.mtm.dto.search.UserForSearchWithWebCountDTO;
import com.github.learndifferent.mtm.dto.search.WebForSearchDTO;
import com.github.learndifferent.mtm.entity.UserDO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.mapper.UserMapper;
import com.github.learndifferent.mtm.mapper.WebsiteMapper;
import com.github.learndifferent.mtm.utils.ApplicationContextUtils;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.JsonUtils;
import com.github.learndifferent.mtm.utils.PageUtil;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
    private final WebsiteMapper websiteMapper;
    private final TrendsManager trendsManager;
    private final UserMapper userMapper;
    private final LanguageDetector languageDetector;

    @Autowired
    public ElasticsearchManager(@Qualifier("restHighLevelClient") RestHighLevelClient client,
                                WebsiteMapper websiteMapper,
                                TrendsManager trendsManager,
                                UserMapper userMapper,
                                LanguageDetector languageDetector) {
        this.client = client;
        this.websiteMapper = websiteMapper;
        this.trendsManager = trendsManager;
        this.userMapper = userMapper;
        this.languageDetector = languageDetector;
    }

    /**
     * 是否存在该 Index，如果没有就创建 Index
     *
     * @param indexName name of the index
     * @return 是否存在 Index，没有该 Index 的话返回是否创建成功
     */
    public boolean hasIndexOrCreate(String indexName) {

        return existsIndex(indexName) || createIndex(indexName);
    }

    private boolean createIndex(String indexName) {
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        try {
            CreateIndexResponse response = client.indices()
                    .create(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    /**
     * 是否存在该 Index
     *
     * @param indexName name of the index
     * @return true 表示存在，false 表示不存在
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
     * Elasticsearch 和数据库中的数据是否不同步。
     *
     * @param mode          search mode：如果不是指定的模式，直接返回 false，表示数据相同
     * @param notExistIndex 是否不存在该 index：如果不存在该 index，直接返回 true，表示 Elasticsearch 中的数据和数据库中的数据不同
     * @return true 表示 Elasticsearch 中的数据和数据库中的数据条数不同
     */
    public boolean dataInDatabaseDiffFromElasticsearch(SearchMode mode, boolean notExistIndex) {

        if (notExistIndex) {
            // 如果不存在该 index，直接返回 true，表示不同
            return true;
        }

        Future<Long> countEsDocsResult;
        long databaseCount;

        switch (mode) {
            case USER:
                countEsDocsResult = countDocsAsync(EsConstant.INDEX_USER);
                databaseCount = userMapper.countUsers();
                break;
            case WEB:
                countEsDocsResult = countDocsAsync(EsConstant.INDEX_WEB);
                // 数据库中的 distinct url 的数量
                databaseCount = websiteMapper.countDistinctPublicUrl();
                break;
            default:
                // 没有模式的情况下，直接返回 false，表示数据相同
                return false;
        }

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

    /**
     * 统计该 Index 的文档条数
     *
     * @param index index
     * @return 数据条数
     */
    @Async("asyncTaskExecutor")
    public Future<Long> countDocsAsync(String index) {
        CountRequest request = new CountRequest(index);
        try {
            CountResponse countResponse = client.count(request, RequestOptions.DEFAULT);
            long count = countResponse.getCount();
            return AsyncResult.forValue(count);
        } catch (IOException | ElasticsearchStatusException e) {
            if (e instanceof ElasticsearchStatusException) {
                log.warn("ElasticsearchStatusException while counting, "
                        + "which means the Index has been deleted. Returned 0.");
            } else {
                log.error("IOException while counting. Returned 0.");
                e.printStackTrace();
            }
            return AsyncResult.forValue(0L);
        }
    }

    /**
     * 异步存放文档
     *
     * @param websiteData 需要存放的网页原始数据
     * @return {@code Future<Boolean>} true 表示成功；false 表示存放失败
     */
    @Async("asyncTaskExecutor")
    @WebsiteDataClean
    public Future<Boolean> saveDocAsync(WebWithNoIdentityDTO websiteData) {

        WebForSearchDTO web = DozerUtils.convert(websiteData, WebForSearchDTO.class);

        String json = JsonUtils.toJson(web);
        IndexRequest request = new IndexRequest(EsConstant.INDEX_WEB);
        request.id(web.getUrl());
        request.timeout(new TimeValue(8, TimeUnit.SECONDS));
        request.source(json, XContentType.JSON);

        boolean success = false;

        try {
            // 发送请求并返回结果
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

    /**
     * 删除步骤：先检查该 index 是否存在，
     * <p>如果不存在，返回 true 表示已经删除；</p>
     * <p>如果存在该 index，就执行删除</p>
     *
     * @param indexName name of the index
     * @return 是否删除成功
     */
    public boolean checkAndDeleteIndex(String indexName) {

        return !existsIndex(indexName) || deleteIndex(indexName);
    }

    private boolean deleteIndex(String indexName) {
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        try {
            AcknowledgedResponse response = client.indices()
                    .delete(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    /**
     * User Data generation for Elasticsearch based on database
     *
     * @return success or failure
     */
    public boolean generateUserDataForSearch() {

        boolean notClear = !checkAndDeleteIndex(EsConstant.INDEX_USER);
        // 如果无法清空之前的数据，抛出异常
        ThrowExceptionUtils.throwIfTrue(notClear, ResultCode.FAILED);

        List<UserDO> users = userMapper.getUsers();
        List<UserForSearchDTO> usersForSearch = DozerUtils.convertList(users, UserForSearchDTO.class);

        return bulkAddUserDataForSearch(usersForSearch);
    }

    private boolean bulkAddUserDataForSearch(List<UserForSearchDTO> users) {
        BulkRequest bulkRequest = new BulkRequest();

        users.forEach(u -> {
            IndexRequest request = new IndexRequest(EsConstant.INDEX_USER);

            String userId = u.getUserId();
            request.id(userId);

            String json = JsonUtils.toJson(u);
            request.source(json, XContentType.JSON);

            bulkRequest.add(request);
        });

        try {
            BulkResponse responses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            // 没问题返回 true，出现问题返回 false
            return !responses.hasFailures();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    @Async("asyncTaskExecutor")
    public void addUserDataToElasticsearchAsync(UserForSearchDTO user) {

        IndexRequest request = new IndexRequest(EsConstant.INDEX_USER);
        String userId = user.getUserId();
        String json = JsonUtils.toJson(user);
        request.id(userId).source(json, XContentType.JSON);
        try {
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Async("asyncTaskExecutor")
    public void removeUserFromElasticsearchAsync(String userId) {

        DeleteRequest request = new DeleteRequest(EsConstant.INDEX_USER, userId);
        try {
            client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重新生成用于搜索的网页数据。
     * <p>确保之前的数据已经清空，再根据数据库中的数据生成 Elasticsearch 的数据。</p>
     *
     * @return 是否成功
     */
    public boolean generateWebsiteDataForSearch() {

        boolean notClear = !checkAndDeleteIndex(EsConstant.INDEX_WEB);
        // 如果无法清空之前的数据，抛出未知异常
        ThrowExceptionUtils.throwIfTrue(notClear, ResultCode.FAILED);

        // 获取所有网页数据，包装为 Elasticsearch 需要的数据结构
        List<WebForSearchDTO> webs = websiteMapper.getAllPublicWebDataForSearch();
        // 清空之前的数据后，开始进行批量生成数据的操作
        return bulkAddWebsiteDataForSearch(webs);
    }

    private boolean bulkAddWebsiteDataForSearch(List<WebForSearchDTO> webs) {

        BulkRequest bulkRequest = new BulkRequest();

        for (WebForSearchDTO web : webs) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.INDEX_WEB);
            indexRequest.id(web.getUrl());

            String json = JsonUtils.toJson(web);
            indexRequest.source(json, XContentType.JSON);

            bulkRequest.add(indexRequest);
        }

        try {
            BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            // 没问题返回 true，出现问题返回 false
            return !response.hasFailures();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    @EmptyStringCheck
    public SearchResultsDTO searchUserData(
            @ExceptionIfEmpty(resultCode = ResultCode.NO_RESULTS_FOUND) String keyword,
            int from, int size) {

        SearchSourceBuilder source = new SearchSourceBuilder();

        // 模糊查询
        WildcardQueryBuilder wildcardQueryUsername = QueryBuilders
                .wildcardQuery(EsConstant.USER_NAME, keyword + "*")
                .boost(2.0F);

        WildcardQueryBuilder wildcardQueryUserId = QueryBuilders
                .wildcardQuery(EsConstant.USER_ID, keyword + "*");

        // 复合查询
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
                .should(wildcardQueryUsername)
                .should(wildcardQueryUserId)
                .minimumShouldMatch(1);

        // 放入 queryBuilder 后，再添加 timeout、分页和高亮
        source.query(boolQueryBuilder)
                .timeout(new TimeValue(1, TimeUnit.MINUTES))
                .from(from)
                .size(size);

        // search request
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(EsConstant.INDEX_USER).source(source);

        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            // 总数
            long totalCount = hits.getTotalHits().value;
            // 如果总数小于等于 0，说明没有结果，就抛出异常
            ThrowExceptionUtils.throwIfTrue(totalCount <= 0, ResultCode.NO_RESULTS_FOUND);
            // 总页数
            int totalPages = PageUtil.getAllPages((int) totalCount, size);
            // 分页后的结果
            List<UserForSearchWithWebCountDTO> paginatedResults = getUsersWithWebCountByHits(hits);

            return SearchResultsDTO.builder()
                    .paginatedResults(paginatedResults)
                    .totalCount(totalCount)
                    .totalPage(totalPages)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    private List<UserForSearchWithWebCountDTO> getUsersWithWebCountByHits(SearchHits hits) {
        List<UserForSearchWithWebCountDTO> userList = new ArrayList<>();
        hits.forEach(h -> {
            Map<String, Object> sourceAsMap = h.getSourceAsMap();
            UserForSearchWithWebCountDTO user = convertToUser(sourceAsMap);

            int webCount = websiteMapper.countUserPost(user.getUserName(), false);
            user.setWebCount(webCount);
            userList.add(user);
        });

        return userList;
    }

    private UserForSearchWithWebCountDTO convertToUser(Map<String, Object> map) {
        String userId = (String) map.get(EsConstant.USER_ID);
        String userName = (String) map.get(EsConstant.USER_NAME);
        String role = (String) map.get(EsConstant.ROLE);

        String time = (String) map.get(EsConstant.CREATION_TIME);

        Instant creationTime;
        try {
            creationTime = Instant.parse(time);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            throw new ServiceException(ResultCode.NO_RESULTS_FOUND);
        }

        ThrowExceptionUtils.throwIfNull(creationTime, ResultCode.NO_RESULTS_FOUND);

        return UserForSearchWithWebCountDTO.builder()
                .userId(userId)
                .userName(userName)
                .role(role)
                .createTime(creationTime)
                .build();
    }

    /**
     * 根据关键词搜索（还要统计关键词的次数来做热搜）
     *
     * @param keyword 关键词
     * @param from    from
     * @param size    size
     * @return 结果（搜索结果，总页数，错误信息等）
     * @throws ServiceException 关键词为空的情况， {@link EmptyStringCheck} 注解会抛出无匹配结果异常。
     *                          如果搜索结果为 0，也会抛出无结果异常。
     *                          如果出现网络异常，也会抛出异常。
     */
    @EmptyStringCheck
    public SearchResultsDTO searchWebsiteData(
            @ExceptionIfEmpty(resultCode = ResultCode.NO_RESULTS_FOUND) String keyword,
            int from, int size) {

        // 检测 keyword 的语言并选择合适的分词器
        String analyzer = detectLanguageAndGetAnalyzer(keyword);

        ElasticsearchManager elasticsearchManager =
                ApplicationContextUtils.getBean(ElasticsearchManager.class);
        // 将搜索词分词后放入热搜统计
        elasticsearchManager.analyzeKeywordAndPutToTrendsListAsync(keyword, analyzer);

        // 多字段匹配，title 的权限提高，设置分词器
        MultiMatchQueryBuilder multiMatchQuery = QueryBuilders
                .multiMatchQuery(keyword, EsConstant.DESC, EsConstant.TITLE)
                .field(EsConstant.TITLE, 2.0F);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(multiMatchQuery)
                .timeout(new TimeValue(1, TimeUnit.MINUTES))
                .highlighter(new HighlightBuilder()
                        .field(EsConstant.DESC)
                        .field(EsConstant.TITLE)
                        .preTags(EsConstant.PRE_TAGS)
                        .postTags(EsConstant.POST_TAGS)
                        .numOfFragments(0))
                .from(from).size(size);

        SearchRequest request = new SearchRequest(EsConstant.INDEX_WEB).source(sourceBuilder);

        SearchHits hits;

        try {
            hits = client.search(request, RequestOptions.DEFAULT).getHits();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }

        // 查询到的总数
        long totalCount = hits.getTotalHits().value;
        // 没有结果的时候，抛出无结果异常
        ThrowExceptionUtils.throwIfTrue(totalCount <= 0, ResultCode.NO_RESULTS_FOUND);

        // 总页数
        int totalPage = PageUtil.getAllPages((int) totalCount, size);

        // 获取需要的网页数据
        List<WebForSearchDTO> webs = elasticsearchManager
                .getWebsitesDataForSearchByHits(hits);

        return SearchResultsDTO.builder()
                .paginatedResults(webs)
                .totalCount(totalCount)
                .totalPage(totalPage)
                .build();

    }

    /**
     * 识别是哪国的语言，然后返回需要的 ES 分词器（目前支持英语、中文和日语）
     *
     * @param keyword 被检测的关键词
     * @return 需要的分词器
     */
    private String detectLanguageAndGetAnalyzer(String keyword) {

        Language lan = languageDetector.detectLanguageOf(keyword);

        // 默认使用英文分词器
        String analyzer = "english";

        if (Language.JAPANESE.equals(lan)) {
            // 如果是日语，使用日语的分词器
            analyzer = EsConstant.ANALYZER_JAPANESE;
        }

        if (Language.CHINESE.equals(lan)) {
            // 如果是中文，使用中文的分词器
            analyzer = EsConstant.ANALYZER_CHINESE;
        }

        return analyzer;
    }

    /**
     * 异步分解搜索的关键词，并加入到热搜列表中
     *
     * @param keyword  没有进行分词处理和语言识别的搜索词
     * @param analyzer 分词器
     */
    @Async("asyncTaskExecutor")
    public void analyzeKeywordAndPutToTrendsListAsync(String keyword, String analyzer) {

        if (StringUtils.isEmpty(keyword)) {
            return;
        }

        AnalyzeRequest request = AnalyzeRequest
                .withIndexAnalyzer(EsConstant.INDEX_WEB, analyzer, keyword);
        try {
            AnalyzeResponse analyze = client.indices()
                    .analyze(request, RequestOptions.DEFAULT);
            List<AnalyzeResponse.AnalyzeToken> tokens = analyze.getTokens();
            for (AnalyzeResponse.AnalyzeToken token : tokens) {
                String val = token.getTerm();
                if (val.length() > 1) {
                    // 统计字节数大于 1 的关键词，出现一次就加 1 个 score
                    trendsManager.addToTrendingList(val);
                }
            }
        } catch (IOException e) {
            log.info("IOException while adding data to trending list. "
                    + "Dropped this data because it's not important.");
            e.printStackTrace();
        }
    }

    /**
     * 根据 SearchHits 获取网页数据
     *
     * @param hits hits
     * @return 需要的网页数据
     */
    private List<WebForSearchDTO> getWebsitesDataForSearchByHits(SearchHits hits) {

        List<WebForSearchDTO> webs = new ArrayList<>();
        for (SearchHit hit : hits) {
            Map<String, Object> source = hitHighlightAndGetSource(hit,
                    EsConstant.DESC,
                    EsConstant.TITLE);
            WebForSearchDTO web = convertToWeb(source);
            webs.add(web);
        }

        return webs;
    }

    /**
     * 实现高亮
     *
     * @param hit   搜索结果
     * @param field 需要高亮的字段
     * @return 高亮后的结果
     */
    private Map<String, Object> hitHighlightAndGetSource(SearchHit hit, String... field) {

        Map<String, Object> source = hit.getSourceAsMap();
        Map<String, HighlightField> highlightFields = hit.getHighlightFields();

        for (String f : field) {
            HighlightField highlightField = highlightFields.get(f);
            if (highlightField != null) {
                Text[] texts = highlightField.fragments();

                StringBuilder sb = new StringBuilder();
                for (Text text : texts) {
                    sb.append(text);
                }

                source.put(f, sb.toString());
            }
        }

        return source;
    }

    /**
     * 转化为实体类
     *
     * @param source 待转化
     * @return 实体类
     */
    private WebForSearchDTO convertToWeb(Map<String, Object> source) {

        String title = (String) source.get(EsConstant.TITLE);
        String url = (String) source.get(EsConstant.URL);
        String img = (String) source.get(EsConstant.IMG);
        String desc = (String) source.get(EsConstant.DESC);

        return WebForSearchDTO.builder()
                .title(title)
                .url(url)
                .img(img)
                .desc(desc)
                .build();
    }

}