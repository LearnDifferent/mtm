package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.annotation.common.Url;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.WebId;
import com.github.learndifferent.mtm.annotation.modify.bookmarking.CheckAndReturnExistingData;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck.DefaultValueIfEmpty;
import com.github.learndifferent.mtm.annotation.modify.url.UrlClean;
import com.github.learndifferent.mtm.annotation.modify.webdata.WebsiteDataClean;
import com.github.learndifferent.mtm.annotation.validation.website.bookmarked.BookmarkCheck;
import com.github.learndifferent.mtm.annotation.validation.website.permission.ModifyWebsitePermissionCheck;
import com.github.learndifferent.mtm.constant.consist.HtmlFileConstant;
import com.github.learndifferent.mtm.constant.enums.HomeTimeline;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.BookmarkFilterDTO;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.PopularBookmarkDTO;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.dto.WebsiteWithPrivacyDTO;
import com.github.learndifferent.mtm.entity.WebsiteDO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.manager.DeleteTagManager;
import com.github.learndifferent.mtm.manager.DeleteViewManager;
import com.github.learndifferent.mtm.manager.ElasticsearchManager;
import com.github.learndifferent.mtm.mapper.WebsiteMapper;
import com.github.learndifferent.mtm.query.FilterBookmarksRequest;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.utils.ApplicationContextUtils;
import com.github.learndifferent.mtm.utils.CompareStringUtil;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.PageUtil;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.learndifferent.mtm.vo.BookmarkResultVO;
import com.github.learndifferent.mtm.vo.BookmarkVO;
import com.github.learndifferent.mtm.vo.BookmarksAndTotalPagesVO;
import com.github.learndifferent.mtm.vo.PopularBookmarksVO;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * WebsiteService 实现类
 *
 * @author zhou
 * @date 2021/09/05
 */
@Service
@Slf4j
public class WebsiteServiceImpl implements WebsiteService {

    private final WebsiteMapper websiteMapper;
    private final ElasticsearchManager elasticsearchManager;
    private final DeleteViewManager deleteViewManager;
    private final DeleteTagManager deleteTagManager;

    @Autowired
    public WebsiteServiceImpl(WebsiteMapper websiteMapper,
                              ElasticsearchManager elasticsearchManager,
                              DeleteViewManager deleteViewManager,
                              DeleteTagManager deleteTagManager) {
        this.websiteMapper = websiteMapper;
        this.elasticsearchManager = elasticsearchManager;
        this.deleteViewManager = deleteViewManager;
        this.deleteTagManager = deleteTagManager;
    }

    @Override
    public List<BookmarkVO> filterPublicBookmarks(FilterBookmarksRequest filterRequest) {

        BookmarkFilterDTO filter = BookmarkFilterDTO.of(filterRequest);
        List<WebsiteDO> bookmarks = websiteMapper.filterPublicBookmarks(filter);
        return DozerUtils.convertList(bookmarks, BookmarkVO.class);
    }

    /**
     * Count number of the user's bookmarks
     *
     * @param userName       username of the user
     * @param includePrivate true if including the private bookmarks
     * @return number of the user's bookmarks
     */
    private int countUserPost(String userName, boolean includePrivate) {
        return websiteMapper.countUserPost(userName, includePrivate);
    }

    @Override
    public WebsiteWithPrivacyDTO findWebsiteDataWithPrivacyById(int webId) {
        WebsiteDO web = websiteMapper.getWebsiteDataById(webId);
        return DozerUtils.convert(web, WebsiteWithPrivacyDTO.class);
    }

    @Override
    @WebsiteDataClean
    @BookmarkCheck(usernameParamName = "userName",
                   paramClassContainsUrl = WebWithNoIdentityDTO.class,
                   urlFieldNameInParamClass = "url")
    public boolean bookmarkWithExistingData(
            WebWithNoIdentityDTO webWithNoIdentity, String userName, boolean isPublic) {
        // 添加信息后，放入数据库
        WebsiteDO websiteDO = DozerUtils.convert(webWithNoIdentity, WebsiteDO.class);
        return websiteMapper.addWebsiteData(websiteDO
                .setUserName(userName)
                .setCreateTime(Instant.now())
                .setIsPublic(isPublic));
    }

