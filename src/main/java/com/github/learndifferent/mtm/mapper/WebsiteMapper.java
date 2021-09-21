package com.github.learndifferent.mtm.mapper;

import com.github.learndifferent.mtm.dto.WebForSearchDTO;
import com.github.learndifferent.mtm.dto.WebsiteWithCountDTO;
import com.github.learndifferent.mtm.entity.WebsiteDO;
import com.github.learndifferent.mtm.query.WebFilterRequest;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * WebsiteMapper
 *
 * @author zhou
 * @date 2021/09/05
 */
@Repository
public interface WebsiteMapper {

    /**
     * 根据用户和日期筛选网页数据
     *
     * @param filter 其中 load 表示加载多少数据
     *               ，usernames 表示选中的用户
     *               ，fromDate 和 toDate 表示筛选的日期
     *               ，order 表示根据哪个字段来排序
     *               ，desc 为 false 或 null 时，不按照 desc 排序
     * @return 符合条件的网页数据
     */
    List<WebsiteDO> findWebsitesDataByFilter(WebFilterRequest filter);


    /**
     * 计算单独出现的url
     *
     * @return int
     */
    int countDistinctUrl();

    /**
     * 展示被收藏最多的网页
     *
     * @param from 从
     * @param size 大小
     * @return {@code List<WebsiteWithCountDTO>}
     */
    List<WebsiteWithCountDTO> showMostMarked(@Param("from") int from,
                                             @Param("size") int size);

    /**
     * 获取所有用于搜索的网页数据
     *
     * @return {@code List<WebForSearchDTO>}
     */
    List<WebForSearchDTO> getAllWebsitesDataForSearch();

    /**
     * 计算所有网页数据的条数
     *
     * @return int
     */
    int countAll();

    /**
     * 计算用户的网页数据的条数
     *
     * @param userName 用户名
     * @return int
     */
    int countUserPost(String userName);

    /**
     * 计算所有除了该用户的网页数据的数量
     *
     * @param userName 用户名
     * @return int
     */
    int countExcludeUserPost(String userName);

    /**
     * 根据用户得到的网页数据
     * <p>如果 from 和 size 不为 null，则分页。</p>
     *
     * @param userName 用户名
     * @param from     from
     * @param size     size
     * @return {@code List<WebsiteDO>}
     */
    List<WebsiteDO> findWebsitesDataByUser(@Param("userName") String userName,
                                           @Param("from") Integer from,
                                           @Param("size") Integer size);

    /**
     * 排除某用户获取的网页数据（分页）
     *
     * @param userName 用户名
     * @param from     从
     * @param size     大小
     * @return {@code List<WebsiteDO>}
     */
    List<WebsiteDO> findWebsitesDataExcludeUser(@Param("userName") String userName,
                                                @Param("from") int from,
                                                @Param("size") int size);

    /**
     * 通过url找到所有网页数据
     *
     * @param url url
     * @return {@code List<WebsiteDO>}
     */
    List<WebsiteDO> findWebsitesDataByUrl(String url);

    /**
     * 显示所有网页数据（desc 排序）
     *
     * @param from 从
     * @param size 大小
     * @return {@code List<WebsiteDO>}
     */
    List<WebsiteDO> showAllWebsiteDataDesc(@Param("from") int from,
                                           @Param("size") int size);

    /**
     * 通过 id 删除网页
     *
     * @param webId id
     * @return boolean
     */
    boolean deleteWebsiteDataById(Integer webId);

    /**
     * 删除该用户名的用户所收藏的所有网站数据
     *
     * @param userName 用户名
     * @return boolean
     */
    void deleteWebsiteDataByUsername(String userName);

    /**
     * 添加网页数据
     *
     * @param websiteDO 网页数据
     * @return boolean
     */
    boolean addWebsiteData(WebsiteDO websiteDO);

    /**
     * 通过id获取网页数据
     *
     * @param webId id
     * @return {@code WebsiteDO}
     */
    WebsiteDO getWebsiteDataById(Integer webId);

    /**
     * 通过id更新网页数据
     *
     * @param websiteDO 网页数据
     * @return boolean
     */
    boolean updateWebsiteDataById(WebsiteDO websiteDO);

    /**
     * 显示所有网页数据
     *
     * @return {@code List<WebsiteDO>}
     */
    List<WebsiteDO> showAllWebsitesData();
}
