package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.constant.enums.HomeTimeline;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.entity.BookmarkDO;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.utils.PaginationUtils;
import com.github.learndifferent.mtm.vo.BookmarksAndTotalPagesVO;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceImplTest {

    @InjectMocks
    private BookmarkServiceImpl bookmarkService;

    @Mock
    private BookmarkMapper bookmarkMapper;

    @Test
    @DisplayName("should get the bookmarks and total pages")
    void shouldGetTheBookmarksAndTotalPages() {

        String currentUser = "current";
        int from = 0;
        int size = 10;
        int totalNumber = 100;
        PageInfoDTO pageInfo = PageInfoDTO.builder().from(from).size(size).build();

        List<BookmarkDO> list = new ArrayList<>();
        for (int i = from; i < size; i++) {
            // add empty data
            list.add(new BookmarkDO());
        }

        Mockito.when(bookmarkMapper.getAllPublicAndSpecificPrivateBookmarks(from, size, currentUser))
                .thenReturn(list);
        Mockito.when(bookmarkMapper.countAllPublicAndSpecificPrivateBookmarks(currentUser))
                .thenReturn(totalNumber);

        BookmarksAndTotalPagesVO result = bookmarkService.getHomeTimeline(
                currentUser, HomeTimeline.LATEST, "", pageInfo);

        Assertions.assertEquals(size - from, result.getBookmarks().size());

        int expectedTotalPages = PaginationUtils.getTotalPages(totalNumber, size);
        Assertions.assertEquals(expectedTotalPages, result.getTotalPages());
    }
}