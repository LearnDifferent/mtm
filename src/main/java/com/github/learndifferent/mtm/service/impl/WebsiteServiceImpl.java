package com.github.learndifferent.mtm.service.impl;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.learndifferent.mtm.annotation.common.Url;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.modify.marked.MarkCheckReturn;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck.DefaultValueIfEmpty;
import com.github.learndifferent.mtm.annotation.modify.url.UrlClean;
import com.github.learndifferent.mtm.annotation.modify.webdata.WebsiteDataClean;
import com.github.learndifferent.mtm.annotation.validation.website.marked.MarkCheck;
import com.github.learndifferent.mtm.constant.consist.HtmlFileConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.ShowPattern;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.dto.WebsitePatternDTO;
import com.github.learndifferent.mtm.dto.WebsiteWithCountDTO;
import com.github.learndifferent.mtm.entity.WebsiteDO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.manager.ElasticsearchManager;
import com.github.learndifferent.mtm.mapper.WebsiteMapper;
import com.github.learndifferent.mtm.query.SaveNewWebDataRequest;
import com.github.learndifferent.mtm.query.WebFilterRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.utils.ApplicationContextUtils;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.PageUtil;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * WebsiteService 实现类
 *
 * @author zhou
 * @date 2021/09/05
 */
@Service
public class WebsiteServiceImpl implements WebsiteService {

    private final WebsiteMapper websiteMapper;
    private final ElasticsearchManager elasticsearchManager;

    @Autowired
    public WebsiteServiceImpl(WebsiteMapper websiteMapper, ElasticsearchManager elasticsearchManager) {
        this.websiteMapper = websiteMapper;
        this.elasticsearchManager = elasticsearchManager;
    }

    @Override
    public List<WebsiteDTO> findWebsitesDataByFilter(WebFilterRequest filter) {
        List<WebsiteDO> webs = websiteMapper.findWebsitesDataByFilter(filter);
        return DozerUtils.convertList(webs, WebsiteDTO.class);
    }

    @Override
    public int countUserPost(String userName) {
        return websiteMapper.countUserPost(userName);
    }

