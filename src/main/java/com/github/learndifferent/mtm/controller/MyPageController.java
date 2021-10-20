package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.dto.WebWithPrivacyCommentCountDTO;
import com.github.learndifferent.mtm.service.NotificationService;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.utils.IpUtils;
import com.github.learndifferent.mtm.utils.PageUtil;
import com.github.learndifferent.mtm.vo.MyWebsiteDataVO;
import com.github.learndifferent.mtm.vo.PersonalInfoVO;
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
    private final NotificationService notificationService;

    @Autowired
    public MyPageController(WebsiteService websiteService,
                            UserService userService,
                            NotificationService notificationService) {
        this.websiteService = websiteService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    /**
     * Get personal information
     *
     * @param request Request
     * @return {@link PersonalInfoVO} Personal information
     */
    @GetMapping
    public PersonalInfoVO getPersonalInfo(HttpServletRequest request) {

        String userName = (String) StpUtil.getLoginId();
        UserDTO user = userService.getUserByName(userName);
        String ip = IpUtils.getIp(request);
        long totalReplyNotifications = notificationService.countReplyNotifications(userName);

        return PersonalInfoVO.builder()
                .user(user)
                .ip(ip)
                .totalReplyNotifications(totalReplyNotifications)
                .build();
    }

    /**
     * Get my website data
     *
     * @param pageInfo Pagination info
     * @return {@link MyWebsiteDataVO} My paginated website data and total pages
     */
    @GetMapping("/webs")
    public MyWebsiteDataVO getMyWebsiteDataInfo(@PageInfo PageInfoDTO pageInfo) {
        String userName = (String) StpUtil.getLoginId();

        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();

        int totalCount = websiteService.countUserPost(userName, true);
        int totalPages = PageUtil.getAllPages(totalCount, size);

        List<WebWithPrivacyCommentCountDTO> myWebsiteData =
                websiteService.getWebsDataAndCommentCountByUser(userName, from, size, true);

        return MyWebsiteDataVO.builder().myWebsiteData(myWebsiteData).totalPages(totalPages).build();
    }
}
