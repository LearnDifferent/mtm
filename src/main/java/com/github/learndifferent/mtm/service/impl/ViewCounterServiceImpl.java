package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.entity.ViewDataDO;
import com.github.learndifferent.mtm.mapper.BookmarkViewMapper;
import com.github.learndifferent.mtm.service.ViewCounterService;
import com.github.learndifferent.mtm.utils.ApplicationContextUtils;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.vo.VisitedBookmarksVO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
public class ViewCounterServiceImpl implements ViewCounterService {

    private final StringRedisTemplate redisTemplate;
    private final BookmarkViewMapper bookmarkViewMapper;

    /**
     * the length of {@link KeyConstant#WEB_VIEW_COUNT_PREFIX}
     */
    private final static int LENGTH_OF_KEY_WEB_VIEW_COUNT_PREFIX = KeyConstant.WEB_VIEW_COUNT_PREFIX.length();

    @Autowired
    public ViewCounterServiceImpl(StringRedisTemplate redisTemplate,
                                  BookmarkViewMapper bookmarkViewMapper) {
        this.redisTemplate = redisTemplate;
        this.bookmarkViewMapper = bookmarkViewMapper;
    }

    @Override
    public void increaseViewsAndAddToSet(Integer webId) {
        if (webId == null) {
            return;
        }
        String key = KeyConstant.WEB_VIEW_COUNT_PREFIX + webId;
        redisTemplate.opsForValue().increment(key);
        // add the key to set
        redisTemplate.opsForSet().add(KeyConstant.VIEW_KEY_SET, key);
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

    private ViewCounterServiceImpl getBean() {
        return ApplicationContextUtils.getBean(ViewCounterServiceImpl.class);
    }

    @Override
    public List<String> updateViewsAndReturnFailKeys() {
        // get all keys containing view data in Redis
        Set<String> keys = redisTemplate.opsForSet().members(KeyConstant.VIEW_KEY_SET);

        // save the view data from database to Redis if no keys are found
        // save them from Redis to database if found
        boolean isEmpty = CollectionUtils.isEmpty(keys);
        return isEmpty ? saveViewsToRedisAndReturnEmptyList()
                : getBean().saveViewsToDbAndReturnFailKeys(keys);
    }

    private List<String> saveViewsToRedisAndReturnEmptyList() {
        // get data from database
        List<ViewDataDO> data = bookmarkViewMapper.getAllViewData();

        // save to Redis
        Map<String, String> kv = data.stream().collect(Collectors.toMap(
                d -> KeyConstant.WEB_VIEW_COUNT_PREFIX + d.getWebId(),
                d -> String.valueOf(d.getViews())));
        redisTemplate.opsForValue().multiSet(kv);

        // add the keys to set in Redis
        Set<String> keySet = kv.keySet();
        int size = keySet.size();
        String[] keys = keySet.toArray(new String[size]);
        redisTemplate.opsForSet().add(KeyConstant.VIEW_KEY_SET, keys);

        return Collections.emptyList();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<String> saveViewsToDbAndReturnFailKeys(Set<String> keys) {

        // clear all data before adding new data
        bookmarkViewMapper.clearAll();

        // keys that failed to save
        List<String> failKeys = new ArrayList<>();

        // get all values by keys and create a new set to store every web id and its views
        Set<ViewDataDO> set = new HashSet<>();
        keys.forEach(key -> updateViewsCollections(set, failKeys, key));

        // save all data to database
        bookmarkViewMapper.addAll(set);

        // return the list of the keys that failed to save
        return failKeys;
    }

    private void updateViewsCollections(Set<ViewDataDO> set, List<String> failKeys, String key) {
        String val = redisTemplate.opsForValue().get(key);
        if (val == null) {
            // add the key to fail list if no value available
            failKeys.add(key);
            return;
        }

        try {
            updateViewsCollections(set, key, val);
        } catch (Exception e) {
            e.printStackTrace();
            // add the key to list if failure
            failKeys.add(key);
        }
    }

    private void updateViewsCollections(Set<ViewDataDO> set, String key, String val) {
        // get views
        int views = Integer.parseInt(val);
        // get web id
        String webIdString = key.substring(LENGTH_OF_KEY_WEB_VIEW_COUNT_PREFIX);
        int webId = Integer.parseInt(webIdString);
        // create data
        ViewDataDO data = ViewDataDO.builder().views(views).webId(webId).build();
        // add data to set
        set.add(data);
    }

    @Override
    @Scheduled(fixedRate = 43_200_000)
    public void updateViewsScheduledTask() {
        getBean().updateViewsAndReturnFailKeys();
    }

    @Override
    @Cacheable(value = "bookmarks:visited", unless = "#result != null and #result.size() > 0")
    public List<VisitedBookmarksVO> getVisitedBookmarks(PageInfoDTO pageInfo) {
        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();
        List<VisitedBookmarksVO> data = bookmarkViewMapper.getVisitedBookmarks(from, size);
        return DozerUtils.convertList(data, VisitedBookmarksVO.class);
    }
}