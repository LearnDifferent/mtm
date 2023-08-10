package com.github.learndifferent.mtm.strategy.search.main;

import com.github.learndifferent.mtm.constant.consist.SearchConstant;
import com.github.learndifferent.mtm.dto.search.SearchResultsDTO;
import com.github.learndifferent.mtm.dto.search.WebForSearchDTO;
import com.github.learndifferent.mtm.manager.SearchManager;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.utils.PaginationUtils;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Bookmark data search in MySQL
 *
 * @author zhou
 * @date 2023/8/10
 */
@Component(SearchConstant.SEARCH_STRATEGY_BEAN_NAME_PREFIX + SearchConstant.SEARCH_BOOKMARK_IN_MYSQL)
@RequiredArgsConstructor
public class BookmarkDataSearchMySqlStrategy implements DataSearchStrategy {

    private final SearchManager searchManager;
    private final BookmarkMapper bookmarkMapper;

    @Override
    public SearchResultsDTO search(String keyword, int from, int size, Integer rangeFrom, Integer rangeTo)
            throws IOException {
        return searchBookmarksMySql(keyword, from, size);
    }

    private SearchResultsDTO searchBookmarksMySql(String keyword, int from, int size) {

        this.searchManager.addToTrendingList(keyword);

        List<WebForSearchDTO> paginatedResults = this.bookmarkMapper
                .searchWebDataByKeyword(keyword, from, size);
        long totalCount = this.bookmarkMapper.countWebDataByKeyword(keyword);
        int totalPages = PaginationUtils.getTotalPages((int) totalCount, size);

        return SearchResultsDTO.builder()
                .paginatedResults(paginatedResults)
                .totalCount(totalCount)
                .totalPage(totalPages)
                .build();
    }
}
