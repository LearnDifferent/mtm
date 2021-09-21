package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.WebId;
import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.annotation.validation.website.delete.DeleteWebsitePermission;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.manager.WebsiteManager;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.vo.NewWebVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Save and delete website data.
 *
 * @author zhou
 * @date 2021/09/05
 */

@RestController
@RequestMapping("/web")
public class WebsiteDataController {

    private final WebsiteService websiteService;
    private final WebsiteManager websiteManager;

    @Autowired
    public WebsiteDataController(WebsiteService websiteService,
                                 WebsiteManager websiteManager) {
        this.websiteService = websiteService;
        this.websiteManager = websiteManager;
    }

    /**
     * Delete website data by web id.
     * {@link DeleteWebsitePermission} will verify whether the user own this website data.
     * If not, it will an exception.
     *
     * @param webId    Website ID
     * @param userName Username
     * @return {@code ResultVO<?>} Success or failure.
     * @throws ServiceException If user does not have permission to delete
     *                          , the result code will be {@link ResultCode#PERMISSION_DENIED}
     */
    @DeleteMapping
    @DeleteWebsitePermission
    public ResultVO<?> deleteWebsiteData(@RequestParam("webId") @WebId int webId,
                                         @Username String userName) {

        boolean success = websiteService.delWebsiteDataById(webId);
        return success ? ResultCreator.result(ResultCode.DELETE_SUCCESS)
                : ResultCreator.result(ResultCode.DELETE_FAILED);
    }

    /**
     * Save Website Data that has no web id, username and creation time.
     *
     * @param website  Website Data that has no web id, username and creation time
     * @param userName User who saves the website data
     * @return {@code ResultVO<?>} Success or failure.
     * @throws ServiceException {@link WebsiteService#saveWebsiteData(WebWithNoIdentityDTO, String)} will check throw
     *                          exceptions. The Result Codes are: {@link ResultCode#ALREADY_MARKED}, {@link
     *                          ResultCode#PERMISSION_DENIED} and {@link ResultCode#URL_MALFORMED}
     */
    @PostMapping
    public ResultVO<?> saveWebsiteData(
            @RequestBody WebWithNoIdentityDTO website
            , @RequestParam("userName") String userName) {

        boolean success = websiteService.saveWebsiteData(website, userName);
        return success ? ResultCreator.okResult() : ResultCreator.failResult();
    }

    /**
     * Save New Website Data with {@link NewWebVO}.
     *
     * @param newWebVO URL, Username and the boolean that whether the data will be synchronized to Elasticsearch
     * @return {@code boolean[]} index 0 stores the result of saving data to database and index 1 stores the result of
     * saving data to Elasticsearch
     */
    @SystemLog(title = "Mark", optsType = OptsType.CREATE)
    @PostMapping("/add")
    public boolean[] saveWebsiteData(@RequestBody NewWebVO newWebVO) {
        return websiteManager.saveNewWebsiteData(newWebVO);
    }
}
