package com.github.learndifferent.mtm.strategy.timeline;

import com.github.learndifferent.mtm.entity.BookmarkDO;
import com.github.learndifferent.mtm.utils.BeanUtils;
import com.github.learndifferent.mtm.vo.BookmarkVO;
import com.github.learndifferent.mtm.vo.BookmarksAndTotalPagesVO;
import java.util.List;

/**
 * Home timeline strategy
 *
 * @author zhou
 * @date 2023/8/10
 */
public interface HomeTimelineStrategy {

    /**
     * Get bookmarked websites and total pages for the current user on the home page
     *
     * @param currentUsername   username of the user that is currently logged in
     * @param requestedUsername username of the user whose data is being requested
     * @param from              from
     * @param size              size
     * @return Paginated bookmarks and total Pages
     */
    BookmarksAndTotalPagesVO getHomeTimeline(String currentUsername, String requestedUsername, int from, int size);

    /**
     * Convert list of {@link BookmarkDO} to list of {@link BookmarkVO}
     *
     * @param bookmarks list of {@link BookmarkDO}
     * @return list of {@link BookmarkVO}
     */
    default List<BookmarkVO> convertToBookmarkVO(List<BookmarkDO> bookmarks) {
        return BeanUtils.convertList(bookmarks, BookmarkVO.class);
    }
}
