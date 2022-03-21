package com.github.learndifferent.mtm.mapper;

import com.github.learndifferent.mtm.dto.WebsiteDataFilterDTO;
import com.github.learndifferent.mtm.dto.WebsiteWithCountDTO;
import com.github.learndifferent.mtm.dto.search.WebForSearchDTO;
import com.github.learndifferent.mtm.entity.WebsiteDO;
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
     * 根据用户和日期筛选公开的网页数据
     *
     * @param filter 其中 load 表示加载多少数据
     *               ，usernames 表示选中的用户
     *               ，fromDate 和 toDate 表示筛选的日期
     *               ，order 表示根据哪个字段来排序
     *               ，desc 为 false 或 null 时，不按照 desc 排序
     * @return 符合条件的网页数据
     */
    List<WebsiteDO> findPublicWebDataByFilter(WebsiteDataFilterDTO filter);


    /**
     * 计算单独出现的公开的 url
     *
     * @return int
     */
    int countDistinctPublicUrl();

    /**
     * 展示被收藏最多的公开网页
     *
     * @param from 从
     * @param size 大小
     * @return {@code List<WebsiteWithCountDTO>}
     */
    List<WebsiteWithCountDTO> mostPublicMarkedWebs(@Param("from") int from, @Param("size") int size);

    /**
     * 获取所有用于搜索的公开网页数据
     *
     * @return {@code List<WebForSearchDTO>}
     */
    List<WebForSearchDTO> getAllPublicWebDataForSearch();

    /**
     * 计算所有公开网页数据的条数。指定的用户需要把私有的网页数据个数也计入其中
     *
     * @param specUsername 指定的用户名
     * @return int 数据条数
     */
    int countAllPubAndSpecUserPriWebs(String specUsername);

    /**
     * 计算用户的网页数据的条数（可以选择是否包含私有的网页数据）
     *
     * @param userName       用户名
     * @param includePrivate 是否包含私有的网页数据
     * @return int 数据条数
     */
    int countUserPost(@Param("userName") String userName,
                      @Param("includePrivate") boolean includePrivate);

    /**
     * 计算除去 {@code excludeUsername} 用户名的用户收藏的公开的网页的总数，
     * 如果 {@code excludeUsername} 和 {@code userNameToShowAll} 不相等，
     * 那么用户名为 {@code userNameToShowAll} 的用户的私有的网页数据也要统计
     *
     * @param excludeUsername   除去该用户名的用户
     * @param userNameToShowAll 该用户名的用户需要展示公开和私有的网页数据
     * @return 一共有多少条数据
     */
    int countExcludeUserPost(@Param("excludeUsername") String excludeUsername,
                             @Param("userNameToShowAll") String userNameToShowAll);

    /**
     * 根据用户名，获取网页数据。
     * <p>如果 {@code includePrivate} 参数为 true，会查所有数据；如果为 false，就只查找公开的数据。</p>
     * <p>如果 from 和 size 不为 null，则分页。</p>
     *
     * @param userName       用户名
     * @param from           from
     * @param size           size
     * @param includePrivate 是否包含私有数据
     * @return {@code List<WebsiteDO>}
     */
    List<WebsiteDO> findWebsitesDataByUser(@Param("userName") String userName,
                                           @Param("from") Integer from,
                                           @Param("size") Integer size,
                                           @Param("includePrivate") boolean includePrivate);

    /**
     * 查找除去用户名为 {@code excludeUsername} 的所有公开网页。
     * 其中如果有用户名为 {@code userNameToShowAll} 的网页数据，
     * 那么该用户的公开和私有数据都要展示出来。
     * <p>处理的时候，如果 {@code excludeUsername} 和 {@code userNameToShowAll} 相等，
     * 那么就没必要再把 {@code userNameToShowAll} 用户的数据全部查出来</p>
     *
     * @param excludeUsername   不查找该用户 / 某个用户
     * @param from              from
     * @param size              size
     * @param userNameToShowAll 该用户名的用户的所有数据都要展示
     * @return {@link List}<{@link WebsiteDO}> 除去某个用户的所有网页
     */
    List<WebsiteDO> findWebsitesDataExcludeUser(@Param("excludeUsername") String excludeUsername,
                                                @Param("from") int from,
                                                @Param("size") int size,
                                                @Param("userNameToShowAll") String userNameToShowAll);

    /**
     * 通过 url 找到所有的网页数据，包括私有的）
     *
     * @param url url
     * @return {@code List<WebsiteDO>}
     */
    List<WebsiteDO> findWebsitesDataByUrl(String url);

    /**
     * 获取所有公开网页数据，以及指定用户的公开和私有网页数据数据（desc 排序，分页）。
     * 如果 from 或 size 为 null，表示获取所有。
     *
     * @param from         从
     * @param size         大小
     * @param specUsername 指定的用户名
     * @return {@code List<WebsiteDO>}
     */
    List<WebsiteDO> getAllPubAndSpecUserPriWebs(@Param("from") Integer from,
                                                @Param("size") Integer size,
                                                @Param("specUsername") String specUsername);

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
     * 通过 id 获取网页数据（包括公开和私有）
     *
     * @param webId id
     * @return {@code WebsiteDO}
     */
    WebsiteDO getWebsiteDataById(Integer webId);

    /**
     * 通过 web id 更新网页数据
     *
     * @param websiteDO 网页数据
     * @return 是否成功
     */
    boolean updateWebsiteDataById(WebsiteDO websiteDO);

    /**
     * Get user's name, who owns the website data
     *
     * @param webId website id
     * @return {@link String}
     */
    String getUsernameByWebId(int webId);
}
