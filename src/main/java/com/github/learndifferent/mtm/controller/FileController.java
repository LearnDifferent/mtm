package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.general.idempotency.IdempotencyCheck;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.BookmarkService;
import com.github.learndifferent.mtm.utils.LoginUtils;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class FileController {

    private final BookmarkService bookmarkService;

    /**
     * Export user's bookmarks to an HTML file.
     * <p>Export bookmarks belonging to the user that is currently logged in
     * if the username is missing.</p>
     *
     * @param userId   user ID of the user whose data is being exported.
     * @param response response
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link BookmarkService#exportBookmarksToHtmlFile(Long,
     *                                                                  long, HttpServletResponse)}
     *                                                                  will throw an exception if an IO Exception
     *                                                                  occurs. The Result Code is {@link
     *                                                                  ResultCode#CONNECTION_ERROR CONNECTION_ERROR}
     */
    @GetMapping
    @IdempotencyCheck
    public void export(@RequestParam(value = "userId", required = false) Long userId,
                       HttpServletResponse response) {
        long currentUserId = LoginUtils.getCurrentUserId();
        bookmarkService.exportBookmarksToHtmlFile(userId, currentUserId, response);
    }

    /**
     * Import an HTML file that contains bookmarks
     *
     * @param htmlFile a file that contains bookmarks in HTML format
     * @return the message of the result
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link BookmarkService#importBookmarksFromHtmlFile(MultipartFile,
     *                                                                  long)} will throw an exception with the
     *                                                                  result code of {@link ResultCode#HTML_FILE_NO_BOOKMARKS
     *                                                                  HTML_FILE_NO_BOOKMARKS} if it's not a valid
     *                                                                  HTML file that contains bookmarks
     */
    @PostMapping
    @IdempotencyCheck
    public ResultVO<String> importFile(@RequestBody MultipartFile htmlFile) {
        if (Objects.isNull(htmlFile)) {
            return ResultCreator.result(ResultCode.HTML_FILE_NO_BOOKMARKS);
        }

        long currentUserId = LoginUtils.getCurrentUserId();
        String msg = bookmarkService.importBookmarksFromHtmlFile(htmlFile, currentUserId);
        return ResultCreator.okResult(msg);
    }
}