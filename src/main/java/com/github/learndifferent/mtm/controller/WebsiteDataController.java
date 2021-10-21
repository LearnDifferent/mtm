package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.SaveWebDataResultDTO;
import com.github.learndifferent.mtm.dto.UserPublicWebInfoDTO;
import com.github.learndifferent.mtm.dto.WebWithNoIdentityDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.query.SaveNewWebDataRequest;
import com.github.learndifferent.mtm.query.SaveWebDataRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.WebsiteService;
import com.github.learndifferent.mtm.utils.DozerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
     * @throws ServiceException {@link WebsiteService#delWebsiteDataById(int, String)} will throw
     *                          an exception if the user does not have permission to delete, the result
     *                          code will be {@link ResultCode#PERMISSION_DENIED}
     */
    @DeleteMapping
    public ResultVO<?> deleteWebsiteData(@RequestParam("webId") Integer webId,
                                         @RequestParam("userName") String userName) {

        boolean success = websiteService.delWebsiteDataById(webId, userName);
        return success ? ResultCreator.result(ResultCode.DELETE_SUCCESS)
                : ResultCreator.result(ResultCode.DELETE_FAILED);
    }

    /**
     * Save Existing Public Website Data that has no Web ID, Username and Creation Time
     *
     * @param websiteData Request body of existing public website data that
     *                    has no web id, username and creation time,
     *                    which only contains title, url, image and description.
     * @param userName    User who saves the website data
     * @return {@code ResultVO<?>} Success or failure.
     * @throws ServiceException {@link WebsiteService#saveWebsiteData(WebWithNoIdentityDTO, String, boolean)}
     *                          will verify and throw exceptions if something goes wrong.
     *                          The Result Codes are: {@link ResultCode#ALREADY_MARKED}, {@link
     *                          ResultCode#PERMISSION_DENIED} and {@link ResultCode#URL_MALFORMED}
     */
    @PostMapping("/existing")
    public ResultVO<?> saveExistingPublicWebsiteData(@RequestBody SaveWebDataRequest websiteData,
                                                     @RequestParam("userName") String userName) {

        WebWithNoIdentityDTO website = DozerUtils.convert(websiteData, WebWithNoIdentityDTO.class);
        boolean success = websiteService.saveWebsiteData(website, userName, true);
        return success ? ResultCreator.okResult() : ResultCreator.failResult();
    }

    /**
     * Save New Website Data
     *
     * @param newWebsiteData URL, username, a boolean value named {@code isPublic} related to privacy settings
     *                       and a boolean value named {@code syncToElasticsearch} related to whether the data
     *                       will be synchronized to Elasticsearch or not
     * @return {@link ResultVO}<{@link SaveWebDataResultDTO}> The result of saving website data
     */
    @SystemLog(title = "Mark", optsType = OptsType.CREATE)
    @PostMapping("/new")
    public ResultVO<SaveWebDataResultDTO> saveNewWebsiteData(@RequestBody SaveNewWebDataRequest newWebsiteData) {
        SaveWebDataResultDTO results = websiteService.saveNewWebsiteData(newWebsiteData);
        return ResultCreator.okResult(results);
    }

    /**
     * Change the saved website privacy settings.
     * If the website is public, then make it private.
     * If the website is private, then make it public.
     *
     * @param webId    web id
     * @param userName name of user who trying to change the privacy settings
     * @return success or failure
     * @throws ServiceException {@link WebsiteService#changeWebPrivacySettings(int, String)}
     *                          will throw an exception with
     *                          {@link com.github.learndifferent.mtm.constant.enums.ResultCode#WEBSITE_DATA_NOT_EXISTS}
     *                          if the website data does not exists
     *                          and with {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                          if the user has no permission to change the website privacy settings.
     */
    @GetMapping
    public ResultVO<?> changeWebPrivacySettings(@RequestParam("webId") Integer webId,
                                                @RequestParam("userName") String userName) {
        boolean success = websiteService.changeWebPrivacySettings(webId, userName);
        return success ? ResultCreator.okResult() : ResultCreator.result(ResultCode.UPDATE_FAILED);
    }

    /**
     * Get website data by {@code webId}
     *
     * @param webId    web id
     * @param userName username
     * @return {@link ResultVO}<{@link WebsiteDTO}> If the user has no permission to get the website data,
     * or the website doesn't exists, then it will return {@code null}
     */
    @GetMapping("/get")
    public ResultVO<WebsiteDTO> getWebsiteDataById(@RequestParam("webId") Integer webId,
                                                   @RequestParam("userName") String userName) {
        WebsiteDTO web = websiteService.getWebsiteDataByIdAndCheckUsername(webId, userName);
        return web == null ? ResultCreator.failResult() : ResultCreator.okResult(web);
    }

    /**
     * Get paginated website data and total pages by username
     *
     * @param username username
     * @return {@link ResultVO}<{@link UserPublicWebInfoDTO}> Paginated public website data and total pages belonging to
     * the user, with the result code of {@link ResultCode#SUCCESS}
     */
    @GetMapping("/get/{username}")
    public ResultVO<UserPublicWebInfoDTO> getWebsiteDataInfoByUsername(@PathVariable("username") String username,
                                                                       @PageInfo(size = 8) PageInfoDTO pageInfo) {
        UserPublicWebInfoDTO info = websiteService.getUserPublicWebInfoDTO(username, pageInfo);
        return ResultCreator.okResult(info);
    }
}
