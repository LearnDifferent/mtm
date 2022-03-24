package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import com.github.learndifferent.mtm.service.ViewCounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * View Counter
 *
 * @author zhou
 * @date 2022/3/24
 */
@Service
public class ViewCounterServiceImpl implements ViewCounterService {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public ViewCounterServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void increaseViews(Integer webId) {
        if (webId != null) {
            redisTemplate.opsForValue().increment(KeyConstant.WEB_VIEW_COUNT_PREFIX + webId);
        }
    }

    @Override
    public int countViews(Integer webId) {

        if (webId == null) {
            return 0;
        }

        String views = redisTemplate.opsForValue().get(KeyConstant.WEB_VIEW_COUNT_PREFIX + webId);
        if (views == null) {
            return 0;
        }

        try {
            return Integer.parseInt(views);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
