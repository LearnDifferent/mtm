package com.github.learndifferent.mtm.strategy.search.main;

import com.github.learndifferent.mtm.constant.consist.SearchConstant;
import com.github.learndifferent.mtm.dto.search.SearchResultsDTO;
import com.github.learndifferent.mtm.dto.search.TagForSearchDTO;
import com.github.learndifferent.mtm.mapper.TagMapper;
import com.github.learndifferent.mtm.utils.LoginUtils;
import com.github.learndifferent.mtm.utils.PaginationUtils;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Tag data search in MySQL
 *
 * @author zhou
 * @date 2023/8/10
 */
@Component(SearchConstant.SEARCH_STRATEGY_BEAN_NAME_PREFIX + SearchConstant.SEARCH_TAG_IN_MYSQL)
@RequiredArgsConstructor
public class TagDataSearchMySqlStrategy implements DataSearchStrategy {

    private final TagMapper tagMapper;

    @Override
    public SearchResultsDTO search(String keyword, int from, int size, Integer rangeFrom, Integer rangeTo)
            throws IOException {
        return this.searchTagMySql(keyword, from, size, rangeFrom, rangeTo);
    }

    private SearchResultsDTO searchTagMySql(String keyword, int from, int size, Integer rangeFrom, Integer rangeTo) {
        if (rangeFrom != null && rangeTo != null && rangeFrom > rangeTo) {
            Integer tmp = rangeFrom;
            rangeFrom = rangeTo;
            rangeTo = tmp;
        }

        String username = LoginUtils.getCurrentUsername();
        List<TagForSearchDTO> tagData = this.tagMapper
                .searchTagDataByKeywordAndRange(keyword, username, rangeFrom, rangeTo, from, size);
        long totalCount = this.tagMapper.countTagDataByKeywordAndRange(keyword, username, rangeFrom, rangeTo);
        int totalPages = PaginationUtils.getTotalPages((int) totalCount, size);

        return SearchResultsDTO.builder()
                .paginatedResults(tagData)
                .totalCount(totalCount)
                .totalPage(totalPages)
                .build();
    }
}
