package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 统计该网页的评论数
 *
 * @author zhou
 * @date 2021/9/30
 */
@Component
public class CountCommentManager {

    private final CommentMapper commentMapper;

    @Autowired
    public CountCommentManager(CommentMapper commentMapper) {this.commentMapper = commentMapper;}

    /**
     * Get count of website comments
     *
     * @param webId web id
     * @return count of website comments
     */
    public int countCommentByWebId(Integer webId) {
        Integer count = commentMapper.countCommentByWebId(webId);
        return count == null ? 0 : count;
    }
}
