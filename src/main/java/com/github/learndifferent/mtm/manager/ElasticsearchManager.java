package com.github.learndifferent.mtm.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck.ExceptionIfEmpty;
import com.github.learndifferent.mtm.constant.consist.EsConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.SearchResultsDTO;
import com.github.learndifferent.mtm.dto.WebForSearchDTO;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.mapper.UserMapper;
import com.github.learndifferent.mtm.mapper.WebsiteMapper;
import com.github.learndifferent.mtm.utils.ApplicationContextUtils;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.JsonUtils;
import com.github.learndifferent.mtm.utils.PageUtil;
import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
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
import org.elasticsearch.index.query.QueryBuilders;
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
 * 操作 Elasticsearch
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

    @Autowired
    public ElasticsearchManager(@Qualifier("restHighLevelClient") RestHighLevelClient client,
                                WebsiteMapper websiteMapper,
                                TrendsManager trendsManager,
                                UserMapper userMapper) {
        this.client = client;
        this.websiteMapper = websiteMapper;
        this.trendsManager = trendsManager;
        this.userMapper = userMapper;
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
     * 在执行 websiteDataDiffFromDatabase() 方法之前，判断一下 Elasticsearch 中是否存在该 index。
     * <p>如果存在了，再执行。</p>
     * <p>如果不存在该 index，直接返回 true，表示 Elasticsearch 中的数据和数据库中的数据不同</p>
     *
     * @param existIndex Elasticsearch 中是否存在该 index
     * @return true 表示 Elasticsearch 中的数据和数据库中的数据条数不同
     */
    public boolean websiteDataDiffFromDatabase(boolean existIndex) {
        if (existIndex) {
            return websiteDataDiffFromDatabase();
        }
        return true;
    }

    /**
     * Elasticsearch 和数据库中的数据不同步
     *
     * @return true 表示 Elasticsearch 中的数据和数据库中的数据条数不同
     */
    private boolean websiteDataDiffFromDatabase() {
        // 数据库中的 distinct url 的数量
        long databaseUrlCount = websiteMapper.countDistinctPublicUrl();
        // Elasticsearch 中的文档的数量
        long elasticsearchDocCount = countWebsiteDocs();
        // 两者数量是否相同
        return 0 != databaseUrlCount - elasticsearchDocCount;
    }

    /**
     * 统计网页数据条数
     *
     * @return int 数据条数
     */
    private long countWebsiteDocs() {
        CountRequest request = new CountRequest(EsConstant.INDEX_WEB);
        try {
            CountResponse countResponse = client.count(request, RequestOptions.DEFAULT);
            return countResponse.getCount();
        } catch (IOException | ElasticsearchStatusException e) {
            if (e instanceof ElasticsearchStatusException) {
                log.warn("ElasticsearchStatusException while counting, " +
                        "which means the Index has been deleted. Returned minus one.");
            } else {
                log.error("IOException while counting. Returned minus one.");
                e.printStackTrace();
            }
            return -1L;
        }
    }

    /**
     * 异步存放文档
     *
     * @param websiteData                   需要存放的网页原始数据
     * @param ifFalseThenReturnTrueAsResult 如果传入的是 null 或 false，表示不要异步存放文档，
     *                                      此时直接返回 true 作为结果，表示无需异步存放。
     *                                      <p>如果传入的是 true，表示需要异步存放文档</p>
     * @return {@code Future<Boolean>} true 表示成功，或者无需存放；false 表示存放失败
     */
    public Future<Boolean> saveDocAsync(WebWithNoIdentityDTO websiteData,
                                        Boolean ifFalseThenReturnTrueAsResult) {
        // 如果 dontSave 为 true，表示无需异步存放此 Doc，直接返回 true 作为结果
        boolean dontSave = ifFalseThenReturnTrueAsResult == null
                || !ifFalseThenReturnTrueAsResult;

        if (dontSave) {
            return AsyncResult.forValue(true);
        }

        // 如果需要异步存放到 Elasticsearch 就执行内部方法 saveDocAsync
        ElasticsearchManager elasticsearchManager =
                ApplicationContextUtils.getBean(ElasticsearchManager.class);
        return elasticsearchManager.saveDocAsync(websiteData);
    }


    @Async("asyncTaskExecutor")
    public Future<Boolean> saveDocAsync(WebWithNoIdentityDTO websiteData) {

        WebForSearchDTO web = DozerUtils.convert(websiteData, WebForSearchDTO.class);

        boolean success = false;
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(web);
            IndexRequest request = new IndexRequest(EsConstant.INDEX_WEB);
            // 用网址 url 作为 ID
            request.id(web.getUrl());
            request.timeout("8s");
            request.source(json, XContentType.JSON);
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
            log.error("IOException while saving document to Elasticsearch. " +
                    "Dropped this data because it can be added manually.");
            e.printStackTrace();
        }
        return AsyncResult.forValue(success);
    }

    /**
     * 删除步骤：先检查该 index 是否存在，
     * <p>如果不存在，返回 true 表示已经删除；</p>
     * <p>如果不存在该 index，就执行删除</p>
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
     * 重新生成搜索数据。
     * <p>确保之前的数据已经清空，再根据数据库中的数据生成 Elasticsearch 的数据。</p>
     *
     * @return 是否成功
     */
    public boolean generateWebsiteDataForSearch() {

        boolean notClear = !checkAndDeleteIndex(EsConstant.INDEX_WEB);
        if (notClear) {
            // 如果无法清空之前的数据，抛出未知异常
            throw new ServiceException(ResultCode.ERROR);
        }

        // 清空之前的数据后，开始进行批量生成数据的操作
        return bulkAddWebsiteDataForSearch(getAllWebsitesDataForSearch());
    }

    /**
     * 获取所有网页数据，包装为 Elasticsearch 需要的数据结构
     *
     * @return 获取到的网页数据
     */
    private List<WebForSearchDTO> getAllWebsitesDataForSearch() {
        return websiteMapper.getAllPublicWebDataForSearch();
    }

    private boolean bulkAddWebsiteDataForSearch(List<WebForSearchDTO> webs) {

        BulkRequest bulkRequest = new BulkRequest();

        for (WebForSearchDTO web : webs) {
            String json = JsonUtils.toJson(web);
            IndexRequest indexRequest = new IndexRequest(EsConstant.INDEX_WEB);
            indexRequest.id(web.getUrl());
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

        ElasticsearchManager elasticsearchManager =
                ApplicationContextUtils.getBean(ElasticsearchManager.class);
        // 将搜索词分词后放入热搜统计
        elasticsearchManager.analyzeKeywordAndPutToTrendsListAsync(keyword);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery(keyword, EsConstant.DESC, EsConstant.TITLE))
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
        if (totalCount == 0) {
            // 没有结果的时候，抛出无结果异常
            throw new ServiceException(ResultCode.NO_RESULTS_FOUND);
        }

        // 总页数
        int totalPage = PageUtil.getAllPages((int) totalCount, size);

        // 获取需要的网页数据
        List<WebForSearchDTO> webs = elasticsearchManager
                .getWebsitesDataForSearchByHits(hits);

        return SearchResultsDTO.builder()
                .totalCount(totalCount)
                .totalPage(totalPage)
                .webs(webs)
                .build();
    }

    /**
     * 异步分解搜索的关键词，并加入到热搜列表中
     *
     * @param keyword 没有进行分词处理和语言识别的搜索词
     */
    @Async("asyncTaskExecutor")
    public void analyzeKeywordAndPutToTrendsListAsync(String keyword) {

        if (StringUtils.isEmpty(keyword)) {
            return;
        }

        // 检测 keyword 的语言并选择合适的分词器
        String analyzer = detectLanguageAndGetAnalyzer(keyword);

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
            log.info("IOException while adding data to trending list. " +
                    "Dropped this data because it's not important.");
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
            WebForSearchDTO web = hitHighlightAndGetWeb(hit,
                    EsConstant.DESC,
                    EsConstant.TITLE);
            webs.add(web);
        }

        return webs;
    }

    /**
     * 识别是哪国的语言，然后返回需要的 ES 分词器（目前支持英语、中文和日语）
     *
     * @param keyword 被检测的关键词
     * @return 需要的分词器
     */
    private String detectLanguageAndGetAnalyzer(String keyword) {

        LanguageDetector detector = LanguageDetectorBuilder
                .fromLanguages(Language.JAPANESE, Language.CHINESE)
                .build();
        Language lan = detector.detectLanguageOf(keyword);

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
     * 实现高亮并返回实体类
     *
     * @param hit   搜索结果
     * @param field 需要高亮的字段
     * @return 高亮后的实体类
     */
    private WebForSearchDTO hitHighlightAndGetWeb(SearchHit hit, String... field) {

        Map<String, Object> source = hit.getSourceAsMap();

        for (String f : field) {
            HighlightField highlightField = hit.getHighlightFields().get(f);
            if (highlightField != null) {
                Text[] texts = highlightField.fragments();

                StringBuilder sb = new StringBuilder();
                for (Text text : texts) {
                    sb.append(text);
                }

                source.put(f, sb.toString());
            }
        }

        return convertToWeb(source);
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
                .title(title).url(url).img(img).desc(desc)
                .build();
    }

}
