package com.github.learndifferent.mtm.strategy.search.main;

import com.github.learndifferent.mtm.constant.consist.SearchConstant;
import com.github.learndifferent.mtm.dto.search.SearchResultsDTO;
import com.github.learndifferent.mtm.dto.search.UserForSearchWithMoreInfo;
import com.github.learndifferent.mtm.mapper.UserMapper;
import com.github.learndifferent.mtm.utils.PaginationUtils;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * User data search in MySQL
 *
 * @author zhou
 * @date 2023/8/10
 */
@Component(SearchConstant.SEARCH_STRATEGY_BEAN_NAME_PREFIX + SearchConstant.SEARCH_USER_IN_MYSQL)
@RequiredArgsConstructor
public class UserDataSearchMySqlStrategy implements DataSearchStrategy {

    private final UserMapper userMapper;

    @Override
    public SearchResultsDTO search(String keyword, int from, int size, Integer rangeFrom, Integer rangeTo)
            throws IOException {
        return searchUsersMySql(keyword, from, size);
    }

    private SearchResultsDTO searchUsersMySql(String keyword, int from, int size) {
        List<UserForSearchWithMoreInfo> userData = userMapper
                .searchUserDataByKeyword(keyword, from, size);

        Long count = userMapper.countUserByKeyword(keyword);
        long totalCount = Optional.ofNullable(count).orElse(0L);
        int totalPages = PaginationUtils.getTotalPages((int) totalCount, size);

        return SearchResultsDTO.builder()
                .paginatedResults(userData)
                .totalCount(totalCount)
                .totalPage(totalPages)
                .build();
    }

}