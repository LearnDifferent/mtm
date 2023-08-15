package com.github.learndifferent.mtm.strategy.search.main;

import com.github.learndifferent.mtm.constant.consist.SearchConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.search.SearchResultsDTO;
import com.github.learndifferent.mtm.dto.search.UserForSearchWithMoreInfo;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.manager.SearchManager;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.utils.PaginationUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Component;

/**
 * User data search in Elasticsearch
 *
 * @author zhou
 * @date 2023/8/10
 */
@Component(SearchConstant.SEARCH_STRATEGY_BEAN_NAME_PREFIX + SearchConstant.INDEX_USER)
@RequiredArgsConstructor
@Slf4j
public class UserDataSearchElasticsearchStrategy implements DataSearchStrategy {

    private final SearchManager searchManager;
    private final BookmarkMapper bookmarkMapper;

    @Override
    public SearchResultsDTO search(String keyword, int from, int size, Integer rangeFrom, Integer rangeTo)
            throws IOException {
        return searchUsersElasticsearch(keyword, from, size);
    }

    private SearchResultsDTO searchUsersElasticsearch(String keyword, int from, int size) throws IOException {
        SearchRequest searchRequest = getUserSearchRequest(keyword, from, size);

        SearchHits hits = searchManager.searchAndGetHits(searchRequest);
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
                .wildcardQuery(SearchConstant.USER_NAME, keyword + "*")
                .boost(2.0F);

        WildcardQueryBuilder wildcardQueryUserId = QueryBuilders
                .wildcardQuery(SearchConstant.USER_ID, keyword + "*");

        // 复合查询
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
                .should(wildcardQueryUsername)
                .should(wildcardQueryUserId)
                .minimumShouldMatch(1);

        SearchSourceBuilder source = new SearchSourceBuilder();
        source.query(boolQueryBuilder)
                .highlighter(new HighlightBuilder()
                        .field(SearchConstant.USER_NAME)
                        .field(SearchConstant.USER_ID)
                        .numOfFragments(0))
                .timeout(new TimeValue(1, TimeUnit.MINUTES))
                .from(from)
                .size(size);

        // search request
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(SearchConstant.INDEX_USER).source(source);
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
        Integer id = (Integer) source.get(SearchConstant.USER_ID);
        String userName = String.valueOf(source.get(SearchConstant.USER_NAME));
        String role = String.valueOf(source.get(SearchConstant.ROLE));

        String time = String.valueOf(source.get(SearchConstant.CREATION_TIME));

        Instant creationTime;
        try {
            creationTime = Instant.parse(time);
        } catch (DateTimeParseException e) {
            log.error("Invalid creation time: {}", time, e);
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
                .bookmarkNumber(number)
                .build();
    }
}