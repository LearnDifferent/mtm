package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.constant.enums.HomeTimeline;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.constant.enums.PageInfoParam;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.UserWithWebCountDTO;
import com.github.learndifferent.mtm.dto.WebDataAndTotalPagesDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.query.WebDataFilterRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.vo.HomePageVO;
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
    private final UserService userService;

    @Autowired
    public HomeController(WebsiteService websiteService, UserService userService) {
        this.websiteService = websiteService;
        this.userService = userService;
    }

    /**
     * Get all usernames of the users
     * and the total number of their public bookmarks sorted by the total number
     *
     * @return all usernames of the users and the total number of their public bookmarks sorted by the total number
     */
    @SystemLog(title = "Filter", optsType = OptsType.READ)
    @GetMapping("/filter")
    public ResultVO<List<UserWithWebCountDTO>> getUsernamesAndCountPublicBookmarks() {
        List<UserWithWebCountDTO> data = userService.getUsernamesAndCountPublicBookmarks();
        return ResultCreator.okResult(data);
    }

    /**
     * Filter bookmarked sites
     *
     * @param filter filter request
     * @return {@link ResultVO}<{@link List}<{@link WebsiteDTO}>> Filtered bookmarked sites
     */
    @PostMapping("/filter")
    public ResultVO<List<WebsiteDTO>> filter(@RequestBody WebDataFilterRequest filter) {

        List<WebsiteDTO> webs = websiteService.findPublicWebDataByFilter(filter);
        return ResultCreator.okResult(webs);
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
        WebDataAndTotalPagesDTO data =
                websiteService.getHomeTimeline(currentUser, timeline, requestedUsername, pageInfo);

        return HomePageVO.builder()
                .currentUser(currentUser)
                .websiteDataInfo(data)
                .requestedUsername(requestedUsername)
                .build();
    }
}