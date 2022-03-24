package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.ViewCounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Increase and count views
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
        viewCounterService.increaseViews(webId);
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
}
