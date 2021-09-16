package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.entity.UserDO;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.IpUtils;
import com.github.learndifferent.mtm.utils.PageUtil;
import com.github.learndifferent.mtm.vo.MyPageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 我的页面的 Controller
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
    public MyPageController(WebsiteService websiteService,
                            UserService userService) {
        this.websiteService = websiteService;
        this.userService = userService;
    }

    @GetMapping
    public ResultVO<MyPageVO> load(@PageInfo PageInfoDTO pageInfo,
                                   HttpServletRequest request) {

        String userName = (String) StpUtil.getLoginId();
        UserDTO user = getUser(userName);

        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();
        int totalCount = websiteService.countUserPost(userName);

        List<WebsiteDTO> myWebs = websiteService.findWebsitesDataByUser(
                userName, from, size);

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

    private UserDTO getUser(String userName) {
        UserDO userDO = userService.getUserByName(userName);

        return DozerUtils.convert(userDO, UserDTO.class);
    }
}