    @Override
    public List<WebsiteDTO> findWebsitesDataByUser(String userName, Integer from, Integer size) {
        List<WebsiteDO> webs = websiteMapper.findWebsitesDataByUser(userName, from, size);
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

    @Override
    public boolean[] saveNewWebsiteData(SaveNewWebDataRequest newWebsiteData) {

        String url = newWebsiteData.getUrl();
        String username = newWebsiteData.getUsername();
        Boolean syncToElasticsearch = newWebsiteData.getSyncToElasticsearch();

        WebsiteServiceImpl websiteService =
                ApplicationContextUtils.getBean(WebsiteServiceImpl.class);

        WebWithNoIdentityDTO rawWebsite = websiteService
                .scrapeWebsiteDataFromUrl(url, username);

        // 如果选择同步到 Elasticsearch 中，就异步执行保存网页数据的方法并返回结果
        // 如果选择不同步，也就是 syncToEs 为 false 或 null 的情况：直接返回 true 作为结果，表示无需异步存放
        Future<Boolean> futureResult = elasticsearchManager
                .saveDocAsync(rawWebsite, syncToElasticsearch);

        // 将网页数据放入数据库，返回是否成功放入
        boolean toDatabase = websiteService.saveWebsiteData(rawWebsite, username);

        // true 表示存放成功或无需存放到 Elasticsearch；false 表示存放失败
        boolean toElasticsearch = true;
        try {
            // 获取异步存放数据到 Elasticsearch 的结果
            toElasticsearch = futureResult.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

        return new boolean[]{toDatabase, toElasticsearch};
    }


    /**
     * 根据链接，获取网页的 title、url、img 和简介数据。
     * <p>会先经过 {@link UrlClean} 来清理 URL 的格式，
     * 然后经过 {@link MarkCheckReturn} 来查找网页数据是否已经存在数据库中。</p>
     * <p>如果已经存在数据库中，且该 {@code userName} 的用户已经收藏过，就抛出异常；
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
    private WebWithNoIdentityDTO scrapeWebsiteDataFromUrl(@Url String url,
                                                          @Username String userName) {

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
    @EmptyStringCheck
    public WebsitePatternDTO getWebsitesByPattern(@DefaultValueIfEmpty String pattern,
                                                  @DefaultValueIfEmpty String username,
                                                  PageInfoDTO pageInfo) {

        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();

        WebsitePatternDTO.WebsitePatternDTOBuilder builder = WebsitePatternDTO.builder();

        ShowPattern showPattern = castPatternStringToPatternEnum(pattern);

        switch (showPattern) {
            case MARKED:
                // 最多收藏的情况
                List<WebsiteWithCountDTO> markedWebs = showMostMarked(from, size);
                int markedTotalCount = countDistinctUrl();
                int markedTotalPage = PageUtil.getAllPages(markedTotalCount, size);
                builder.webs(markedWebs).totalPage(markedTotalPage);
                break;
            case USER_PAGE:
                // 查看某个用户所有收藏的情况
                List<WebsiteDTO> userPageWebs = findWebsitesDataByUser(username, from, size);
                int userPageTotalCount = countUserPost(username);
                int userPageTotalPage = PageUtil.getAllPages(userPageTotalCount, size);
                builder.webs(userPageWebs).totalPage(userPageTotalPage);
                break;
            case WITHOUT_USER_PAGE:
                // 查看除去某个用户的所有收藏的情况
                List<WebsiteDTO> withoutUserPageWebs = findWebsitesDataExcludeUser(username, from, size);
                int withoutUserPageTotalCount = countExcludeUserPost(username);
                int withoutUserPageTotalPage = PageUtil.getAllPages(withoutUserPageTotalCount, size);
                builder.webs(withoutUserPageWebs).totalPage(withoutUserPageTotalPage);
                break;
            case DEFAULT:
            default:
                // 默认查看全部的情况（如果 pattern 不是以上的情况，也是按照默认情况处理）
                List<WebsiteDTO> webs = showAllWebsiteDataDesc(from, size);
                int totalCount = countAll();
                int totalPage = PageUtil.getAllPages(totalCount, size);
                builder.webs(webs).totalPage(totalPage);
        }

        return builder.build();
    }

    /**
     * 获取最多用户收藏的 URL 及其网页信息
     *
     * @param from 分页起始
     * @param size 页面大小
     * @return 最多用户收藏的网页及其信息
     */
    private List<WebsiteWithCountDTO> showMostMarked(int from, int size) {
        return websiteMapper.showMostMarked(from, size);
    }

    /**
     * 计算 URL 出现的次数，并剔除重复
     *
     * @return 一共有多少条数据
     */
    private int countDistinctUrl() {
        return websiteMapper.countDistinctUrl();
    }

    /**
     * 计算所有数据
     *
     * @return 一共有多少条数据
     */
    private int countAll() {
        return websiteMapper.countAll();
    }

    /**
     * 计算除去某个用户收藏的网页的总数
     *
     * @param userName 除去某个用户
     * @return 一共有多少条数据
     */
    private int countExcludeUserPost(String userName) {
        return websiteMapper.countExcludeUserPost(userName);
    }

    /**
     * 查找除去某个用户的所有网页
     *
     * @param userName 不查找该用户 / 某个用户
     * @param from     from
     * @param size     size
     * @return 除去某个用户的所有网页
     */
    private List<WebsiteDTO> findWebsitesDataExcludeUser(String userName, int from, int size) {
        List<WebsiteDO> webs = websiteMapper.findWebsitesDataExcludeUser(userName, from, size);
        return DozerUtils.convertList(webs, WebsiteDTO.class);
    }

    /**
     * 倒序查询所有网页（分页）
     *
     * @param from 起始
     * @param size 页面大小
     * @return 查询到的网页
     */
    private List<WebsiteDTO> showAllWebsiteDataDesc(int from, int size) {
        List<WebsiteDO> webs = websiteMapper.showAllWebsiteDataDesc(from, size);
        return DozerUtils.convertList(webs, WebsiteDTO.class);
    }

    private ShowPattern castPatternStringToPatternEnum(String pattern) {

        pattern = camelToSnake(pattern).toUpperCase();

        try {
            return ShowPattern.valueOf(pattern);
        } catch (IllegalArgumentException | NullPointerException e) {
            // 找不到的时候，返回默认值
            return ShowPattern.DEFAULT;
        }
    }

    private String camelToSnake(String value) {
        return new PropertyNamingStrategy.SnakeCaseStrategy().translate(value);
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

    /**
     * 以 HTML 格式，导出该用户的所有网页数据。如果该用户没有数据，直接输出无数据的提示。
     *
     * @param username 用户名
     * @param response response
     * @throws ServiceException ResultCode.CONNECTION_ERROR
     */
    @Override
    public void exportWebsDataByUserToHtmlFile(String username, HttpServletResponse response) {

        Date date = Calendar.getInstance().getTime();
        String time = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS").format(date);
        String filename = username + "_" + time + ".html";

        String html = getWebsDataByUserInHtml(username);

        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            response.getWriter().print(html);
        } catch (IOException e) {
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    /**
     * 根据用户名，生成该用户保存的所有网页数据，并返回 html 格式的字符串。
     *
     * @param username 用户名
     * @return {@code String}
     */
    private String getWebsDataByUserInHtml(String username) {

        List<WebsiteDTO> webs = findWebsitesDataByUser(username);

        StringBuilder sb = new StringBuilder();

        sb.append(HtmlFileConstant.FILE_START);

        // 如果没有内容的情况
        if (CollectionUtils.isEmpty(webs)) {
            sb.append(username).append(" doesn't have any data.")
                    .append(HtmlFileConstant.FILE_END);
            return sb.toString();
        }

        webs.forEach(w -> {
            sb.append(HtmlFileConstant.BEFORE_IMG);
            sb.append(w.getImg());
            sb.append(HtmlFileConstant.AFTER_IMG_BEFORE_URL);
            sb.append(w.getUrl());
            sb.append(HtmlFileConstant.BEFORE_TITLE);
            sb.append(w.getTitle());
            sb.append(HtmlFileConstant.AFTER_URL_BEFORE_DESC);
            sb.append(w.getDesc());
            sb.append(HtmlFileConstant.AFTER_DESC);
        });

        sb.append(HtmlFileConstant.FILE_END);

        return sb.toString();
    }


    /**
     * 根据用户获取该用户的所有网页数据
     *
     * @param userName 用户名
     * @return {@code List<WebsiteDO>}
     */
    private List<WebsiteDTO> findWebsitesDataByUser(String userName) {
        // from 和 size 为 null 的时候，表示不分页，直接获取全部
        List<WebsiteDO> webs = websiteMapper.findWebsitesDataByUser(userName, null, null);
        return DozerUtils.convertList(webs, WebsiteDTO.class);
    }

    @Override
    public ResultVO<String> importWebsDataFromHtmlFile(MultipartFile htmlFile, String username) {
        // [Success][Failure][Already Exists]
        int[] result = new int[3];

        // 默认的回复
        ResultVO<String> defaultFailResult =
                ResultCreator.failResult("No Data Available. Please Upload the Correct HTML file.");

        try (InputStream in = htmlFile.getInputStream()) {

            Document document = Jsoup.parse(in, "UTF-8", "");
            Elements dts = document.getElementsByTag("dt");

            dts.forEach(dt -> {
                WebWithNoIdentityDTO web = getWebFromElement(dt);
                try {
                    WebsiteServiceImpl websiteService =
                            ApplicationContextUtils.getBean(WebsiteServiceImpl.class);
                    boolean success = websiteService.saveWebsiteData(web, username);
                    if (success) {
                        result[0]++;
                    } else {
                        result[1]++;
                    }
                } catch (ServiceException e) {
                    switch (e.getResultCode()) {
                        case ALREADY_MARKED:
                            // 如果抛出的是已经收藏过了的异常，就将 Already Exists 结果加一
                            result[2]++;
                            break;
                        case URL_MALFORMED:
                            // 如果是 URL 格式异常，就按失败来处理
                            result[1]++;
                            break;
                        default:
                            // 如果是其他情况，就继续抛出
                            throw new ServiceException(e.getResultCode(), e.getMessage());
                    }
                }
            });

        } catch (IOException e) {
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        } catch (IllegalArgumentException e) {
            // Jsoup 如果抛出了 IllegalArgumentException，说明传入的 HTML 文件有问题
            return defaultFailResult;
        }

        boolean hasData =
                result[0] + result[1] + result[2] != 0;

        if (hasData) {
            String responseMsg = "Success: " + result[0]
                    + ", Failure: " + result[1]
                    + ", Already Exists: " + result[2];
            return ResultCreator.okResult(responseMsg);
        }

        return defaultFailResult;
    }

    private WebWithNoIdentityDTO getWebFromElement(org.jsoup.nodes.Element dt) {

        WebWithNoIdentityDTO.WebWithNoIdentityDTOBuilder webBuilder =
                WebWithNoIdentityDTO.builder();

        Elements imgTag = dt.getElementsByTag("img");
        String img = imgTag.get(0).attr("abs:src");
        webBuilder.img(img);

        Elements aTag = dt.getElementsByTag("a");
        String url = aTag.get(0).attr("href");
        String title = aTag.get(0).text();

        webBuilder.url(url).title(title);

        String desc = dt.text();
        webBuilder.desc(desc);

        return webBuilder.build();
    }
}
