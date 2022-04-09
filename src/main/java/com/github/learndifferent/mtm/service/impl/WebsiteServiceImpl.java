package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.annotation.common.Url;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.WebId;
import com.github.learndifferent.mtm.annotation.modify.bookmarking.CheckAndReturnBasicData;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck.DefaultValueIfEmpty;
import com.github.learndifferent.mtm.annotation.modify.url.UrlClean;
import com.github.learndifferent.mtm.annotation.modify.webdata.WebsiteDataClean;
import com.github.learndifferent.mtm.annotation.validation.website.bookmarked.BookmarkCheck;
import com.github.learndifferent.mtm.annotation.validation.website.permission.ModifyWebsitePermissionCheck;
import com.github.learndifferent.mtm.constant.consist.HtmlFileConstant;
import com.github.learndifferent.mtm.constant.enums.HomeTimeline;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.BasicWebDataDTO;
import com.github.learndifferent.mtm.dto.BasicWebDataDTO.BasicWebDataDTOBuilder;
import com.github.learndifferent.mtm.dto.BookmarkFilterDTO;
import com.github.learndifferent.mtm.dto.NewBookmarkDTO;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.PopularBookmarkDTO;
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
import com.github.learndifferent.mtm.utils.PaginationUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.learndifferent.mtm.vo.BookmarkVO;
import com.github.learndifferent.mtm.vo.BookmarkingResultVO;
import com.github.learndifferent.mtm.vo.BookmarksAndTotalPagesVO;
import com.github.learndifferent.mtm.vo.PopularBookmarksVO;
import com.github.learndifferent.mtm.vo.VisitedBookmarksVO;
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
import org.apache.commons.lang3.BooleanUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Website Service Implementation
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

    @Override
    @WebsiteDataClean
    @BookmarkCheck(usernameParamName = "username",
                   classContainsUrlParamName = BasicWebDataDTO.class,
                   urlFieldNameInParamClass = "url")
    public boolean bookmarkWithBasicWebData(BasicWebDataDTO data, String username, boolean isPublic) {
        NewBookmarkDTO newBookmark = NewBookmarkDTO.of(data, username, isPublic);
        WebsiteDO w = DozerUtils.convert(newBookmark, WebsiteDO.class);
        return websiteMapper.addBookmark(w);
    }

    @Override
    public BookmarkingResultVO bookmark(String url, String username, Boolean isPublic, Boolean beInEs) {
        // get the basic data
        BasicWebDataDTO basic = scrapeWebData(url, username);

        // Only public data can be added to Elasticsearch
        boolean bePublic = Optional.ofNullable(isPublic).orElse(true);
        boolean shouldBeInElasticsearch = Optional.ofNullable(beInEs).orElse(true);
        boolean shouldBeInEs = bePublic && shouldBeInElasticsearch;

        return shouldBeInEs ? saveToElasticsearchAndDatabase(username, basic)
                : saveToDatabase(username, basic, bePublic);
    }

    private BookmarkingResultVO saveToElasticsearchAndDatabase(String username, BasicWebDataDTO data) {
        // save to Elasticsearch asynchronously
        Future<Boolean> elasticsearchResult = elasticsearchManager.saveToElasticsearchAsync(data);
        // save to database and get the BookmarkingResultVO
        BookmarkingResultVO result = saveToDatabase(username, data, true);
        // get the result of saving to Elasticsearch asynchronously
        boolean hasSavedToElasticsearch = false;
        try {
            hasSavedToElasticsearch = elasticsearchResult.get(10L, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return result.setHasSavedToElasticsearch(hasSavedToElasticsearch);
    }

    private BookmarkingResultVO saveToDatabase(String username, BasicWebDataDTO basic, boolean bePublic) {
        // get the result of saving to database
        WebsiteServiceImpl bean = ApplicationContextUtils.getBean(WebsiteServiceImpl.class);
        boolean hasSavedToDatabase = bean.bookmarkWithBasicWebData(basic, username, bePublic);
        // return the result of saving to database
        return BookmarkingResultVO.builder().hasSavedToDatabase(hasSavedToDatabase).build();
    }

    private BasicWebDataDTO scrapeWebData(String url, String userName) {

        try {
            WebsiteServiceImpl bean = ApplicationContextUtils.getBean(WebsiteServiceImpl.class);
            return bean.scrapeWebDataFromUrl(url, userName);
        } catch (MalformedURLException e) {
            throw new ServiceException(ResultCode.URL_MALFORMED);
        } catch (SocketTimeoutException e) {
            throw new ServiceException(ResultCode.URL_ACCESS_DENIED);
        } catch (IOException e) {
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    /**
     * Firstly, {@link UrlClean} will clean up the URL.
     * <p>
     * Secondly, {@link CheckAndReturnBasicData} will check whether the website is bookmarked by the user and the
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
     * @return {@link BasicWebDataDTO}
     */
    @UrlClean
    @CheckAndReturnBasicData
    public BasicWebDataDTO scrapeWebDataFromUrl(@Url String url, @Username String userName) throws IOException {
        Document document = Jsoup.parse(new URL(url), 3000);

        String title = document.title();
        String desc = document.body().text();
        String img = getFirstImg(document);

        return BasicWebDataDTO.builder()
                .title(title)
                .url(url)
                .img(img)
                .desc(desc)
                .build();
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
    public PopularBookmarksVO getPopularBookmarksAndTotalPages(PageInfoDTO pageInfo) {
        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();

        List<PopularBookmarkDTO> bookmarks = websiteMapper.getPopularPublicBookmarks(from, size);
        int totalCount = websiteMapper.countDistinctPublicUrl();
        int totalPages = PaginationUtils.getTotalPages(totalCount, size);
        return PopularBookmarksVO.builder().bookmarks(bookmarks).totalPages(totalPages).build();
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
                return getPublicIncludeCurrentPrivateExceptRequestedUserBookmark(
                        currentUsername, requestedUsername, from, size);
            case LATEST:
            default:
                // get all public bookmarks and current user's private bookmarks
                return getLatestBookmarks(currentUsername, from, size);
        }
    }

    private List<BookmarkVO> convertToBookmarkVO(List<WebsiteDO> websites) {
        return DozerUtils.convertList(websites, BookmarkVO.class);
    }

    private BookmarksAndTotalPagesVO getUserBookmarks(String username,
                                                      int from,
                                                      int size,
                                                      boolean shouldIncludePrivate) {

        int totalCounts = websiteMapper.countUserBookmarks(username, shouldIncludePrivate);
        int totalPages = PaginationUtils.getTotalPages(totalCounts, size);

        List<WebsiteDO> b = websiteMapper.getUserBookmarks(username, from, size, shouldIncludePrivate);
        List<BookmarkVO> bookmarks = convertToBookmarkVO(b);

        return BookmarksAndTotalPagesVO.builder().totalPages(totalPages).bookmarks(bookmarks).build();
    }

    private BookmarksAndTotalPagesVO getPublicIncludeCurrentPrivateExceptRequestedUserBookmark(
            String currentUsername, String requestedUsername, int from, int size) {

        List<BookmarkVO> bookmarks =
                getAllPublicSomePrivateExcludingSpecificUserBookmark(currentUsername, requestedUsername, from, size);

        int totalCount = websiteMapper
                .countAllPublicSomePrivateExcludingSpecificUserBookmark(currentUsername, requestedUsername);
        int totalPages = PaginationUtils.getTotalPages(totalCount, size);
        return BookmarksAndTotalPagesVO.builder().bookmarks(bookmarks).totalPages(totalPages).build();
    }

    /**
     * Get public bookmarks of all users and
     * some private bookmarks of the user whose username is {@code includePrivateUsername},
     * excluding the bookmarks of the user whose username is {@code excludeUsername}
     *
     * @param includePrivateUsername username of the user whose public and private bookmarks will be shown
     * @param excludeUsername        username of the user whose bookmarks will not be shown
     * @param from                   from
     * @param size                   size
     * @return bookmarks
     */
    private List<BookmarkVO> getAllPublicSomePrivateExcludingSpecificUserBookmark(
            String includePrivateUsername, String excludeUsername, int from, int size) {

        List<WebsiteDO> websites = websiteMapper.getAllPublicSomePrivateExcludingSpecificUserBookmark(
                includePrivateUsername, excludeUsername, from, size);

        return convertToBookmarkVO(websites);
    }

    private BookmarksAndTotalPagesVO getLatestBookmarks(String currentUsername, int from, int size) {
        List<BookmarkVO> bookmarks =
                getAllPublicAndSpecificPrivateBookmarks(from, size, currentUsername);

        int totalCount = websiteMapper.countAllPublicAndSpecificPrivateBookmarks(currentUsername);
        int totalPages = PaginationUtils.getTotalPages(totalCount, size);
        return BookmarksAndTotalPagesVO.builder().bookmarks(bookmarks).totalPages(totalPages).build();
    }

    /**
     * Get public bookmarks of all users and private bookmarks of specific user
     * <p>
     * The result will not be paginated if {@code from} or {@code size} is null
     * </p>
     *
     * @param from         from
     *                     <p>The result will not be paginated if {@code from} or {@code size} is null</p>
     * @param size         size
     *                     <p>The result will not be paginated if {@code from} or {@code size} is null</p>
     * @param specUsername username of the user whose public and private bookmarks will be shown
     * @return public bookmarks of all users and private bookmarks of specific user
     */
    private List<BookmarkVO> getAllPublicAndSpecificPrivateBookmarks(Integer from,
                                                                     Integer size,
                                                                     String specUsername) {
        List<WebsiteDO> websites = websiteMapper.getAllPublicAndSpecificPrivateBookmarks(from, size, specUsername);
        return convertToBookmarkVO(websites);
    }

    @Override
    public BookmarksAndTotalPagesVO getUserBookmarks(String username,
                                                     PageInfoDTO pageInfo,
                                                     Boolean shouldIncludePrivate) {
        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();
        boolean needPrivate = Optional.ofNullable(shouldIncludePrivate).orElse(false);
        return getUserBookmarks(username, from, size, needPrivate);
    }

    @Override
    @ModifyWebsitePermissionCheck
    public boolean deleteBookmark(@WebId Integer webId, @Username String userName) {
        // ID will not be null after checking by @ModifyWebsitePermissionCheck
        boolean success = websiteMapper.deleteBookmarkById(webId);
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
        // ID will not be null after checking by @ModifyWebsitePermissionCheck
        WebsiteDO bookmark = websiteMapper.getBookmarkById(webId);
        ThrowExceptionUtils.throwIfNull(bookmark, ResultCode.WEBSITE_DATA_NOT_EXISTS);

        boolean newPrivacy = !bookmark.getIsPublic();
        WebsiteDO webWithNewPrivacy = bookmark.setIsPublic(newPrivacy);
        return websiteMapper.updateBookmark(webWithNewPrivacy);
    }

    @Override
    public BookmarkVO getBookmark(int webId, String userName) {
        WebsiteDO bookmark = websiteMapper.getBookmarkById(webId);

        // data does not exist
        ThrowExceptionUtils.throwIfNull(bookmark, ResultCode.WEBSITE_DATA_NOT_EXISTS);

        // data is not public
        // and the owner's username of data does not match the username
        boolean noPermission = Boolean.FALSE.equals(bookmark.getIsPublic())
                && CompareStringUtil.notEqualsIgnoreCase(userName, bookmark.getUserName());
        ThrowExceptionUtils.throwIfTrue(noPermission, ResultCode.PERMISSION_DENIED);

        return DozerUtils.convert(bookmark, BookmarkVO.class);
    }

    @Override
    public void checkBookmarkExistsAndUserPermission(int webId, String username) {
        // username cannot be empty
        ThrowExceptionUtils.throwIfTrue(StringUtils.isEmpty(username), ResultCode.PERMISSION_DENIED);

        WebsiteDO bookmark = websiteMapper.getBookmarkById(webId);
        ThrowExceptionUtils.throwIfNull(bookmark, ResultCode.WEBSITE_DATA_NOT_EXISTS);

        Boolean isPublic = bookmark.getIsPublic();
        boolean isPrivate = BooleanUtils.isFalse(isPublic);

        String owner = bookmark.getUserName();

        boolean hasNoPermission = isPrivate && CompareStringUtil.notEqualsIgnoreCase(username, owner);
        ThrowExceptionUtils.throwIfTrue(hasNoPermission, ResultCode.PERMISSION_DENIED);
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

        boolean shouldIncludePrivate = username.equalsIgnoreCase(currentUsername);
        String html = getBookmarksByUserInHtml(username, shouldIncludePrivate);

        try {
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            response.getWriter().print(html);
        } catch (IOException e) {
            throw new ServiceException(ResultCode.CONNECTION_ERROR);
        }
    }

    private String getBookmarksByUserInHtml(String username, boolean shouldIncludePrivate) {

        List<BookmarkVO> bookmarks = getAllUserBookmarks(username, shouldIncludePrivate);

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

    private List<BookmarkVO> getAllUserBookmarks(String userName, boolean shouldIncludePrivate) {
        // from 和 size 为 null 的时候，表示不分页，直接获取全部
        List<WebsiteDO> bookmarks = websiteMapper.getUserBookmarks(userName, null, null, shouldIncludePrivate);
        return DozerUtils.convertList(bookmarks, BookmarkVO.class);
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
            BasicWebDataDTO basicWebData = getBasicWebDataFromElement(dt);
            bookmarkAndUpdateResult(username, result, basicWebData);
        });
    }

    private BasicWebDataDTO getBasicWebDataFromElement(Element dt) {

        BasicWebDataDTOBuilder webBuilder = BasicWebDataDTO.builder();

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

    private void bookmarkAndUpdateResult(String username, int[] result, BasicWebDataDTO web) {
        try {
            boolean success = bookmarkAndGetResult(username, web);
            updateImportingResult(result, success);
        } catch (ServiceException e) {
            ResultCode resultCode = e.getResultCode();
            updateImportingResult(result, e, resultCode);
        }
    }

    private boolean bookmarkAndGetResult(String username, BasicWebDataDTO web) {
        WebsiteServiceImpl websiteService =
                ApplicationContextUtils.getBean(WebsiteServiceImpl.class);
        // the imported bookmarks are public
        return websiteService.bookmarkWithBasicWebData(web, username, true);
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

    @Override
    @Cacheable(value = "bookmarks:visited")
    public List<VisitedBookmarksVO> getVisitedBookmarks(PageInfoDTO pageInfo) {
        int from = pageInfo.getFrom();
        int size = pageInfo.getSize();
        List<VisitedBookmarksVO> data = websiteMapper.getVisitedBookmarks(from, size);
        return DozerUtils.convertList(data, VisitedBookmarksVO.class);
    }
}