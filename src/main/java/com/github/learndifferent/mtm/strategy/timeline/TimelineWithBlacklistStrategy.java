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
 * Timeline with blacklist strategy
 *
 * @author zhou
 * @date 2023/8/10
 */
@Component(HomeTimelineConstant.TIMELINE_WITH_BLACKLIST)
@RequiredArgsConstructor
public class TimelineWithBlacklistStrategy implements HomeTimelineStrategy {

    private final BookmarkMapper bookmarkMapper;

    @Override
    public BookmarksAndTotalPagesVO getHomeTimeline(long currentUserId,
                                                    Long requestedUserId,
                                                    int from,
                                                    int size) {
        // check out all public bookmarks except the requested user's
        // this will include current user's private bookmarks if the requested user is not current user
        return getPublicIncludeCurrentPrivateExceptRequestedUserBookmark(
                currentUserId, requestedUserId, from, size);
    }

    private BookmarksAndTotalPagesVO getPublicIncludeCurrentPrivateExceptRequestedUserBookmark(
            long currentUserId, Long requestedUserId, int from, int size) {

        List<BookmarkVO> bookmarks = bookmarkMapper
                .filterBookmarksByUsers(currentUserId, requestedUserId, from, size);

        int totalCount = this.bookmarkMapper
                .countBookmarkByUsers(currentUserId, requestedUserId);
        int totalPages = PaginationUtils.getTotalPages(totalCount, size);
        return BookmarksAndTotalPagesVO.builder().bookmarks(bookmarks).totalPages(totalPages).build();
    }
}
