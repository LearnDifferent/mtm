package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.manager.HtmlFileManager;
import com.github.learndifferent.mtm.response.ResultVO;
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
 * html 文件相关
 *
 * @author zhou
 * @date 2021/09/12
 */
@RestController
@RequestMapping("/file")
public class HtmlFileController {

    private final HtmlFileManager htmlFileManager;

    @Autowired
    public HtmlFileController(HtmlFileManager htmlFileManager) {
        this.htmlFileManager = htmlFileManager;
    }

    /**
     * 以 HTML 格式，导出该 username 的用户收藏的所有的网页的数据。
     * 如果该用户没有数据，直接输出无数据的提示。
     *
     * @param username 用户名
     * @param response response
     * @throws ServiceException 可能会有 IO 异常，状态码为 ResultCode.CONNECTION_ERROR
     */
    @GetMapping
    public void export(@RequestParam(value = "username", required = false) String username,
                       HttpServletResponse response) {

        if (StringUtils.isEmpty(username)) {
            username = getCurrentUser();
        }
        htmlFileManager.exportWebsDataByUserToHtmlFile(username, response);
    }

    @PostMapping
    public ResultVO<String> importFile(@RequestBody MultipartFile htmlFile) {
        String currentUser = getCurrentUser();
        return htmlFileManager.importWebsDataFromHtmlFile(htmlFile, currentUser);
    }

    private String getCurrentUser() {
        return (String) StpUtil.getLoginId();
    }
}
