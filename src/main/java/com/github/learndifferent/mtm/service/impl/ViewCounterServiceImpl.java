package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.constant.consist.RedisConstant;
import com.github.learndifferent.mtm.entity.ViewDataDO;
import com.github.learndifferent.mtm.mapper.BookmarkViewMapper;
import com.github.learndifferent.mtm.service.ViewCounterService;
import com.github.learndifferent.mtm.utils.ApplicationContextUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * View Counter Implementation
 *
 * @author zhou
 * @date 2022/3/24
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ViewCounterServiceImpl implements ViewCounterService {

    private final StringRedisTemplate redisTemplate;
    private final BookmarkViewMapper bookmarkViewMapper;

    /**
     * the length of {@link RedisConstant#WEB_VIEW_COUNT_PREFIX}
     */
    private final static int LENGTH_OF_KEY_WEB_VIEW_COUNT_PREFIX = RedisConstant.WEB_VIEW_COUNT_PREFIX.length();

    @Override
    public void increaseViewsAndAddToSet(Integer bookmarkId) {
        if (Objects.isNull(bookmarkId)) {
            return;
        }
        String key = RedisConstant.WEB_VIEW_COUNT_PREFIX + bookmarkId;
        this.redisTemplate.opsForValue().increment(key);
        // add the key to set
        this.redisTemplate.opsForSet().add(RedisConstant.VIEW_KEY_SET, key);
    }

    @Override
    public int countViews(Integer bookmarkId) {

        if (Objects.isNull(bookmarkId)) {
            return 0;
        }

        String views = this.redisTemplate.opsForValue().get(RedisConstant.WEB_VIEW_COUNT_PREFIX + bookmarkId);
        if (Objects.isNull(views)) {
            return 0;
        }

        try {
            return Integer.parseInt(views);
        } catch (NumberFormatException e) {
            log.error("Cannot convert {} to integer", views, e);
            return 0;
        }
    }

    private ViewCounterServiceImpl getCurrentBean() {
        return ApplicationContextUtils.getBean(ViewCounterServiceImpl.class);
    }

    @Override
    public List<String> updateViewsAndReturnFailKeys() {
        // get all keys containing view data in Redis
        Set<String> keys = this.redisTemplate.opsForSet().members(RedisConstant.VIEW_KEY_SET);

        // save the view data from database to Redis if no keys are found
        // save them from Redis to database if found
        boolean isEmpty = CollectionUtils.isEmpty(keys);
        return isEmpty ? this.saveViewsToRedisAndReturnEmptyList()
                : this.getCurrentBean().saveViewsToDbAndReturnFailKeys(keys);
    }

    private List<String> saveViewsToRedisAndReturnEmptyList() {
        // get data from database
        List<ViewDataDO> data = this.bookmarkViewMapper.getAllViewData();

        // save to Redis
        Map<String, String> map = data.stream().collect(Collectors.toMap(
                d -> RedisConstant.WEB_VIEW_COUNT_PREFIX + d.getBookmarkId(),
                d -> String.valueOf(d.getViews())));
        if (MapUtils.isEmpty(map)) {
            log.info("No views data");
            return Collections.emptyList();
        }

        log.info("Saving views data to Redis: {}", map);

        this.redisTemplate.opsForValue().multiSet(map);

        // add the keys to set in Redis
        Set<String> keySet = map.keySet();
        int size = keySet.size();
        String[] keys = keySet.toArray(new String[size]);
        this.redisTemplate.opsForSet().add(RedisConstant.VIEW_KEY_SET, keys);

        return Collections.emptyList();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CacheEvict(value = "bookmarks:visited", allEntries = true)
    public List<String> saveViewsToDbAndReturnFailKeys(Set<String> keys) {

        // clear all data before adding new data
        this.bookmarkViewMapper.clearAll();

        // keys that failed to save
        List<String> failKeys = new ArrayList<>();

        // get all values by keys and create a new set to store every bookmark id and its views
        Set<ViewDataDO> set = new HashSet<>();
        keys.forEach(key -> updateViewsCollections(set, failKeys, key));

        // save all data to database
        this.bookmarkViewMapper.addAll(set);

        // return the list of the keys that failed to save
        return failKeys;
    }

    private void updateViewsCollections(Set<ViewDataDO> set, List<String> failKeys, String key) {
        String val = this.redisTemplate.opsForValue().get(key);
        if (Objects.isNull(val)) {
            // add the key to fail list if no value available
            failKeys.add(key);
            return;
        }

        try {
            this.updateViewsCollections(set, key, val);
        } catch (Exception e) {
            log.error("Cannot update views for key: {}", key, e);
            // add the key to list if failure
            failKeys.add(key);
        }
    }

    private void updateViewsCollections(Set<ViewDataDO> set, String key, String val) {
        // get views
        int views = Integer.parseInt(val);
        // get bookmark id
        String bookmarkIdStr = key.substring(LENGTH_OF_KEY_WEB_VIEW_COUNT_PREFIX);
        int bookmarkId = Integer.parseInt(bookmarkIdStr);
        // create data
        ViewDataDO data = ViewDataDO.builder().views(views).bookmarkId(bookmarkId).build();
        // add data to set
        set.add(data);
    }

    @Override
    @Scheduled(fixedRate = 43_200_000)
    public void updateViewsScheduledTask() {
        this.getCurrentBean().updateViewsAndReturnFailKeys();
    }
}