package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.constant.enums.ShowPattern;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.UserWithWebCountDTO;
import com.github.learndifferent.mtm.dto.WebDataAndTotalPagesDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.query.WebFilterRequest;
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
     * Get users' name and the number of their marked public websites
     *
     * @return {@link List}<{@link UserWithWebCountDTO}> Users' name and the amount of their websites
     */
    @SystemLog(title = "Filter", optsType = OptsType.READ)
    @GetMapping("/filter")
    public List<UserWithWebCountDTO> getNamesAndCountTheirPublicWebs() {
        return userService.getNamesAndCountTheirPublicWebs();
    }

    /**
     * Filter website data
     *
     * @param filter filter request
     * @return {@link ResultVO}<{@link List}<{@link WebsiteDTO}>> Filtered Paginated Website Data
     */
    @SystemLog(title = "Filter", optsType = OptsType.READ)
    @PostMapping("/filter")
    public ResultVO<List<WebsiteDTO>> filter(@RequestBody WebFilterRequest filter) {

        List<WebsiteDTO> webs = websiteService.findPublicWebDataByFilter(filter);
        return ResultCreator.okResult(webs);
    }

    /**
     * Load data
     *
     * @param pattern  website data presentation pattern
     * @param userName username
     * @param pageInfo pagination info
     * @return {@link HomePageVO} Data
     */
    @GetMapping("/load")
    public HomePageVO getHomePageData(
            @RequestParam("pattern") ShowPattern pattern,
            @RequestParam(value = "userName", required = false) String userName,
            @PageInfo(size = 7) PageInfoDTO pageInfo) {

        WebDataAndTotalPagesDTO websiteDataInfo = websiteService.getWebsitesByPattern(pattern,
                userName, pageInfo);

        String currentUser = (String) StpUtil.getLoginId();

        return HomePageVO.builder()
                .currentUser(currentUser)
                .websiteDataInfo(websiteDataInfo)
                .optUsername(userName)
                .build();
    }
}