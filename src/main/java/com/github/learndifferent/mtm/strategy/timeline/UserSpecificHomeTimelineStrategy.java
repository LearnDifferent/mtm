package com.github.learndifferent.mtm.strategy.timeline;

import com.github.learndifferent.mtm.constant.consist.HomeTimelineConstant;
import com.github.learndifferent.mtm.constant.enums.AccessPrivilege;
import com.github.learndifferent.mtm.manager.UserManager;
import com.github.learndifferent.mtm.vo.BookmarksAndTotalPagesVO;
import java.util.Objects;
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

    private final UserManager userManager;

    @Override
    public BookmarksAndTotalPagesVO getHomeTimeline(long currentUserId,
                                                    Long requestedUserId,
                                                    int from,
                                                    int size) {
        // check whether the current user is requested user
        boolean isCurrentUser = Objects.nonNull(requestedUserId)
                && currentUserId == requestedUserId;

        // if the current user is requesting his own data, then he can access his private data
        AccessPrivilege privilege = isCurrentUser ? AccessPrivilege.ALL : AccessPrivilege.LIMITED;

        // check out public bookmarks of the requested user's
        // this will include private bookmarks if the requested user is current user
        return this.userManager.getUserBookmarks(requestedUserId, from, size, privilege);
    }
}
