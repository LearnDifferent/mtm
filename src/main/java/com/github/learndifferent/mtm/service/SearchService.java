package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.constant.enums.SearchMode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.search.SearchResultsDTO;
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
     * 是否存在该数据
     *
     * @param mode Check user data if {@link SearchMode#USER} and check website data if {@link SearchMode#WEB}
     * @return true 表示存在，false 表示不存在
     */
    boolean existsData(SearchMode mode);

    /**
     * Check if data in database is different from data in Elasticsearch.
     *
     * @param mode       Check user data if {@link SearchMode#USER} and check website data if {@link SearchMode#WEB}
     * @param existIndex Index exists or not
     * @return Returns true if detect a difference.
     * <p>If the index does not exist, returns true. If the {@link SearchMode} is not {@link SearchMode#USER} or
     * {@link SearchMode#WEB}, or it's null, returns false.</p>
     */
    boolean dataInDatabaseDiffFromElasticsearch(SearchMode mode, boolean existIndex);

    /**
     * 删除步骤：先检查该 index 是否存在，
     * <p>如果不存在，返回 true 表示已经删除；</p>
     * <p>如果不存在该 index，就执行删除</p>
     *
     * @param mode Delete user data if {@link SearchMode#USER} and delete website data if {@link SearchMode#WEB}
     * @return 是否删除成功
     * @throws com.github.learndifferent.mtm.exception.ServiceException Only admin can delete all website data, and
     *                                                                  if the current user is not admin, {@link
     *                                                                  com.github.learndifferent.mtm.annotation.validation.user.role.admin.AdminValidation}
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     */
    boolean checkAndDeleteIndex(SearchMode mode);

    /**
     * Data generation for Elasticsearch based on database
     *
     * @param mode If the value is {@link SearchMode#USER}, generate user data.
     *             If the value is {@link SearchMode#WEB}, generate website data.
     *             The default mode is {@link SearchMode#WEB}.
     * @return success or failure
     */
    boolean generateDataForSearch(SearchMode mode);

    /**
     * 根据关键词搜索数据（还要统计关键词的次数来做热搜）
     *
     * @param mode     Search user data if {@link SearchMode#USER} and search website data if {@link SearchMode#WEB}
     * @param keyword  关键词
     * @param pageInfo 分页信息
     * @return 结果（搜索结果，总页数，错误信息等）
     * @throws ServiceException 关键词为空的情况， @EmptyStringCheck 注解会抛出无匹配结果异常。
     *                          如果搜索结果为 0，也会抛出无结果异常。
     *                          如果出现网络异常，也会抛出异常。
     */
    SearchResultsDTO search(SearchMode mode, String keyword, PageInfoDTO pageInfo);

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