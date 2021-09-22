package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.WebId;
import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.annotation.validation.website.delete.DeleteWebsitePermission;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.query.SaveNewWebDataRequest;
import com.github.learndifferent.mtm.query.SaveWebDataRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.utils.DozerUtils;
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

    @Autowired
    public WebsiteDataController(WebsiteService websiteService) {
        this.websiteService = websiteService;
    }

    /**
     * Delete website data by Web ID.
     *
     * @param webId    Website ID
     * @param userName Username
     * @return {@code ResultVO<?>} Success or failure.
     * @throws ServiceException {@link DeleteWebsitePermission} will verify whether the user own this website data.
     *                          It will throw an exception if the user does not have permission to delete, the result
     *                          code will be {@link ResultCode#PERMISSION_DENIED}
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
     * Save Website Data that has no Web ID, Username and Creation Time
     *
     * @param websiteData Request body of existing website data that has no web id, username and creation time,
     *                    which only contains title, url, image and description.
     * @param userName    User who saves the website data
     * @return {@code ResultVO<?>} Success or failure.
     * @throws ServiceException {@link WebsiteService#saveWebsiteData(WebWithNoIdentityDTO, String)}
     *                          will verify and throw exceptions if something goes wrong.
     *                          The Result Codes are: {@link ResultCode#ALREADY_MARKED}, {@link
     *                          ResultCode#PERMISSION_DENIED} and {@link ResultCode#URL_MALFORMED}
     */
    @PostMapping
    public ResultVO<?> saveWebsiteData(@RequestBody SaveWebDataRequest websiteData,
                                       @RequestParam("userName") String userName) {

        WebWithNoIdentityDTO website = DozerUtils.convert(websiteData, WebWithNoIdentityDTO.class);
        boolean success = websiteService.saveWebsiteData(website, userName);
        return success ? ResultCreator.okResult() : ResultCreator.failResult();
    }

    /**
     * Save New Website Data with the existing data
     *
     * @param newWebsiteData URL, username and a boolean value related to
     *                       whether the data will be synchronized to Elasticsearch or not
     * @return {@code boolean[]} The boolean array that the first element stores true or false
     * depending on whether or not the data was successfully saved to Database and
     * the second element stores true if Elasticsearch saved the data and false otherwise
     */
    @SystemLog(title = "Mark", optsType = OptsType.CREATE)
    @PostMapping("/add")
    public boolean[] saveWebsiteData(@RequestBody SaveNewWebDataRequest newWebsiteData) {
        return websiteService.saveNewWebsiteData(newWebsiteData);
    }
}
