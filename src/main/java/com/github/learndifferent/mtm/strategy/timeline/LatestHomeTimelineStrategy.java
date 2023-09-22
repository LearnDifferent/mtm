package com.github.learndifferent.mtm.strategy.timeline;

import com.github.learndifferent.mtm.constant.consist.HomeTimelineConstant;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.utils.PaginationUtils;
import com.github.learndifferent.mtm.vo.BookmarkVO;
import com.github.learndifferent.mtm.vo.BookmarksAndTotalPagesVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Latest Home Timeline Strategy
 *
 * @author zhou
 * @date 2023/8/10
 */
@Component(HomeTimelineConstant.LATEST_TIMELINE)
@RequiredArgsConstructor
public class LatestHomeTimelineStrategy implements HomeTimelineStrategy {

    private final BookmarkMapper bookmarkMapper;

    @Override
    public BookmarksAndTotalPagesVO getHomeTimeline(long currentUserId,
                                                    Long requestedUserId,
                                                    int from,
                                                    int size) {
        // get all public bookmarks and current user's private bookmarks
        List<BookmarkVO> bookmarks =
                bookmarkMapper.getPublicAndUserOwnedPrivateBookmarks(from, size, currentUserId);

        int totalCount = bookmarkMapper.countPublicAndUserOwnedPrivateBookmarks(currentUserId);
        int totalPages = PaginationUtils.getTotalPages(totalCount, size);
        return BookmarksAndTotalPagesVO.builder().bookmarks(bookmarks).totalPages(totalPages).build();
    }
}