    @Override
    public BookmarkResultVO bookmark(String url, String username, Boolean isPublic, Boolean beInEs) {

        boolean bePublic = Optional.ofNullable(isPublic).orElse(true);
        boolean syncToElasticsearchValue = Optional.ofNullable(beInEs).orElse(true);

        // Only public data can be added to Elasticsearch
        boolean syncToElasticsearch = bePublic && syncToElasticsearchValue;

        // get the basic data
        WebsiteServiceImpl bean = ApplicationContextUtils.getBean(WebsiteServiceImpl.class);
        WebWithNoIdentityDTO basic = bean.scrapeWebDataFromUrl(url, username);

        // Default Future Result
        Future<Boolean> resultOfElasticsearch = null;

        if (syncToElasticsearch) {
            // 如果选择同步到 Elasticsearch 中，就异步执行保存方法并返回结果
            // 如果选择不同步，也就是 syncToEs 为 false 或 null 的情况：
            // 直接返回 true 作为结果，表示无需异步存放
            // 此时 resultOfElasticsearch 会从 null 转化为 FutureTask 类型的值
            resultOfElasticsearch = elasticsearchManager.
                    saveBookmarkToElasticsearchAsync(basic);
        }

        // save to database
        boolean hasSavedToDatabase = bean.bookmarkWithExistingData(basic, username, bePublic);

        // 如果 Future Result 为 null，说明无需放入 Elasticsearch 中
        if (resultOfElasticsearch == null) {
            // return the result of saving to database
            return BookmarkResultVO.builder().hasSavedToDatabase(hasSavedToDatabase).build();
        }

        // get the result of saving to Elasticsearch
        boolean hasSavedToElasticsearch = false;
        try {
            hasSavedToElasticsearch = resultOfElasticsearch.get(10L, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

        return BookmarkResultVO.builder()
                .hasSavedToDatabase(hasSavedToDatabase)
                .hasSavedToElasticsearch(hasSavedToElasticsearch)
                .build();
    }

    /**
     * Firstly, {@link UrlClean} will clean up the URL.
     * <p>
     * Secondly, {@link CheckAndReturnExistingData} will check whether the website is bookmarked by the user and the
     * website data is stored in database:
     * </p>
     * <li>
     * If the user has already bookmarked the website, then throw an exception.
     * </li>
     * <li>
     * If the user didn't bookmark the website and the website data is stored in database,
     * then return the data in database.
     * </li>
     * <li>
     * If the user didn't bookmark the website and website data is not stored in database, then do nothing.
     * </li>
     * <p>
     * Lastly, scrap the website data from URL.
     * </p>
     *
     * @param url      URL
     * @param userName username of the user
     * @return {@link WebWithNoIdentityDTO}
     */
    @UrlClean
    @CheckAndReturnExistingData
    public WebWithNoIdentityDTO scrapeWebDataFromUrl(@Url String url, @Username String userName) {

        try {
            Document document = Jsoup.parse(new URL(url), 3000);

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
    public BookmarksAndTotalPagesVO getHomeTimeline(String currentUsername,
                                                    HomeTimeline homeTimeline,
                                                    @DefaultValueIfEmpty String requestedUsername,
                                                    PageInfoDTO pageInfo) {
        // get pagination information
        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();

        // check whether the current user is requested user
        boolean isCurrentUser = currentUsername.equalsIgnoreCase(requestedUsername);

        switch (homeTimeline) {
            case USER:
                // check out public bookmarks of the requested user's
                // this will include private bookmarks if the requested user is current user
                return getUserBookmarks(requestedUsername, from, size, isCurrentUser);
            case BLOCK:
                // check out all public bookmarks except the requested user's
                // this will include current user's private bookmarks if the requested user is not current user
                return getBookmarksExceptRequestedUser(currentUsername, requestedUsername, from, size);
            case LATEST:
            default:
                // get all public bookmarks and current user's private bookmarks
                return getLatestBookmarks(currentUsername, from, size);
        }
    }

    private BookmarksAndTotalPagesVO getBookmarksExceptRequestedUser(
            String currentUsername, String requestedUsername, int from, int size) {

        List<WebsiteWithPrivacyDTO> bookmarks =
                getAllPubSpecUserPriWebsExcludeUser(requestedUsername, from, size, currentUsername);

        int totalCount = countExcludeUserPost(requestedUsername, currentUsername);
        int totalPages = PageUtil.getAllPages(totalCount, size);
        return BookmarksAndTotalPagesVO.builder().bookmarks(bookmarks).totalPages(totalPages).build();
    }

    private BookmarksAndTotalPagesVO getLatestBookmarks(String currentUsername, int from, int size) {
        List<WebsiteWithPrivacyDTO> bookmarks =
                getAllPubSpecUserPriWebs(from, size, currentUsername);

        int totalCount = countAllPubAndSpecUserPriWebs(currentUsername);
        int totalPages = PageUtil.getAllPages(totalCount, size);
        return BookmarksAndTotalPagesVO.builder().bookmarks(bookmarks).totalPages(totalPages).build();
    }

    @Override
    public PopularBookmarksVO getPopularBookmarksAndTotalPages(PageInfoDTO pageInfo) {
        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();

        List<PopularBookmarkDTO> bookmarks = websiteMapper.getPopularPublicBookmarks(from, size);
        int totalCount = websiteMapper.countDistinctPublicUrl();
        int totalPages = PageUtil.getAllPages(totalCount, size);
        return PopularBookmarksVO.builder().bookmarks(bookmarks).totalPages(totalPages).build();
    }

    /**
     * 计算所有公开网页数据的条数。指定的用户需要把私有的网页数据个数也计入其中
     *
     * @param specUsername 指定的用户名
     * @return int 数据条数
     */
    private int countAllPubAndSpecUserPriWebs(String specUsername) {
        return websiteMapper.countAllPubAndSpecUserPriWebs(specUsername);
    }

    /**
     * 计算除去 {@code excludeUsername} 用户名的用户收藏的公开的网页的总数，
     * 如果 {@code excludeUsername} 和 {@code userNameToShowAll} 不相等，
     * 那么用户名为 {@code userNameToShowAll} 的用户的私有的网页数据也要统计
     *
     * @param excludeUsername   除去该用户名的用户
     * @param userNameToShowAll 该用户名的用户需要展示公开和私有的网页数据
     * @return 一共有多少条数据
     */
    private int countExcludeUserPost(String excludeUsername, String userNameToShowAll) {
        return websiteMapper.countExcludeUserPost(excludeUsername, userNameToShowAll);
    }

    /**
     * Get user with the name of {@code userNameToShowAll}'s private and all user's public bookmarks,
     * excluding the user with the name of with {@code excludeUsername}'s bookmarks.
     *
     * @param excludeUsername   不查找该用户 / 某个用户
     * @param from              from
     * @param size              size
     * @param userNameToShowAll 该用户名的用户的所有数据都要展示
     * @return {@link List}<{@link WebsiteWithPrivacyDTO}> 除去某个用户的所有网页
     */
    private List<WebsiteWithPrivacyDTO> getAllPubSpecUserPriWebsExcludeUser(
            String excludeUsername, int from, int size, String userNameToShowAll) {

        List<WebsiteDO> websites = websiteMapper.findWebsitesDataExcludeUser(
                excludeUsername, from, size, userNameToShowAll);

        return getBookmarks(websites);
    }

    /**
     * Get all public and specific user's private bookmarks
     *
     * @param from         from
     * @param size         size
     * @param specUsername specific user's name
     * @return {@link List}<{@link WebsiteWithPrivacyDTO}>
     */
    private List<WebsiteWithPrivacyDTO> getAllPubSpecUserPriWebs(Integer from,
                                                                 Integer size,
                                                                 String specUsername) {
        List<WebsiteDO> websites =
                websiteMapper.getAllPubAndSpecUserPriWebs(from, size, specUsername);

        return getBookmarks(websites);
    }

    private List<WebsiteWithPrivacyDTO> getBookmarks(List<WebsiteDO> websites) {
        return DozerUtils.convertList(websites, WebsiteWithPrivacyDTO.class);
    }

    private BookmarksAndTotalPagesVO getUserBookmarks(String username, int from, int size, boolean includePrivate) {

        int totalCounts = countUserPost(username, includePrivate);
        int totalPages = PageUtil.getAllPages(totalCounts, size);

        List<WebsiteDO> b = websiteMapper.findWebsitesDataByUser(username, from, size, includePrivate);
        List<WebsiteWithPrivacyDTO> bookmarks = getBookmarks(b);

        return BookmarksAndTotalPagesVO.builder()
                .totalPages(totalPages)
                .bookmarks(bookmarks)
                .build();
    }

    @Override
    public BookmarksAndTotalPagesVO getUserBookmarks(String username, PageInfoDTO pageInfo, Boolean includePrivate) {
        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();
        boolean shouldIncludePrivate = Optional.ofNullable(includePrivate).orElse(false);
        return getUserBookmarks(username, from, size, shouldIncludePrivate);
    }

    @Override
    public List<WebsiteDTO> findWebsitesDataByUrl(String url) {
        List<WebsiteDO> webs = websiteMapper.findWebsitesDataByUrl(url);
        return DozerUtils.convertList(webs, WebsiteDTO.class);
    }

    @Override
    @ModifyWebsitePermissionCheck
    public boolean deleteBookmark(@WebId Integer webId, @Username String userName) {
        // Web Id will not be null after checking by @ModifyWebsitePermissionCheck
        boolean success = websiteMapper.deleteWebsiteDataById(webId);
        if (success) {
            // delete views
            deleteViewManager.deleteWebView(webId);
            // delete tags
            deleteTagManager.deleteAllTagsByWebId(webId);
        }
        return success;
    }

    @Override
    @ModifyWebsitePermissionCheck
    public boolean changePrivacySettings(@WebId Integer webId, @Username String userName) {
        // Web Id will not be null after checking by @ModifyWebsitePermissionCheck
        WebsiteDO web = websiteMapper.getWebsiteDataById(webId);
        ThrowExceptionUtils.throwIfNull(web, ResultCode.WEBSITE_DATA_NOT_EXISTS);

        boolean newPrivacy = !web.getIsPublic();
        WebsiteDO webWithNewPrivacy = web.setIsPublic(newPrivacy);
        return websiteMapper.updateWebsiteDataById(webWithNewPrivacy);
    }

    @Override
    public WebsiteDTO getBookmark(int webId, String userName) {
        WebsiteDO web = websiteMapper.getWebsiteDataById(webId);

        // website data does not exist
        ThrowExceptionUtils.throwIfNull(web, ResultCode.WEBSITE_DATA_NOT_EXISTS);

        // website data is not public
        // and the owner's username of website data does not match the username
        boolean noPermission = Boolean.FALSE.equals(web.getIsPublic())
                && CompareStringUtil.notEqualsIgnoreCase(userName, web.getUserName());
        ThrowExceptionUtils.throwIfTrue(noPermission, ResultCode.PERMISSION_DENIED);

        return DozerUtils.convert(web, WebsiteDTO.class);
    }

    @Override
    public void exportBookmarksToHtmlFile(String username,
                                          String currentUsername,
                                          HttpServletResponse response) {

        username = StringUtils.isEmpty(username) ? currentUsername : username;

        Instant now = Instant.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss_SSS")
                .withZone(ZoneId.systemDefault());
        String time = dtf.format(now);

        String filename = username + "_" + time + ".html";

        boolean includePrivate = username.equalsIgnoreCase(currentUsername);
        String html = getBookmarksByUserInHtml(username, includePrivate);

        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            response.getWriter().print(html);
        } catch (IOException e) {
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    private String getBookmarksByUserInHtml(String username, boolean includePrivate) {

        List<WebsiteDTO> bookmarks = findAllWebDataByUser(username, includePrivate);

        StringBuilder sb = new StringBuilder();
        sb.append(HtmlFileConstant.FILE_START);

        // Return this message if no data available
        if (CollectionUtils.isEmpty(bookmarks)) {
            return sb.append(username)
                    .append(" doesn't have any data.")
                    .append(HtmlFileConstant.FILE_END)
                    .toString();
        }

        bookmarks.forEach(w -> {
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

        return sb.append(HtmlFileConstant.FILE_END).toString();
    }

    private List<WebsiteDTO> findAllWebDataByUser(String userName, boolean includePrivate) {
        // from 和 size 为 null 的时候，表示不分页，直接获取全部
        List<WebsiteDO> webs = websiteMapper.findWebsitesDataByUser(userName,
                null, null, includePrivate);
        return DozerUtils.convertList(webs, WebsiteDTO.class);
    }

    @Override
    public String importBookmarksFromHtmlFile(MultipartFile htmlFile, String username) {
        // [Success][Failure][Existing]
        int[] result = new int[3];

        try (InputStream in = htmlFile.getInputStream()) {
            importBookmarksAndUpdateResult(username, result, in);
        } catch (IOException e) {
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        } catch (IllegalArgumentException e) {
            // The HTML file is not valid if Jsoup throws an IllegalArgumentException
            throw new ServiceException(ResultCode.HTML_FILE_NO_BOOKMARKS);
        }
        throwExceptionIfFailToImport(result);

        return "Success: " + result[0]
                + ", Failure: " + result[1]
                + ", Existing : " + result[2];
    }

    private void importBookmarksAndUpdateResult(String username, int[] result, InputStream in) throws IOException {

        Document document = Jsoup.parse(in, "UTF-8", "");
        Elements dts = document.getElementsByTag("dt");

        dts.forEach(dt -> {
            WebWithNoIdentityDTO bookmark = getBookmarkFromElement(dt);
            bookmarkAndUpdateResult(username, result, bookmark);
        });
    }

    private WebWithNoIdentityDTO getBookmarkFromElement(org.jsoup.nodes.Element dt) {

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

    private void bookmarkAndUpdateResult(String username, int[] result, WebWithNoIdentityDTO web) {
        try {
            boolean success = bookmarkAndGetResult(username, web);
            updateImportingResult(result, success);
        } catch (ServiceException e) {
            ResultCode resultCode = e.getResultCode();
            updateImportingResult(result, e, resultCode);
        }
    }

    private boolean bookmarkAndGetResult(String username, WebWithNoIdentityDTO web) {
        WebsiteServiceImpl websiteService =
                ApplicationContextUtils.getBean(WebsiteServiceImpl.class);
        // the imported bookmarks are public
        return websiteService.bookmarkWithExistingData(web, username, true);
    }

    private void updateImportingResult(int[] result, boolean success) {
        if (success) {
            result[0]++;
        } else {
            result[1]++;
        }
    }

    private void updateImportingResult(int[] result, ServiceException e, ResultCode resultCode) {
        switch (resultCode) {
            case ALREADY_SAVED:
                // Existing: has already saved it
                result[2]++;
                break;
            case URL_MALFORMED:
                // Failure: Not a valid URL
                result[1]++;
                break;
            default:
                // Others
                throw new ServiceException(resultCode, e.getMessage());
        }
    }

    private void throwExceptionIfFailToImport(int[] result) {
        boolean hasNoData =
                result[0] + result[1] + result[2] == 0;
        ThrowExceptionUtils.throwIfTrue(hasNoData, ResultCode.HTML_FILE_NO_BOOKMARKS);
    }
}