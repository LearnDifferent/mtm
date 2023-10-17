package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.constant.consist.RedisConstant;
import com.github.learndifferent.mtm.mapper.BookmarkViewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Delete data related to views
 *
 * @author zhou
 * @date 2022/3/25
 */
@Component
public class DeleteViewManager {

    private final BookmarkViewMapper bookmarkViewMapper;
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public DeleteViewManager(BookmarkViewMapper bookmarkViewMapper,
                             StringRedisTemplate redisTemplate) {
        this.bookmarkViewMapper = bookmarkViewMapper;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Delete views of a bookmark
     *
     * @param id ID of the bookmark
     */
    public void deleteBookmarkView(long id) {
        boolean success = bookmarkViewMapper.deleteViewData(id);
        if (success) {
            // remove from Redis
            String key = RedisConstant.WEB_VIEW_COUNT_PREFIX + id;
            redisTemplate.delete(key);
            redisTemplate.opsForSet().remove(RedisConstant.VIEW_KEY_SET, key);
        }
    }
}
