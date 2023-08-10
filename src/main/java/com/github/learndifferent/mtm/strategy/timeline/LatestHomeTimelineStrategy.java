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
    public BookmarksAndTotalPagesVO getHomeTimeline(String currentUsername,
                                                    String requestedUsername,
                                                    int from,
                                                    int size) {
        // get all public bookmarks and current user's private bookmarks
        List<BookmarkVO> bookmarks =
                getAllPublicAndSpecificPrivateBookmarks(from, size, currentUsername);

        int totalCount = bookmarkMapper.countAllPublicAndSpecificPrivateBookmarks(currentUsername);
        int totalPages = PaginationUtils.getTotalPages(totalCount, size);
        return BookmarksAndTotalPagesVO.builder().bookmarks(bookmarks).totalPages(totalPages).build();
    }

    /**
     * Get public bookmarks of all users and private bookmarks of specific user
     * <p>
     * The result will not be paginated if {@code from} or {@code size} is null
     * </p>
     *
     * @param from         from
     *                     <p>The result will not be paginated if {@code from} or {@code size} is null</p>
     * @param size         size
     *                     <p>The result will not be paginated if {@code from} or {@code size} is null</p>
     * @param specUsername username of the user whose public and private bookmarks will be shown
     * @return public bookmarks of all users and private bookmarks of specific user
     */
    private List<BookmarkVO> getAllPublicAndSpecificPrivateBookmarks(Integer from,
                                                                     Integer size,
                                                                     String specUsername) {
        List<BookmarkDO> bookmarks = bookmarkMapper.getAllPublicAndSpecificPrivateBookmarks(from, size, specUsername);
        return convertToBookmarkVO(bookmarks);
    }
}
