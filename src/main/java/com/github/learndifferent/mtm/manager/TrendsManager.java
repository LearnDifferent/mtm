package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck.ExceptionIfEmpty;
import com.github.learndifferent.mtm.constant.consist.EsConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 操作排行（在 redis 中）
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
     * 获取热搜排行榜
     *
     * @return 按照 score 排序的前 20 个热搜词
     */
    public Set<String> getTrends() {

        return redisTemplate.opsForZSet().reverseRangeByScore(EsConstant.TRENDING, 0, 10);
    }

    /**
     * 删除热搜词
     *
     * @param word 需要删除的热搜词
     * @return 是否成功
     * @throws ServiceException 如果 word 为空，就抛出异常
     */
    @EmptyStringCheck
    public boolean deleteTrendsByWord(
            @ExceptionIfEmpty(errorMessage = "关键词为空，无法删除") String word) {

        Long success = redisTemplate.opsForZSet().remove(EsConstant.TRENDING, word);

        return success != null && success != 0;
    }

    /**
     * 删除所有热搜词
     *
     * @return 是否成功
     */
    public boolean deleteAllTrends() {
        Boolean deleted = redisTemplate.delete(EsConstant.TRENDING);
        return deleted != null ? deleted : false;
    }

    /**
     * 将 word 加入 Trending List 中（Redis 的 zset，出现一次加 1 个 score）
     *
     * @param word 需要加入的词（因为统计的是字节数大于 1 的关键词，所以不会是空或 null）
     */
    @EmptyStringCheck
    public void addToTrendingList(
            @ExceptionIfEmpty(resultCode = ResultCode.NO_RESULTS_FOUND) String word) {

        redisTemplate.opsForZSet().incrementScore(EsConstant.TRENDING, word, 1);
    }
}
