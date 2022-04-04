package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.WebsiteService;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
public class FileController {

    private final WebsiteService websiteService;

    @Autowired
    public FileController(WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

    /**
     * Export user's bookmarks to a HTML file.
     * <p>Export bookmarks belonging to the user that is currently logged in
     * if the username is missing.</p>
     *
     * @param username username of the user whose data is being exported.
     * @param response response
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link WebsiteService#exportBookmarksToHtmlFile(String,
     *                                                                  String, HttpServletResponse)}
     *                                                                  will throw an exception if an IO Exception
     *                                                                  occurs. The Result Code is {@link
     *                                                                  com.github.learndifferent.mtm.constant.enums.ResultCode#CONNECTION_ERROR
     *                                                                  CONNECTION_ERROR}
     */
    @GetMapping
    public void export(@RequestParam(value = "username", required = false) String username,
                       HttpServletResponse response) {
        String currentUsername = getCurrentUser();
        websiteService.exportBookmarksToHtmlFile(username, currentUsername, response);
    }

    /**
     * Import a HTML file that contains bookmarks
     *
     * @param htmlFile a file that contains bookmarks in HTML format
     * @return the message of the result
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link WebsiteService#importBookmarksFromHtmlFile(MultipartFile,
     *                                                                  String)} will throw an exception with the
     *                                                                  result code of {@link com.github.learndifferent.mtm.constant.enums.ResultCode#HTML_FILE_NO_BOOKMARKS
     *                                                                  HTML_FILE_NO_BOOKMARKS} if it's not a valid
     *                                                                  HTML file that contains bookmarks
     */
    @PostMapping
    public ResultVO<String> importFile(@RequestBody MultipartFile htmlFile) {
        String currentUser = getCurrentUser();
        String msg = websiteService.importBookmarksFromHtmlFile(htmlFile, currentUser);
        return ResultCreator.okResult(msg);
    }

    private String getCurrentUser() {
        return StpUtil.getLoginIdAsString();
    }
}
