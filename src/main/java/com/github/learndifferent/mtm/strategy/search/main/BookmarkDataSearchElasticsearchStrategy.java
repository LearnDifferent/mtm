package com.github.learndifferent.mtm.strategy.search.main;

import com.github.learndifferent.mtm.constant.consist.SearchConstant;
import com.github.learndifferent.mtm.dto.search.SearchResultsDTO;
import com.github.learndifferent.mtm.dto.search.WebForSearchDTO;
import com.github.learndifferent.mtm.manager.SearchManager;
import com.github.learndifferent.mtm.utils.PaginationUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Component;

/**
 * Bookmark data search in Elasticsearch
 *
 * @author zhou
 * @date 2023/8/10
 */
@Component(SearchConstant.SEARCH_STRATEGY_BEAN_NAME_PREFIX + SearchConstant.INDEX_WEB)
@RequiredArgsConstructor
public class BookmarkDataSearchElasticsearchStrategy implements DataSearchStrategy {

    private final SearchManager searchManager;

    @Override
    public SearchResultsDTO search(String keyword, int from, int size, Integer rangeFrom, Integer rangeTo)
            throws IOException {
        return searchBookmarksElasticsearch(keyword, from, size);
    }

    private SearchResultsDTO searchBookmarksElasticsearch(String keyword, int from, int size)
            throws IOException {

        this.searchManager.addToTrendingList(keyword);

        SearchRequest searchRequest = getBookmarkSearchRequest(keyword, from, size);

        SearchHits hits = this.searchManager.searchAndGetHits(searchRequest);
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
                .multiMatchQuery(keyword, SearchConstant.DESC, SearchConstant.TITLE)
                .field(SearchConstant.TITLE, 2.0F);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(multiMatchQuery)
                .timeout(new TimeValue(1, TimeUnit.MINUTES))
                .highlighter(new HighlightBuilder()
                        .field(SearchConstant.DESC)
                        .field(SearchConstant.TITLE)
                        .preTags(SearchConstant.PRE_TAGS)
                        .postTags(SearchConstant.POST_TAGS)
                        .numOfFragments(0))
                .from(from).size(size);

        return new SearchRequest(SearchConstant.INDEX_WEB).source(sourceBuilder);
    }

    private List<WebForSearchDTO> getBookmarkResults(SearchHits hits) {

        SearchHit[] hitsArray = hits.getHits();
        return Arrays.stream(hitsArray)
                .map(this::convertToBookmark)
                .collect(Collectors.toList());
    }

    private WebForSearchDTO convertToBookmark(SearchHit hit) {
        Map<String, Object> source = hitHighlightAndGetSource(hit, SearchConstant.DESC, SearchConstant.TITLE);

        String title = String.valueOf(source.get(SearchConstant.TITLE));
        String url = String.valueOf(source.get(SearchConstant.URL));
        String img = String.valueOf(source.get(SearchConstant.IMG));
        String desc = String.valueOf(source.get(SearchConstant.DESC));

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
