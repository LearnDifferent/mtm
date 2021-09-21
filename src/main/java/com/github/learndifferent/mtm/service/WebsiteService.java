package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.dto.WebsitePatternDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.query.SaveNewWebDataRequest;
import com.github.learndifferent.mtm.query.WebFilterRequest;
import com.github.learndifferent.mtm.response.ResultVO;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * WebsiteService
 *
 * @author zhou
 * @date 2021/09/05
 */
public interface WebsiteService {

    /**
     * 根据 WebFilterRequest 筛选器来筛选网页数据
     *
     * @param filter 筛选器，筛选器包含：
     *               需要加载多少条数据，
     *               所有用户名（没有用户名的时候表示查找所有），
     *               日期（可以是一个或两个，没有的时候表示查找所有）
     * @return 筛选出来的网页
     */
    List<WebsiteDTO> findWebsitesDataByFilter(WebFilterRequest filter);

    /**
     * 计算某个用户收藏的网页的总数
     *
     * @param userName 某个用户
     * @return 一共有多少条数据
     */
    int countUserPost(String userName);

    /**
     * 查找某个用户的收藏
     *
     * @param userName 某个用户
     * @param from     from
     * @param size     size
     * @return 某个用户的所有收藏
     */
    List<WebsiteDTO> findWebsitesDataByUser(String userName, Integer from, Integer size);

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
     * 根据 URL 和用户名，收藏新的网页数据
     *
     * @param newWebsiteData URL、用户名，以及是否同步数据到 Elasticsearch
     * @return {@code boolean[]} boolean 数组 index 为 0 的位置表示是否存放到数据库中，
     * boolean 数组 index 为 1 的位置表示是否存放到 Elasticsearch 中。
     */
    boolean[] saveNewWebsiteData(SaveNewWebDataRequest newWebsiteData);

    /**
     * 获取该 pattern 下，分页后需要的网页数据和总页数
     *
     * @param pattern  默认模式为查询所有，如果有指定的模式，就按照指定模式获取
     * @param username 如果需要用户名，就传入用户名；如果不需要，就传入空字符串；
     * @param pageInfo 分页相关信息
     * @return 该 pattern 下，分页后需要的网页数据和总页数
     */
    WebsitePatternDTO getWebsitesByPattern(String pattern, String username, PageInfoDTO pageInfo);

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
     * 以 HTML 格式，导出该用户的所有网页数据。如果该用户没有数据，直接输出无数据的提示。
     *
     * @param username 用户名
     * @param response response
     * @throws ServiceException ResultCode.CONNECTION_ERROR
     */
    void exportWebsDataByUserToHtmlFile(String username, HttpServletResponse response);

    /**
     * Import website data from html file and return ResultVO as result.
     *
     * @param htmlFile html file
     * @param username username
     * @return {@link ResultVO}<{@link String}> ResultVO
     */
    ResultVO<String> importWebsDataFromHtmlFile(MultipartFile htmlFile, String username);
}
