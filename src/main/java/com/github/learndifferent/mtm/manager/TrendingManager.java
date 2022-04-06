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
 * Trending Manager
 *
 * @author zhou
 * @date 2021/09/05
 */
@Component
public class TrendingManager {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public TrendingManager(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Get top 20 trending keywords
     *
     * @return Top 20 trending keywords
     */
    public Set<String> getTop20Trending() {

        return redisTemplate.opsForZSet().reverseRange(EsConstant.TRENDING, 0, 19);
    }

    /**
     * Delete a specific trending keyword
     *
     * @param word Trending Word to Delete
     * @return true if success
     * @throws ServiceException If the {@code word} is empty, throw an exception
     */
    @EmptyStringCheck
    public boolean deleteTrendingWord(
            @ExceptionIfEmpty(errorMessage = "Please choose a word to delete") String word) {

        Long success = redisTemplate.opsForZSet().remove(EsConstant.TRENDING, word);

        return success != null && success != 0;
    }

    /**
     * Delete all trending keywords
     *
     * @return true if success
     */
    public boolean deleteTrending() {
        Boolean success = redisTemplate.delete(EsConstant.TRENDING);
        return Optional.ofNullable(success).orElse(false);
    }

    /**
     * Put the {@code word} in trending list and increment the score of it
     *
     * @param word the word to put in trending list
     */
    @EmptyStringCheck
    public void addToTrendingList(
            @ExceptionIfEmpty(resultCode = ResultCode.NO_RESULTS_FOUND) String word) {

        redisTemplate.opsForZSet().incrementScore(EsConstant.TRENDING, word, 1);
    }
}