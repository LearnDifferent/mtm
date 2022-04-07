package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.service.NotificationService;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.utils.IpUtils;
import com.github.learndifferent.mtm.vo.PersonalInfoVO;
import com.github.learndifferent.mtm.vo.UserVO;
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
@RequestMapping("/my-page")
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

        String username = getCurrentUsername();
        UserVO user = userService.getUserByName(username);
        String ip = IpUtils.getIp(request);
        long totalReplyNotifications = notificationService.countReplyNotifications(username);

        return PersonalInfoVO.builder()
                .user(user)
                .ip(ip)
                .totalReplyNotifications(totalReplyNotifications)
                .build();
    }

    private String getCurrentUsername() {
        return StpUtil.getLoginIdAsString();
    }
}