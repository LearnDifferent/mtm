package com.github.learndifferent.mtm.strategy.timeline;

import com.github.learndifferent.mtm.constant.consist.HomeTimelineConstant;
import com.github.learndifferent.mtm.entity.BookmarkDO;
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
    public BookmarksAndTotalPagesVO getHomeTimeline(String currentUsername,
                                                    String requestedUsername,
                                                    int from,
                                                    int size) {
        // check out all public bookmarks except the requested user's
        // this will include current user's private bookmarks if the requested user is not current user
        return getPublicIncludeCurrentPrivateExceptRequestedUserBookmark(
                currentUsername, requestedUsername, from, size);
    }

    private BookmarksAndTotalPagesVO getPublicIncludeCurrentPrivateExceptRequestedUserBookmark(
            String currentUsername, String requestedUsername, int from, int size) {

        List<BookmarkVO> bookmarks =
                getAllPublicSomePrivateExcludingSpecificUserBookmark(currentUsername, requestedUsername, from, size);

        int totalCount = this.bookmarkMapper
                .countAllPublicSomePrivateExcludingSpecificUserBookmark(currentUsername, requestedUsername);
        int totalPages = PaginationUtils.getTotalPages(totalCount, size);
        return BookmarksAndTotalPagesVO.builder().bookmarks(bookmarks).totalPages(totalPages).build();
    }

    /**
     * Get public bookmarks of all users and
     * some private bookmarks of the user whose username is {@code includePrivateUsername},
     * excluding the bookmarks of the user whose username is {@code excludeUsername}
     *
     * @param includePrivateUsername username of the user whose public and private bookmarks will be shown
     * @param excludeUsername        username of the user whose bookmarks will not be shown
     * @param from                   from
     * @param size                   size
     * @return bookmarks
     */
    private List<BookmarkVO> getAllPublicSomePrivateExcludingSpecificUserBookmark(
            String includePrivateUsername, String excludeUsername, int from, int size) {

        List<BookmarkDO> bookmarks = this.bookmarkMapper.getAllPublicSomePrivateExcludingSpecificUserBookmark(
                includePrivateUsername, excludeUsername, from, size);

        return convertToBookmarkVO(bookmarks);
    }
}
