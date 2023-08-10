package com.github.learndifferent.mtm.strategy.search.main;

import com.github.learndifferent.mtm.constant.consist.SearchConstant;
import com.github.learndifferent.mtm.dto.search.SearchResultsDTO;
import com.github.learndifferent.mtm.dto.search.TagForSearchDTO;
import com.github.learndifferent.mtm.manager.ElasticsearchManager;
import com.github.learndifferent.mtm.utils.PaginationUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;

/**
 * Tag data search in Elasticsearch
 *
 * @author zhou
 * @date 2023/8/10
 */
@Component(SearchConstant.SEARCH_STRATEGY_BEAN_NAME_PREFIX + SearchConstant.INDEX_TAG)
@RequiredArgsConstructor
public class TagDataSearchElasticsearchStrategy implements DataSearchStrategy {

    private final ElasticsearchManager elasticsearchManager;

    @Override
    public SearchResultsDTO search(String keyword, int from, int size, Integer rangeFrom, Integer rangeTo)
            throws IOException {
        return searchTagsElasticsearch(keyword, from, size, rangeFrom, rangeTo);
    }

    private SearchResultsDTO searchTagsElasticsearch(String keyword,
                                                     int from,
                                                     int size,
                                                     Integer rangeFrom,
                                                     Integer rangeTo) throws IOException {

        SearchRequest searchRequest = getTagSearchRequest(keyword, from, size, rangeFrom, rangeTo);
        SearchHits hits = this.elasticsearchManager.searchAndGetHits(searchRequest);
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
                QueryBuilders.wildcardQuery(SearchConstant.TAG_NAME, keyword + "*");

        if (rangeFrom != null && rangeTo != null && rangeFrom > rangeTo) {
            // swap
            Integer tmp = rangeFrom;
            rangeFrom = rangeTo;
            rangeTo = tmp;
        }
        // range query
        RangeQueryBuilder rangeQuery =
                QueryBuilders.rangeQuery(SearchConstant.TAG_NUMBER).gte(rangeFrom).lte(rangeTo);

        BoolQueryBuilder boolQuery = new BoolQueryBuilder()
                .must(wildcardQuery)
                .must(rangeQuery);

        SearchSourceBuilder source = new SearchSourceBuilder();
        source.query(boolQuery)
                .timeout(new TimeValue(1, TimeUnit.MINUTES))
                .from(from)
                .size(size)
                .sort(SearchConstant.TAG_NUMBER, SortOrder.DESC);

        SearchRequest searchRequest = new SearchRequest();
        return searchRequest.indices(SearchConstant.INDEX_TAG).source(source);
    }


    private List<TagForSearchDTO> getTagResults(SearchHits hits) {
        SearchHit[] hitsArray = hits.getHits();
        return Arrays.stream(hitsArray).map(h -> {
            Map<String, Object> map = h.getSourceAsMap();
            String tagName = String.valueOf(map.get(SearchConstant.TAG_NAME));
            String tagNum = String.valueOf(map.get(SearchConstant.TAG_NUMBER));
            Integer number = Integer.valueOf(tagNum);
            return TagForSearchDTO.builder().tag(tagName).number(number).build();
        }).collect(Collectors.toList());
    }
}
