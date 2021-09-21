package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.mapper.UserMapper;
import com.github.learndifferent.mtm.mapper.WebsiteMapper;
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

    private final WebsiteMapper websiteMapper;
    private final UserMapper userMapper;

    @Autowired
    public DeleteUserManager(WebsiteMapper websiteMapper, UserMapper userMapper) {
        this.websiteMapper = websiteMapper;
        this.userMapper = userMapper;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean deleteUserAndHisWebsiteData(String username) {
        // 删除该用户收藏的所有网页数据
        websiteMapper.deleteWebsiteDataByUsername(username);
        // 删除该用户
        return userMapper.deleteUserByName(username);
    }
}
