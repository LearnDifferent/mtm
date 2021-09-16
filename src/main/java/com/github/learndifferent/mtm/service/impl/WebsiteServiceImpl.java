package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.annotation.modify.marked.MarkCheckReturn;
import com.github.learndifferent.mtm.annotation.modify.url.UrlClean;
import com.github.learndifferent.mtm.annotation.modify.webdata.WebsiteDataClean;
import com.github.learndifferent.mtm.annotation.validation.website.marked.MarkCheck;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.WebForSearchDTO;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.dto.WebsiteWithCountDTO;
import com.github.learndifferent.mtm.entity.WebsiteDO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.mapper.WebsiteMapper;
import com.github.learndifferent.mtm.query.WebFilter;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.utils.DozerUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * WebsiteService 实现类
 *
 * @author zhou
 * @date 2021/09/05
 */
@Service
public class WebsiteServiceImpl implements WebsiteService {

    private final WebsiteMapper websiteMapper;

    @Autowired
    public WebsiteServiceImpl(WebsiteMapper websiteMapper) {
        this.websiteMapper = websiteMapper;
    }

    @Override
    public List<WebsiteDTO> findWebsitesDataByFilter(WebFilter filter) {
        List<WebsiteDO> webs = websiteMapper.findWebsitesDataByFilter(filter);
        return DozerUtils.convertList(webs, WebsiteDTO.class);
    }

    @Override
    public List<WebsiteWithCountDTO> showMostMarked(int from, int size) {
        return websiteMapper.showMostMarked(from, size);
    }

    @Override
    public int countAll() {
        return websiteMapper.countAll();
    }

    @Override
    public int countDistinctUrl() {
        return websiteMapper.countDistinctUrl();
    }

    @Override
    public List<WebsiteDTO> showAllWebsiteDataDesc(int from, int size) {
        List<WebsiteDO> webs = websiteMapper.showAllWebsiteDataDesc(from, size);
        return DozerUtils.convertList(webs, WebsiteDTO.class);
    }

    @Override
    public int countUserPost(String userName) {
        return websiteMapper.countUserPost(userName);
    }

    @Override
    public int countExcludeUserPost(String userName) {
        return websiteMapper.countExcludeUserPost(userName);
    }

    @Override
    public List<WebsiteDTO> findWebsitesDataExcludeUser(String userName, int from, int size) {
        List<WebsiteDO> webs = websiteMapper.findWebsitesDataExcludeUser(userName, from, size);
        return DozerUtils.convertList(webs, WebsiteDTO.class);
    }

    @Override
    public List<WebsiteDTO> findWebsitesDataByUser(String userName, Integer from, Integer size) {
        List<WebsiteDO> webs = websiteMapper.findWebsitesDataByUser(userName, from, size);
        return DozerUtils.convertList(webs, WebsiteDTO.class);
    }

    @Override
    public List<WebsiteDTO> findWebsitesDataByUser(String userName) {
        // from 和 size 为 null 的时候，表示不分页，直接获取全部
        List<WebsiteDO> webs = websiteMapper.findWebsitesDataByUser(userName, null, null);
        return DozerUtils.convertList(webs, WebsiteDTO.class);
    }

    @Override
    public WebsiteDTO findWebsiteDataById(int webId) {
        WebsiteDO web = websiteMapper.getWebsiteDataById(webId);
        return DozerUtils.convert(web, WebsiteDTO.class);
    }

    @Override
    @WebsiteDataClean
    @MarkCheck(usernameParamName = "userName",
            paramClassContainsUrl = WebWithNoIdentityDTO.class,
            urlFieldNameInParamClass = "url")
    public boolean saveWebsiteData(WebWithNoIdentityDTO rawWebsite, String userName) {
        // 添加信息后，放入数据库
        WebsiteDO websiteDO = DozerUtils.convert(rawWebsite, WebsiteDO.class);
        return websiteMapper.addWebsiteData(websiteDO
                .setUserName(userName).setCreateTime(new Date()));
    }

    /**
     * 先经过 @UrlClean 来清理 URL 的格式，
     * 然后经过 @IfMarkedThenReturn 来查找网页数据是否已经存在数据库中。
     * <p>如果已经存在数据库中，且用户已经收藏过，就抛出异常；
     * 如果已经存在，且用户没有收藏，就返回数据库内的数据。</p>
     * <p>如果没有在数据库中，就从网页中抓取并返回</p>
     *
     * @param url      网页链接
     * @param userName 收藏该网页的用户
     * @return 剔除了唯一信息的网页数据
     * @throws ServiceException 如果已经收藏过，会抛出异常，状态码为 ResultCode.ALREADY_MARKED
     */
    @UrlClean
    @MarkCheckReturn
    @Override
    public WebWithNoIdentityDTO scrapeWebsiteDataFromUrl(String url, String userName) {

        try {
            Document document = Jsoup.parse(new URL(url), 3000);

            // 如果获取到了，就保存
            String title = document.title();
            String desc = document.body().text();
            String img = getFirstImg(document);

            return WebWithNoIdentityDTO.builder()
                    .title(title).url(url).img(img).desc(desc)
                    .build();
        } catch (MalformedURLException e) {
            throw new ServiceException(ResultCode.URL_MALFORMED);
        } catch (SocketTimeoutException e) {
            throw new ServiceException(ResultCode.URL_ACCESS_DENIED);
        } catch (IOException e) {
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    private String getFirstImg(Document document) {
        Elements images = document.select("img[src]");

        if (images.size() > 0) {
            return images.get(0).attr("abs:src");
        }
        // 如果没有图片，就返回一个默认图片地址
        return "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fbpic.588ku.com%2Felement_origin_min_pic%2F00%2F93%2F63%2F2656f2a6a663e1c.jpg&refer=http%3A%2F%2Fbpic.588ku.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1618635152&t=e26535a2d80f40281592178ee20ee656";
    }

    @Override
    public List<WebForSearchDTO> getAllWebsitesDataForSearch() {
        return websiteMapper.getAllWebsitesDataForSearch();
    }

    @Override
    public boolean updateWebsiteDataById(WebsiteDTO website) {
        WebsiteDO websiteDO = DozerUtils.convert(website, WebsiteDO.class);
        return websiteMapper.updateWebsiteDataById(websiteDO);
    }

    @Override
    public List<WebsiteDTO> findWebsitesDataByUrl(String url) {
        List<WebsiteDO> webs = websiteMapper.findWebsitesDataByUrl(url);
        return DozerUtils.convertList(webs, WebsiteDTO.class);
    }

    @Override
    public boolean delWebsiteDataById(int webId) {
        return websiteMapper.deleteWebsiteDataById(webId);
    }

    @Override
    public void deleteWebsiteDataByUsername(String userName) {
        websiteMapper.deleteWebsiteDataByUsername(userName);
    }
}
