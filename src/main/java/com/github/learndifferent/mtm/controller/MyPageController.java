package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.dto.WebWithPrivacyCommentCountDTO;
import com.github.learndifferent.mtm.entity.UserDO;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.IpUtils;
import com.github.learndifferent.mtm.utils.PageUtil;
import com.github.learndifferent.mtm.vo.MyPageVO;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * My Page Controller
 *
 * @author zhou
 * @date 2021/09/05
 */
@RestController
@RequestMapping("/mypage")
public class MyPageController {

    private final WebsiteService websiteService;
    private final UserService userService;

    @Autowired
    public MyPageController(WebsiteService websiteService, UserService userService) {
        this.websiteService = websiteService;
        this.userService = userService;
    }

    /**
     * Load data
     *
     * @param pageInfo pagination info
     * @param request  request
     * @return {@link ResultVO}<{@link MyPageVO}> data for my page
     */
    @GetMapping
    public ResultVO<MyPageVO> load(@PageInfo PageInfoDTO pageInfo, HttpServletRequest request) {

        String userName = (String) StpUtil.getLoginId();
        UserDTO user = userService.getUserByName(userName);

        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();
        int totalCount = websiteService.countUserPost(userName, true);

        List<WebWithPrivacyCommentCountDTO> myWebs =
                websiteService.getWebsDataAndCommentCountByUser(userName, from, size, true);

        int totalPage = PageUtil.getAllPages(totalCount, size);

        String ip = IpUtils.getIp(request);

        MyPageVO myPageVO = MyPageVO.builder()
                .firstCharOfName(userName.trim().charAt(0))
                .user(user)
                .myWebs(myWebs)
                .totalPage(totalPage)
                .ip(ip)
                .build();

        return ResultCreator.okResult(myPageVO);
    }
}
