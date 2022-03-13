package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck.ExceptionIfEmpty;
import com.github.learndifferent.mtm.constant.consist.EsConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.exception.ServiceException;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Trending Words Manager
 *
 * @author zhou
 * @date 2021/09/05
 */
@Component
public class TrendsManager {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public TrendsManager(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Get Top 20 Trends
     *
     * @return Top 20 Trends
     */
    public Set<String> getTrends() {

        return redisTemplate.opsForZSet().reverseRange(EsConstant.TRENDING, 0, 19);
    }

    /**
     * Delete a Trending Words
     *
     * @param word Trending Word to Delete
     * @return true if success
     * @throws ServiceException If the {@code word} is empty, throw an exception
     */
    @EmptyStringCheck
    public boolean deleteTrendsByWord(
            @ExceptionIfEmpty(errorMessage = "Please choose a word to delete") String word) {

        Long success = redisTemplate.opsForZSet().remove(EsConstant.TRENDING, word);

        return success != null && success != 0;
    }

    /**
     * Delete All Trending Words
     *
     * @return true if success
     */
    public boolean deleteAllTrends() {
        Boolean success = redisTemplate.delete(EsConstant.TRENDING);
        return Optional.ofNullable(success).orElse(false);
    }

    /**
     * Put the {@code word} in Trending List and increment the score of it
     *
     * @param word the word to put in Trending List
     */
    @EmptyStringCheck
    public void addToTrendingList(
            @ExceptionIfEmpty(resultCode = ResultCode.NO_RESULTS_FOUND) String word) {

        redisTemplate.opsForZSet().incrementScore(EsConstant.TRENDING, word, 1);
    }
}
