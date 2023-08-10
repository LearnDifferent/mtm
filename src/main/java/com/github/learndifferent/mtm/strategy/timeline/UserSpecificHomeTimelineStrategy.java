package com.github.learndifferent.mtm.strategy.timeline;

import com.github.learndifferent.mtm.constant.consist.HomeTimelineConstant;
import com.github.learndifferent.mtm.constant.enums.AccessPrivilege;
import com.github.learndifferent.mtm.entity.BookmarkDO;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.utils.PaginationUtils;
import com.github.learndifferent.mtm.vo.BookmarkVO;
import com.github.learndifferent.mtm.vo.BookmarksAndTotalPagesVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * User-specific home timeline strategy
 *
 * @author zhou
 * @date 2023/8/10
 */
@Component(HomeTimelineConstant.USER_SPECIFIC_TIMELINE)
@RequiredArgsConstructor
public class UserSpecificHomeTimelineStrategy implements HomeTimelineStrategy {

    private final BookmarkMapper bookmarkMapper;

    @Override
    public BookmarksAndTotalPagesVO getHomeTimeline(String currentUsername,
                                                    String requestedUsername,
                                                    int from,
                                                    int size) {
        // check whether the current user is requested user
        boolean isCurrentUser = currentUsername.equalsIgnoreCase(requestedUsername);

        // if the current user is requesting his own data, then he can access his private data
        AccessPrivilege privilege = isCurrentUser ? AccessPrivilege.ALL : AccessPrivilege.LIMITED;

        // check out public bookmarks of the requested user's
        // this will include private bookmarks if the requested user is current user
        return getUserBookmarks(requestedUsername, from, size, privilege);
    }

    private BookmarksAndTotalPagesVO getUserBookmarks(String username,
                                                      int from,
                                                      int size,
                                                      AccessPrivilege privilege) {

        int totalCounts = bookmarkMapper.countUserBookmarks(username, privilege.canAccessPrivateData());
        int totalPages = PaginationUtils.getTotalPages(totalCounts, size);

        List<BookmarkDO> b = bookmarkMapper.getUserBookmarks(username, from, size, privilege.canAccessPrivateData());
        List<BookmarkVO> bookmarks = convertToBookmarkVO(b);

        return BookmarksAndTotalPagesVO.builder().totalPages(totalPages).bookmarks(bookmarks).build();
    }
}
