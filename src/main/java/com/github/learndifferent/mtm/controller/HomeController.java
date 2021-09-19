package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.UserWithWebCountDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.dto.WebsitePatternDTO;
import com.github.learndifferent.mtm.manager.WebsiteManager;
import com.github.learndifferent.mtm.query.WebFilter;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.vo.HomeVO;
import com.github.learndifferent.mtm.vo.WebsByFilterVO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页的 Controller
 *
 * @author zhou
 * @date 2021/09/05
 */
@RestController
@RequestMapping("/home")
public class HomeController {

    private final WebsiteService websiteService;
    private final UserService userService;
    private final WebsiteManager websiteManager;

    @Autowired
    public HomeController(WebsiteService websiteService,
                          UserService userService,
                          WebsiteManager websiteManager) {
        this.websiteService = websiteService;
        this.userService = userService;
        this.websiteManager = websiteManager;
    }

    @SystemLog(title = "Filter", optsType = OptsType.READ)
    @GetMapping("/filter")
    public List<UserWithWebCountDTO> getUsernamesAndCountTheirMarkedWebs() {
        return userService.getNamesAndCountMarkedWebsDesc();
    }

    @SystemLog(title = "Filter", optsType = OptsType.READ)
    @PostMapping("/filter")
    public ResultVO<WebsByFilterVO> getFilterAndReturnResult(@RequestBody WebFilter filter) {

        List<WebsiteDTO> webs = websiteService.findWebsitesDataByFilter(filter);
        int count = webs.size();

        WebsByFilterVO result = WebsByFilterVO.builder()
                .webs(webs).count(count).build();

        return ResultCreator.okResult(result);
    }

    @GetMapping("/load")
    public ResultVO<HomeVO> loadHome(
            @RequestParam(value = "pattern", defaultValue = "recent") String pattern,
            @PageInfo(size = 20) PageInfoDTO pageInfo,
            @RequestParam(value = "userName", required = false) String userName) {

        WebsitePatternDTO info = websiteManager.getWebsiteByPattern(pattern,
                pageInfo, userName);

        String currentUser = getCurrentUser();

        HomeVO homeVO = HomeVO.builder()
                .currentUser(currentUser)
                .websInfo(info)
                .optUsername(userName)
                .build();

        return ResultCreator.okResult(homeVO);
    }

    private String getCurrentUser() {
        Object loginId = StpUtil.getLoginId();
        // 因为这个 Object 肯定不是 null，所以下面可以直接使用 String.valueOf() 方法
        return String.valueOf(loginId);
    }
}