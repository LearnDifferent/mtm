package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.service.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用于删除用户
 *
 * @author zhou
 * @date 2021/09/16
 */
@Component
public class DeleteUserManager {

    private final WebsiteService websiteService;
    private final UserService userService;

    @Autowired
    public DeleteUserManager(WebsiteService websiteService, UserService userService) {
        this.websiteService = websiteService;
        this.userService = userService;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean deleteUserAndHisWebsiteData(String username) {
        // 删除该用户收藏的所有网页数据
        websiteService.deleteWebsiteDataByUsername(username);
        // 删除该用户
        return userService.deleteUserByName(username);
    }
}
