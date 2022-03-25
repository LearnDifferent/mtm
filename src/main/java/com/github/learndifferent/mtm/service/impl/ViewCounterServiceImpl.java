package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.VisitedBookmarksDTO;
import com.github.learndifferent.mtm.entity.WebDataViewDO;
import com.github.learndifferent.mtm.mapper.WebDataViewMapper;
import com.github.learndifferent.mtm.service.ViewCounterService;
import com.github.learndifferent.mtm.utils.ApplicationContextUtils;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * View Counter
 *
 * @author zhou
 * @date 2022/3/24
 */
@Service
public class ViewCounterServiceImpl implements ViewCounterService {

    private final StringRedisTemplate redisTemplate;
    private final WebDataViewMapper webDataViewMapper;

    /**
     * the length of {@link KeyConstant#WEB_VIEW_COUNT_PREFIX}
     */
    private final static int LENGTH_OF_KEY_WEB_VIEW_COUNT_PREFIX = KeyConstant.WEB_VIEW_COUNT_PREFIX.length();

    @Autowired
    public ViewCounterServiceImpl(StringRedisTemplate redisTemplate,
                                  WebDataViewMapper webDataViewMapper) {
        this.redisTemplate = redisTemplate;
        this.webDataViewMapper = webDataViewMapper;
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

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<String> saveViewsToDbAndReturnFailKeys() {
        // get all keys
        Set<String> keys = redisTemplate.opsForSet().members(KeyConstant.VIEW_KEY_SET);

        // throw an exception if no keys are found
        boolean empty = CollectionUtils.isEmpty(keys);
        ThrowExceptionUtils.throwIfTrue(empty, ResultCode.UPDATE_FAILED);

        // clear all data before adding new data
        webDataViewMapper.clearAll();

        // keys that failed to save
        List<String> failKeys = new ArrayList<>();

        // get all values by keys and create a new set to store every web id and its views
        Set<WebDataViewDO> set = new HashSet<>();
        keys.forEach(key -> updateViewsCollections(set, failKeys, key));

        // save all data to database
        webDataViewMapper.addAll(set);

        // return the list of the keys that failed to save
        return failKeys;
    }

    private void updateViewsCollections(Set<WebDataViewDO> set, List<String> failKeys, String key) {
        String val = redisTemplate.opsForValue().get(key);
        if (val == null) {
            // add the key to list if no value available
            failKeys.add(key);
            return;
        }

        try {
            // get views
            int views = Integer.parseInt(val);
            // get web id
            String webIdString = key.substring(LENGTH_OF_KEY_WEB_VIEW_COUNT_PREFIX);
            int webId = Integer.parseInt(webIdString);
            // create data
            WebDataViewDO data = WebDataViewDO.builder().views(views).webId(webId).build();
            // add data to set
            set.add(data);
        } catch (Exception e) {
            e.printStackTrace();
            // add the key to list if failure
            failKeys.add(key);
        }
    }

    @Override
    @Scheduled(fixedRate = 43_200_000)
    public void saveViewsToDatabaseScheduledTask() {
        ViewCounterServiceImpl viewCounterService =
                ApplicationContextUtils.getBean(ViewCounterServiceImpl.class);
        viewCounterService.saveViewsToDbAndReturnFailKeys();
    }

    @Override
    public List<VisitedBookmarksDTO> getAllVisitedBookmarks() {
        List<VisitedBookmarksDTO> data = webDataViewMapper.getAllVisitedWebData();
        return DozerUtils.convertList(data, VisitedBookmarksDTO.class);
    }
}
