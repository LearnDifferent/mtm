package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck.ExceptionIfEmpty;
import com.github.learndifferent.mtm.annotation.modify.webdata.WebsiteDataClean;
import com.github.learndifferent.mtm.constant.consist.EsConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.SearchMode;
import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import com.github.learndifferent.mtm.dto.search.SearchResultsDTO;
import com.github.learndifferent.mtm.dto.search.TagForSearchDTO;
import com.github.learndifferent.mtm.dto.search.UserForSearchDTO;
import com.github.learndifferent.mtm.dto.search.UserForSearchWithMoreInfo;
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
import com.github.learndifferent.mtm.utils.PaginationUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
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
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final LanguageDetector languageDetector;
    private final TagMapper tagMapper;

    @Autowired
    public ElasticsearchManager(@Qualifier("restHighLevelClient") RestHighLevelClient client,
                                BookmarkMapper bookmarkMapper,
                                TrendingManager trendingManager,
                                UserMapper userMapper,
                                LanguageDetector languageDetector,
                                TagMapper tagMapper) {
        this.client = client;
        this.bookmarkMapper = bookmarkMapper;
        this.trendingManager = trendingManager;
        this.userMapper = userMapper;
        this.languageDetector = languageDetector;
        this.tagMapper = tagMapper;
    }

    /**
     * Check the existent of data
     *
     * @param indexName name of the index
     * @return true if exists
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
                countEsDocsResult = countDocsAsync(EsConstant.INDEX_USER);
                databaseCount = userMapper.countUsers();
                break;
            case TAG:
                countEsDocsResult = countDocsAsync(EsConstant.INDEX_TAG);
                databaseCount = tagMapper.countDistinctTags();
                break;
            case WEB:
                countEsDocsResult = countDocsAsync(EsConstant.INDEX_WEB);
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
        IndexRequest request = new IndexRequest(EsConstant.INDEX_WEB);
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
        IndexRequest request = new IndexRequest(EsConstant.INDEX_USER);
        Integer id = user.getId();
        String json = JsonUtils.toJson(user);
        request.id(String.valueOf(id)).source(json, XContentType.JSON);
        return request;
    }

    /**
     * Check whether the index exists.
     * If not exists, return true.
     * If exists, delete the index and return whether the deletion is success.
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
        throwIfNotClear(EsConstant.INDEX_TAG);

        List<TagAndCountDO> data = tagMapper.getAllTagsAndCountOfPublicBookmarks();
        List<TagForSearchDTO> tcs = DozerUtils.convertList(data, TagForSearchDTO.class);

        BulkRequest bulkRequest = new BulkRequest();
        tcs.forEach(tc -> updateBulkRequest(bulkRequest, EsConstant.INDEX_TAG, tc.getTag(), JsonUtils.toJson(tc)));

        return sendBulkRequest(bulkRequest);
    }

    /**
     * User Data generation for Elasticsearch based on database
     *
     * @return true if success
     */
    public boolean generateUserData() {

        throwIfNotClear(EsConstant.INDEX_USER);

        List<UserDO> us = userMapper.getUsers(null, null);
        List<UserForSearchDTO> users = DozerUtils.convertList(us, UserForSearchDTO.class);

        BulkRequest bulkRequest = new BulkRequest();
        users.forEach(u -> updateBulkRequest(bulkRequest,
                EsConstant.INDEX_USER, String.valueOf(u.getId()), JsonUtils.toJson(u)));

        return sendBulkRequest(bulkRequest);
    }

    public boolean generateBasicWebData() {

        throwIfNotClear(EsConstant.INDEX_WEB);

        List<WebForSearchDTO> data = bookmarkMapper.getAllPublicBasicWebDataForSearch();

        BulkRequest bulkRequest = new BulkRequest();
        data.forEach(b -> updateBulkRequest(bulkRequest, EsConstant.INDEX_WEB, b.getUrl(), JsonUtils.toJson(b)));
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

        DeleteRequest request = new DeleteRequest(EsConstant.INDEX_USER, String.valueOf(id));
        try {
            client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SearchHits searchAndGetHits(SearchRequest searchRequest) throws IOException {
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        return response.getHits();
    }

    private long getTotalCount(SearchHits hits) {
        long totalCount = hits.getTotalHits().value;
        // check total number
        ThrowExceptionUtils.throwIfTrue(totalCount <= 0, ResultCode.NO_RESULTS_FOUND);
        return totalCount;
    }

    @EmptyStringCheck
    public SearchResultsDTO search(@ExceptionIfEmpty(resultCode = ResultCode.NO_RESULTS_FOUND) String keyword,
                                   int from,
                                   int size,
                                   SearchMode mode,
                                   Integer rangeFrom,
                                   Integer rangeTo) {
        try {
            return searchData(keyword.trim(), from, size, mode, rangeFrom, rangeTo);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    private SearchResultsDTO searchData(String keyword,
                                        int from,
                                        int size,
                                        SearchMode mode,
                                        Integer rangeFrom,
                                        Integer rangeTo) throws IOException {

        switch (mode) {
            case TAG:
                return searchTags(keyword, from, size, rangeFrom, rangeTo);
            case USER:
                return searchUsers(keyword, from, size);
            case WEB:
            default:
                return searchBookmarks(keyword, from, size);
        }
    }

    private SearchResultsDTO searchTags(String keyword,
                                        int from,
                                        int size,
                                        Integer rangeFrom,
                                        Integer rangeTo) throws IOException {

        SearchRequest searchRequest = getTagSearchRequest(keyword, from, size, rangeFrom, rangeTo);
        SearchHits hits = searchAndGetHits(searchRequest);
        // get total number of hits
        long totalCount = getTotalCount(hits);
        // get total pages
        int totalPages = PaginationUtils.getTotalPages((int) totalCount, size);
        // get results
        List<TagForSearchDTO> paginatedResults = getTagResults(hits);
        return SearchResultsDTO.builder()
                .paginatedResults(paginatedResults)
                .totalCount(totalCount)
                .totalPage(totalPages)
                .build();
    }

    private SearchRequest getTagSearchRequest(String keyword, int from, int size, Integer rangeFrom, Integer rangeTo) {

        // wildcard search query
        WildcardQueryBuilder wildcardQuery =
                QueryBuilders.wildcardQuery(EsConstant.TAG_NAME, keyword + "*");

        if (rangeFrom != null && rangeTo != null && rangeFrom > rangeTo) {
            // swap
            Integer tmp = rangeFrom;
            rangeFrom = rangeTo;
            rangeTo = tmp;
        }
        // range query
        RangeQueryBuilder rangeQuery =
                QueryBuilders.rangeQuery(EsConstant.TAG_NUMBER).gte(rangeFrom).lte(rangeTo);

        BoolQueryBuilder boolQuery = new BoolQueryBuilder()
                .must(wildcardQuery)
                .must(rangeQuery);

        SearchSourceBuilder source = new SearchSourceBuilder();
        source.query(boolQuery)
                .timeout(new TimeValue(1, TimeUnit.MINUTES))
                .from(from)
                .size(size)
                .sort(EsConstant.TAG_NUMBER, SortOrder.DESC);

        SearchRequest searchRequest = new SearchRequest();
        return searchRequest.indices(EsConstant.INDEX_TAG).source(source);
    }

    private List<TagForSearchDTO> getTagResults(SearchHits hits) {
        SearchHit[] hitsArray = hits.getHits();
        return Arrays.stream(hitsArray).map(h -> {
            Map<String, Object> map = h.getSourceAsMap();
            String tagName = String.valueOf(map.get(EsConstant.TAG_NAME));
            String tagNum = String.valueOf(map.get(EsConstant.TAG_NUMBER));
            Integer number = Integer.valueOf(tagNum);
            return TagForSearchDTO.builder().tag(tagName).number(number).build();
        }).collect(Collectors.toList());
    }

    private SearchResultsDTO searchUsers(String keyword, int from, int size) throws IOException {
        SearchRequest searchRequest = getUserSearchRequest(keyword, from, size);

        SearchHits hits = searchAndGetHits(searchRequest);
        long totalCount = getTotalCount(hits);
        int totalPages = PaginationUtils.getTotalPages((int) totalCount, size);
        List<UserForSearchWithMoreInfo> paginatedResults = getUserResults(hits);

        return SearchResultsDTO.builder()
                .paginatedResults(paginatedResults)
                .totalCount(totalCount)
                .totalPage(totalPages)
                .build();
    }

    private SearchRequest getUserSearchRequest(String keyword, int from, int size) {
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

        SearchSourceBuilder source = new SearchSourceBuilder();
        source.query(boolQueryBuilder)
                .highlighter(new HighlightBuilder()
                        .field(EsConstant.USER_NAME)
                        .field(EsConstant.USER_ID)
                        .numOfFragments(0))
                .timeout(new TimeValue(1, TimeUnit.MINUTES))
                .from(from)
                .size(size);

        // search request
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(EsConstant.INDEX_USER).source(source);
        return searchRequest;
    }

    private List<UserForSearchWithMoreInfo> getUserResults(SearchHits hits) {
        SearchHit[] hitsArray = hits.getHits();
        return Arrays.stream(hitsArray).map(h -> {
            // get user
            Map<String, Object> sourceAsMap = h.getSourceAsMap();
            UserForSearchWithMoreInfo user = convertToUser(sourceAsMap);

            // set highlighted fields
            Map<String, HighlightField> highlightFields = h.getHighlightFields();
            Set<String> fields = highlightFields.keySet();
            user.setHighlightedFields(new ArrayList<>(fields));

            return user;
        }).collect(Collectors.toList());
    }

    private UserForSearchWithMoreInfo convertToUser(Map<String, Object> source) {
        Integer id = (Integer) source.get(EsConstant.USER_ID);
        String userName = String.valueOf(source.get(EsConstant.USER_NAME));
        String role = String.valueOf(source.get(EsConstant.ROLE));

        String time = String.valueOf(source.get(EsConstant.CREATION_TIME));

        Instant creationTime;
        try {
            creationTime = Instant.parse(time);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            throw new ServiceException(ResultCode.NO_RESULTS_FOUND);
        }

        ThrowExceptionUtils.throwIfNull(creationTime, ResultCode.NO_RESULTS_FOUND);

        // the number of websites bookmarked by the user
        int number = bookmarkMapper.countUserBookmarks(userName, false);

        return UserForSearchWithMoreInfo.builder()
                .id(id)
                .userName(userName)
                .role(role)
                .createTime(creationTime)
                .webCount(number)
                .build();
    }

    private SearchResultsDTO searchBookmarks(String keyword, int from, int size)
            throws IOException {

        addToTrendingList(keyword);

        SearchRequest searchRequest = getBookmarkSearchRequest(keyword, from, size);

        SearchHits hits = searchAndGetHits(searchRequest);
        long totalCount = getTotalCount(hits);
        int totalPage = PaginationUtils.getTotalPages((int) totalCount, size);

        List<WebForSearchDTO> paginatedResults = getBookmarkResults(hits);

        return SearchResultsDTO.builder()
                .paginatedResults(paginatedResults)
                .totalCount(totalCount)
                .totalPage(totalPage)
                .build();
    }

    private SearchRequest getBookmarkSearchRequest(String keyword, int from, int size) {
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

        return new SearchRequest(EsConstant.INDEX_WEB).source(sourceBuilder);
    }

    private void addToTrendingList(String keyword) {
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
    public void analyzeAndAddTrendingAsync(String keyword, String analyzer) {

        if (StringUtils.isEmpty(keyword)) {
            return;
        }

        AnalyzeRequest request = AnalyzeRequest.withIndexAnalyzer(EsConstant.INDEX_WEB, analyzer, keyword);
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

    private List<WebForSearchDTO> getBookmarkResults(SearchHits hits) {

        SearchHit[] hitsArray = hits.getHits();
        return Arrays.stream(hitsArray)
                .map(this::convertToBookmark)
                .collect(Collectors.toList());
    }

    private WebForSearchDTO convertToBookmark(SearchHit hit) {
        Map<String, Object> source = hitHighlightAndGetSource(hit, EsConstant.DESC, EsConstant.TITLE);

        String title = String.valueOf(source.get(EsConstant.TITLE));
        String url = String.valueOf(source.get(EsConstant.URL));
        String img = String.valueOf(source.get(EsConstant.IMG));
        String desc = String.valueOf(source.get(EsConstant.DESC));

        return WebForSearchDTO.builder()
                .title(title)
                .url(url)
                .img(img)
                .desc(desc)
                .build();
    }

    /**
     * 实现高亮
     *
     * @param hit    搜索结果
     * @param fields 需要高亮的字段
     * @return 高亮后的结果
     */
    private Map<String, Object> hitHighlightAndGetSource(SearchHit hit, String... fields) {

        Map<String, Object> source = hit.getSourceAsMap();
        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
        Arrays.stream(fields).forEach(f -> updateSource(source, highlightFields, f));
        return source;
    }

    private void updateSource(Map<String, Object> source, Map<String, HighlightField> highlightFields, String field) {
        HighlightField highlightField = highlightFields.get(field);
        if (highlightField == null) {
            return;
        }

        Text[] texts = highlightField.fragments();
        StringBuilder sb = new StringBuilder();
        Arrays.stream(texts).forEach(sb::append);
        source.put(field, sb.toString());
    }
}