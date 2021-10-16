package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.SearchResultsDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import java.util.Set;

/**
 * Search and trending searches
 *
 * @author zhou
 * @date 2021/09/21
 */
public interface SearchService {

    /**
     * 是否存在该 Index，如果没有就创建 Index
     *
     * @param indexName name of the index
     * @return 是否存在 Index，没有该 Index 的话返回是否创建成功
     * @throws com.github.learndifferent.mtm.exception.ServiceException 创建 index 的时候有可能会抛出网络异常
     */
    boolean hasIndexOrCreate(String indexName);

    /**
     * 是否存在该 Index
     *
     * @param indexName name of the index
     * @return true 表示存在，false 表示不存在
     */
    boolean existsIndex(String indexName);

    /**
     * 在执行 websiteDataDiffFromDatabase() 方法之前，判断一下 Elasticsearch 中是否存在该 index。
     * <p>如果存在了，再执行。</p>
     * <p>如果不存在该 index，直接返回 true，表示 Elasticsearch 中的数据和数据库中的数据不同</p>
     * <p>也就是说，比较的是数据库中的 distinct url 的数量是否等于 Elasticsearch 中的数据数量</p>
     *
     * @param existIndex Elasticsearch 中是否存在该 index
     * @return true 表示 Elasticsearch 中的数据和数据库中的数据条数不同
     */
    boolean websiteDataDiffFromDatabase(boolean existIndex);

    /**
     * 删除步骤：先检查该 index 是否存在，
     * <p>如果不存在，返回 true 表示已经删除；</p>
     * <p>如果不存在该 index，就执行删除</p>
     *
     * @param indexName name of the index
     * @return 是否删除成功
     */
    boolean checkAndDeleteIndex(String indexName);

    /**
     * 重新生成搜索数据。
     * <p>确保之前的数据已经清空，再根据数据库中的数据生成 Elasticsearch 的数据。</p>
     *
     * @return 是否成功
     */
    boolean generateWebsiteDataForSearch();

    /**
     * 根据关键词搜索网页数据（还要统计关键词的次数来做热搜）
     *
     * @param keyword  关键词
     * @param pageInfo 分页信息
     * @return 结果（搜索结果，总页数，错误信息等）
     * @throws ServiceException 关键词为空的情况， @EmptyStringCheck 注解会抛出无匹配结果异常。
     *                          如果搜索结果为 0，也会抛出无结果异常。
     *                          如果出现网络异常，也会抛出异常。
     */
    SearchResultsDTO searchWebsiteData(String keyword, PageInfoDTO pageInfo);

    /**
     * 获取热搜排行榜
     *
     * @return 按照 score 排序的前 20 个热搜词
     */
    Set<String> getTrends();

    /**
     * 删除热搜词
     *
     * @param word 需要删除的热搜词
     * @return 是否成功
     * @throws ServiceException 如果 word 为空，就抛出异常
     */
    boolean deleteTrendsByWord(String word);

    /**
     * 删除所有热搜词
     *
     * @return 是否成功
     */
    boolean deleteAllTrends();
}