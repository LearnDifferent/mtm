package com.github.learndifferent.mtm.service.impl;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.learndifferent.mtm.constant.consist.SearchConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.SearchMode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.search.SearchResultsDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.manager.SearchManager;
import com.github.learndifferent.mtm.strategy.search.main.DataSearchStrategyContext;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {

    @InjectMocks
    private SearchServiceImpl searchService;

    @Mock
    private SearchManager searchManager;

    @Mock
    private DataSearchStrategyContext dataSearchStrategyContext;

    @Nested
    class search {

        private final String KEYWORD = "keyword";
        private final SearchMode MODE = SearchMode.WEB;
        private final int FROM = 0;
        private final int SIZE = 1;
        private final PageInfoDTO PAGE_INFO = new PageInfoDTO(FROM, SIZE);
        private final SearchResultsDTO RESULT = new SearchResultsDTO();

        @Test
        @DisplayName("Should get the search result")
        void shouldGetTheSearchResult() throws IOException {
            String strategyName = SearchConstant.SEARCH_STRATEGY_BEAN_NAME_PREFIX + MODE.mode();
            Mockito.when(dataSearchStrategyContext.search(strategyName, KEYWORD, FROM, SIZE, null, null))
                    .thenReturn(RESULT);

            SearchResultsDTO searchResult = searchService.search(MODE, KEYWORD, PAGE_INFO, null, null);
            assertEquals(RESULT, searchResult);
        }

        @Test
        @DisplayName("Should throw an exception")
        void shouldThrowAnException() throws IOException {
            String strategyName = SearchConstant.SEARCH_STRATEGY_BEAN_NAME_PREFIX + MODE.mode();
            Mockito.when(dataSearchStrategyContext
                            .search(strategyName, KEYWORD, FROM, SIZE, null, null))
                    .thenThrow(new ServiceException(ResultCode.CONNECTION_ERROR));

            assertThrows(ServiceException.class,
                    () -> searchService.search(MODE, KEYWORD, PAGE_INFO, null, null));
        }
    }
}