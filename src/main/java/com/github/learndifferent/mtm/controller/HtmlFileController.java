package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.WebsiteService;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Import and export HTML file
 *
 * @author zhou
 * @date 2021/09/12
 */
@RestController
@RequestMapping("/file")
public class HtmlFileController {

    private final WebsiteService websiteService;

    @Autowired
    public HtmlFileController(WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

    /**
     * Export user's website data to a HTML file.
     * (The file will contains a hint if the user has no data)
     *
     * @param username username
     * @param response response
     * @throws ServiceException {@link WebsiteService#exportWebsDataByUserToHtmlFile(String, HttpServletResponse)}
     *                          will throw an exception if an IO Exception occurs. The Result Code is {@link
     *                          com.github.learndifferent.mtm.constant.enums.ResultCode#CONNECTION_ERROR}
     */
    @GetMapping
    public void export(@RequestParam(value = "username", required = false) String username,
                       HttpServletResponse response) {

        if (StringUtils.isEmpty(username)) {
            username = getCurrentUser();
        }
        websiteService.exportWebsDataByUserToHtmlFile(username, response);
    }

    /**
     * Import a HTML file that contains website data.
     *
     * @param htmlFile HTML File
     * @return {@link ResultVO}<{@link String}> Result message
     */
    @PostMapping
    public ResultVO<String> importFile(@RequestBody MultipartFile htmlFile) {
        String currentUser = getCurrentUser();
        return websiteService.importWebsDataFromHtmlFile(htmlFile, currentUser);
    }

    private String getCurrentUser() {
        return (String) StpUtil.getLoginId();
    }
}
