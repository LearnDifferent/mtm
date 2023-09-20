package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.constant.consist.ErrorInfoConstant;
import com.github.learndifferent.mtm.constant.enums.HomeTimeline;
import com.github.learndifferent.mtm.constant.enums.Order;
import com.github.learndifferent.mtm.constant.enums.OrderField;
import com.github.learndifferent.mtm.constant.enums.PageInfoParam;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.query.UsernamesRequest;
import com.github.learndifferent.mtm.service.BookmarkService;
import com.github.learndifferent.mtm.utils.LoginUtils;
import com.github.learndifferent.mtm.vo.BookmarkVO;
import com.github.learndifferent.mtm.vo.BookmarksAndTotalPagesVO;
import com.github.learndifferent.mtm.vo.HomePageVO;
import com.github.learndifferent.mtm.vo.PopularBookmarksVO;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Home Page Controller
 *
 * @author zhou
 * @date 2021/09/05
 */
@RestController
@RequestMapping("/home")
@Validated
@RequiredArgsConstructor
public class HomeController {

    private final BookmarkService bookmarkService;

    /**
     * Get {@link HomePageVO} Data
     *
     * @param timeline          how to display the stream of bookmarks on the home page
     * @param requestedUsername username of the user whose data is being requested
     *                          <p>{@code requestedUsername} is not required</p>
     * @param pageInfo          pagination information
     * @return {@link HomePageVO} Data
     */
    @GetMapping
    public HomePageVO getHomePageData(
            @RequestParam("timeline") HomeTimeline timeline,
            @RequestParam(value = "requestedUsername", required = false) String requestedUsername,
            @PageInfo(size = 12, paramName = PageInfoParam.CURRENT_PAGE) PageInfoDTO pageInfo) {

        String currentUser = LoginUtils.getCurrentUsername();
        BookmarksAndTotalPagesVO data =
                bookmarkService.getHomeTimeline(currentUser, timeline, requestedUsername, pageInfo);

        return HomePageVO.builder()
                .currentUser(currentUser)
                .bookmarksAndTotalPages(data)
                .requestedUsername(requestedUsername)
                .build();
    }

    /**
     * Filter public bookmarked sites
     * <p>
     * {@code fromTimestamp} and {@code toTimestamp} is used to query a specific range of time.
     * </p>
     * <li>It will not query a range of time if both of them are null.</li>
     * <li>It will set the null value to the current time if one of them is null</li>
     * <li>It will swap the two values if necessary.</li>
     *
     * @param usernames     request body that contains usernames
     *                      <p>null or empty if select all users</p>
     * @param load          amount of data to load
     * @param fromTimestamp filter by time
     * @param toTimestamp   filter by time
     * @param orderField    order by the field
     * @param order         {@link Order#ASC} if ascending order, {@link Order#DESC} if descending order
     * @return filtered bookmarked websites
     */
    @PostMapping("/filter")
    public List<BookmarkVO> filter(@RequestBody UsernamesRequest usernames,
                                   @RequestParam("load")
                                   @NotNull(message = ErrorInfoConstant.NO_DATA)
                                   @Positive(message = ErrorInfoConstant.NO_DATA)
                                           Integer load,
                                   @RequestParam(value = "fromTimestamp", required = false) String fromTimestamp,
                                   @RequestParam(value = "toTimestamp", required = false) String toTimestamp,
                                   @RequestParam("orderField") OrderField orderField,
                                   @RequestParam("order") Order order) {
        return bookmarkService.filterPublicBookmarks(usernames, load, fromTimestamp, toTimestamp, orderField, order);
    }

    /**
     * Get popular bookmarks and total pages
     *
     * @param pageInfo pagination information
     * @return popular bookmarks and total pages
     */
    @GetMapping("/popular")
    public PopularBookmarksVO getPopularBookmarks(
            @PageInfo(size = 12, paramName = PageInfoParam.CURRENT_PAGE) PageInfoDTO pageInfo) {

        return bookmarkService.getPopularBookmarksAndTotalPages(pageInfo);
    }
}