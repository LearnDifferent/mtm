package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.validation.user.role.admin.AdminValidation;
import com.github.learndifferent.mtm.dto.VisitedBookmarksDTO;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.ViewCounterService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * View Counter Controller
 *
 * @author zhou
 * @date 2022/3/24
 */
@RestController
@RequestMapping("/view")
public class ViewCounterController {

    private final ViewCounterService viewCounterService;

    @Autowired
    public ViewCounterController(ViewCounterService viewCounterService) {
        this.viewCounterService = viewCounterService;
    }

    /**
     * Increase the number of views of a website data
     *
     * @param webId ID of the website data
     */
    @GetMapping("/incr")
    public void increaseViews(@RequestParam("webId") Integer webId) {
        viewCounterService.increaseViewsAndAddToSet(webId);
    }

    /**
     * Count the number of views of a website data
     *
     * @param webId ID of the website data
     * @return views
     */
    @GetMapping("/count")
    public ResultVO<Integer> countViews(@RequestParam("webId") Integer webId) {
        int views = viewCounterService.countViews(webId);
        return ResultCreator.okResult(views);
    }

    /**
     * Save the numbers of views to the database
     * and return a list of the keys that failed to save
     *
     * @return the list of the keys that failed to save
     * @throws com.github.learndifferent.mtm.exception.ServiceException an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#UPDATE_FAILED}
     *                                                                  will be thrown if no data available
     */
    @GetMapping("/update-db")
    public ResultVO<List<String>> saveViewsToDbAndReturnFailKeys() {
        List<String> failKeys = viewCounterService.saveViewsToDbAndReturnFailKeys();
        return ResultCreator.okResult(failKeys);
    }

    /**
     * Get all visited bookmarks from database
     *
     * @return all visited bookmarks
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link AdminValidation} annotation
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  if the user is not admin
     */
    @AdminValidation
    @GetMapping("/visited-bookmarks")
    public ResultVO<List<VisitedBookmarksDTO>> getAllVisitedBookmarks() {
        List<VisitedBookmarksDTO> bookmarks = viewCounterService.getAllVisitedBookmarks();
        return ResultCreator.okResult(bookmarks);
    }
}
