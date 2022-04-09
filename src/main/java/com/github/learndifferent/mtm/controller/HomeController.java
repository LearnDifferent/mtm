package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.constant.enums.HomeTimeline;
import com.github.learndifferent.mtm.constant.enums.PageInfoParam;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.query.FilterBookmarksRequest;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.vo.BookmarkVO;
import com.github.learndifferent.mtm.vo.BookmarksAndTotalPagesVO;
import com.github.learndifferent.mtm.vo.HomePageVO;
import com.github.learndifferent.mtm.vo.PopularBookmarksVO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
public class HomeController {

    private final WebsiteService websiteService;

    @Autowired
    public HomeController(WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

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

        String currentUser = StpUtil.getLoginIdAsString();
        BookmarksAndTotalPagesVO data =
                websiteService.getHomeTimeline(currentUser, timeline, requestedUsername, pageInfo);

        return HomePageVO.builder()
                .currentUser(currentUser)
                .bookmarksAndTotalPages(data)
                .requestedUsername(requestedUsername)
                .build();
    }

    /**
     * Filter bookmarked sites
     *
     * @param filter filter request
     * @return filtered bookmarked sites
     */
    @PostMapping("/filter")
    public List<BookmarkVO> filter(@RequestBody FilterBookmarksRequest filter) {
        return websiteService.filterPublicBookmarks(filter);
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

        return websiteService.getPopularBookmarksAndTotalPages(pageInfo);
    }
}