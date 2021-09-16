package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.dto.WebForSearchDTO;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.dto.WebsiteWithCountDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.query.WebFilter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * WebsiteService
 *
 * @author zhou
 * @date 2021/09/05
 */
public interface WebsiteService {

    /**
     * 根据 WebFilter 筛选器来筛选网页数据
     *
     * @param filter 筛选器，筛选器包含：
     *               需要加载多少条数据，
     *               所有用户名（没有用户名的时候表示查找所有），
     *               日期（可以是一个或两个，没有的时候表示查找所有）
     * @return 筛选出来的网页
     */
    List<WebsiteDTO> findWebsitesDataByFilter(WebFilter filter);

    /**
     * 获取最多用户收藏的 URL 及其网页信息
     *
     * @param from 分页起始
     * @param size 页面大小
     * @return 最多用户收藏的网页及其信息
     */
    List<WebsiteWithCountDTO> showMostMarked(@Param("from") int from,
                                             @Param("size") int size);

    /**
     * 计算所有数据
     *
     * @return 一共有多少条数据
     */
    int countAll();

    /**
     * 计算 URL 出现的次数，并剔除重复
     *
     * @return 一共有多少条数据
     */
    int countDistinctUrl();

    /**
     * 倒序查询所有网页（分页）
     *
     * @param from 起始
     * @param size 页面大小
     * @return 查询到的网页
     */
    List<WebsiteDTO> showAllWebsiteDataDesc(int from, int size);

    /**
     * 计算某个用户收藏的网页的总数
     *
     * @param userName 某个用户
     * @return 一共有多少条数据
     */
    int countUserPost(String userName);

    /**
     * 计算除去某个用户收藏的网页的总数
     *
     * @param userName 除去某个用户
     * @return 一共有多少条数据
     */
    int countExcludeUserPost(String userName);

    /**
     * 查找除去某个用户的所有网页
     *
     * @param userName 不查找该用户 / 某个用户
     * @param from     from
     * @param size     size
     * @return 除去某个用户的所有网页
     */
    List<WebsiteDTO> findWebsitesDataExcludeUser(@Param("userName") String userName,
                                                 @Param("from") int from,
                                                 @Param("size") int size);

    /**
     * 查找某个用户的收藏
     *
     * @param userName 某个用户
     * @param from     from
     * @param size     size
     * @return 某个用户的所有收藏
     */
    List<WebsiteDTO> findWebsitesDataByUser(@Param("userName") String userName,
                                            @Param("from") Integer from,
                                            @Param("size") Integer size);

    /**
     * 根据用户获取该用户的所有网页数据
     *
     * @param userName 用户名
     * @return {@code List<WebsiteDO>}
     */
    List<WebsiteDTO> findWebsitesDataByUser(String userName);

    /**
     * 通过id找到网页数据
     *
     * @param webId id
     * @return {@code WebsiteDTO}
     */
    WebsiteDTO findWebsiteDataById(int webId);

    /**
     * 保存没有 ID、用户名和创建时间的网页数据，并添加用户信息，生成时间（ID 会在数据库中生成）。
     * <p>如果已经收藏过了，就不能收藏第二次，@UserAlreadyMarkedCheck 注解会抛出异常。
     * 如果传入的用户名不是当前用户名，也会抛出异常。</p>
     * <p>还会使用 @WebsiteDataClean 注解会清理 Website 数据中 title 和 desc 的长度，
     * 判断 url 格式是否正确，以及清理 url</p>
     *
     * @param rawWebsite 没有 ID、用户名和创建时间的网页数据
     * @param userName   保存该网页的用户
     * @return 是否成功
     * @throws ServiceException 如果已经收藏了，会抛出 ServiceException，
     *                          代码为：ResultCode.ALREADY_MARKED；
     *                          如果传入的用户名不是当前的用户名，也会抛出这个异常，
     *                          代码为：ResultCode.PERMISSION_DENIED；
     *                          如果 URL 格式出错，也会抛出这个异常，
     *                          代码为：ResultCode.URL_MALFORMED
     */
    boolean saveWebsiteData(WebWithNoIdentityDTO rawWebsite, String userName);

    /**
     * 根据链接，获取网页的 title、url、img 和简介数据。
     * 如果该用户已经收藏过该链接，@IfMarkedThenReturn 注解就抛出异常。
     *
     * @param url      网页链接
     * @param userName 收藏该网页的用户
     * @return 网页信息
     * @throws ServiceException 已经收藏过的异常
     */
    WebWithNoIdentityDTO scrapeWebsiteDataFromUrl(String url, String userName);

    /**
     * 获取所有用于 Elasticsearch 的数据
     *
     * @return 用于 Elasticsearch 的数据
     */
    List<WebForSearchDTO> getAllWebsitesDataForSearch();

    /**
     * 通过 ID 更新网页
     *
     * @param websiteDO 新的网页数据
     * @return 是否成功
     */
    boolean updateWebsiteDataById(WebsiteDTO websiteDO);

    /**
     * 通过url找到网页数据
     *
     * @param url url
     * @return {@code List<WebsiteDTO>}
     */
    List<WebsiteDTO> findWebsitesDataByUrl(String url);

    /**
     * 通过id删除网页数据
     *
     * @param webId id
     * @return boolean
     */
    boolean delWebsiteDataById(int webId);

    /**
     * 删除该用户名的用户所收藏的所有网站数据
     *
     * @param userName 用户名
     */
    void deleteWebsiteDataByUsername(String userName);
}
