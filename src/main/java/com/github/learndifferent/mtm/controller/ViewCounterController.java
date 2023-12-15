package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck;
import com.github.learndifferent.mtm.annotation.validation.AccessPermissionCheck.DataAccessType;
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
     * Increase the number of views of a bookmark
     *
     * @param bookmarkId ID of the bookmark
     */
    @GetMapping
    public void increaseViews(@RequestParam("bookmarkId") Long bookmarkId) {
        viewCounterService.increaseViewsAndAddToSet(bookmarkId);
    }

    /**
     * Count the number of views of a bookmark
     *
     * @param bookmarkId ID of the bookmark
     * @return views
     */
    @GetMapping("/count")
    public ResultVO<Integer> countViews(@RequestParam("bookmarkId") Long bookmarkId) {
        int views = viewCounterService.countViews(bookmarkId);
        return ResultCreator.okResult(views);
    }

    /**
     * Save the numbers of views from Redis to the database,
     * or add the view data from database to Redis if the Redis has no view data
     *
     * @return Return a list of keys that failed to save
     * @throws com.github.learndifferent.mtm.exception.ServiceException This will throw an exception with the result
     *                                                                  code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED
     *                                                                  PERMISSION_DENIED} if the user is not admin
     */
    @GetMapping("/update")
    @AccessPermissionCheck(dataAccessType = DataAccessType.IS_ADMIN)
    public ResultVO<List<String>> updateViews() {
        List<String> failKeys = viewCounterService.updateViewsAndReturnFailKeys();
        return ResultCreator.okResult(failKeys);
    }
}