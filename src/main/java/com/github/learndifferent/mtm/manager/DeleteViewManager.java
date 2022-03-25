package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import com.github.learndifferent.mtm.mapper.WebDataViewMapper;
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

    private final WebDataViewMapper webDataViewMapper;
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public DeleteViewManager(WebDataViewMapper webDataViewMapper,
                             StringRedisTemplate redisTemplate) {
        this.webDataViewMapper = webDataViewMapper;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Delete the view data of a web page
     *
     * @param webId id of the web page
     */
    public void deleteWebView(int webId) {
        boolean success = webDataViewMapper.deleteViewData(webId);
        if (success) {
            // remove from Redis
            String key = KeyConstant.WEB_VIEW_COUNT_PREFIX + webId;
            redisTemplate.delete(key);
            redisTemplate.opsForSet().remove(KeyConstant.VIEW_KEY_SET, key);
        }
    }
}
