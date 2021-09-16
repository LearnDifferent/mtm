package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.constant.consist.HtmlFileConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.WebsiteService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * html 格式数据的生成、导出和导入
 *
 * @author zhou
 * @date 2021/09/12
 */
@Slf4j
@Component
public class HtmlFileManager {

    private final WebsiteService websiteService;

    @Autowired
    public HtmlFileManager(WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

    public ResultVO<String> importWebsDataFromHtmlFile(MultipartFile htmlFile, String username) {
        // [Success][Failure][Already Exists]
        int[] result = new int[3];

        try (InputStream in = htmlFile.getInputStream()) {

            Document document = Jsoup.parse(in, "UTF-8", "");
            Elements dts = document.getElementsByTag("dt");

            dts.forEach(dt -> {
                WebWithNoIdentityDTO web = getWebFromElement(dt);
                try {
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
        }

        boolean hasData =
                result[0] + result[1] + result[2] != 0;

        if (hasData) {
            String responseMsg = "Success: " + result[0]
                    + ", Failure: " + result[1]
                    + ", Already Exists: " + result[2];
            return ResultCreator.okResult(responseMsg);
        }

        return ResultCreator.failResult("No Data Available. Please Upload the Correct HTML file.");
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

    /**
     * 以 HTML 格式，导出该用户的所有网页数据。如果该用户没有数据，直接输出无数据的提示。
     *
     * @param username 用户名
     * @param response response
     * @throws ServiceException ResultCode.CONNECTION_ERROR
     */
    public void exportWebsDataByUserToHtmlFile(String username,
                                               HttpServletResponse response) {

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

        List<WebsiteDTO> webs = websiteService.findWebsitesDataByUser(username);

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
}
