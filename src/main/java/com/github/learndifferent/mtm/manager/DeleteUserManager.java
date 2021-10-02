package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.mapper.CommentMapper;
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
    private final CommentMapper commentMapper;

    @Autowired
    public DeleteUserManager(WebsiteMapper websiteMapper,
                             UserMapper userMapper,
                             CommentMapper commentMapper) {
        this.websiteMapper = websiteMapper;
        this.userMapper = userMapper;
        this.commentMapper = commentMapper;
    }

    /**
     * 删除用户的网页数据和评论数据（包括回复）
     *
     * @param username 用户名
     * @return 返回 false 表示删除失败，也就是没有该用户
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean deleteUserAndWebAndCommentData(String username) {
        // 删除该用户收藏的所有网页数据
        websiteMapper.deleteWebsiteDataByUsername(username);
        // 删除用户的评论数据
        commentMapper.deleteCommentsByUsername(username);
        // 删除该用户（false 表示没有该用户）
        return userMapper.deleteUserByName(username);
    }
}
