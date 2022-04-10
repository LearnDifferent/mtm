package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.annotation.validation.user.role.admin.AdminValidation;
import com.github.learndifferent.mtm.constant.enums.PageInfoParam;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.entity.SysLog;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.SystemLogService;
import com.github.learndifferent.mtm.service.VerificationService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin Controller
 *
 * @author zhou
 * @date 2021/09/05
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final SystemLogService logService;
    private final VerificationService verificationService;

    @Autowired
    public AdminController(SystemLogService logService,
                           VerificationService verificationService) {
        this.logService = logService;
        this.verificationService = verificationService;
    }

    /**
     * Check if the current user is an admin
     *
     * @return {@link ResultCode#SUCCESS} if the current user is admin
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link AdminValidation} annotation
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  if the user is not admin
     */
    @GetMapping
    @AdminValidation
    public ResultVO<ResultCode> checkAdmin() {
        return ResultCreator.okResult();
    }

    /**
     * Send invitation code
     *
     * @param token token for invitation code
     * @param email Email
     * @throws com.github.learndifferent.mtm.exception.ServiceException The invitation code will be assigned to the
     *                                                                  "data" field in a {@code ServiceException} when
     *                                                                  an email setting error occurs.
     *                                                                  And the {@code ServiceException} will be thrown
     *                                                                  with the result code of {@link ResultCode#EMAIL_SET_UP_ERROR}
     *                                                                  if there is an email setting error.
     */
    @GetMapping("/invitation")
    public void send(@RequestParam("token") String token,
                     @RequestParam("email") String email) {

        verificationService.sendInvitationCode(token, email);
    }

    /**
     * Get system logs from cache and database
     *
     * @param pageInfo pagination information
     * @return system logs
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link AdminValidation} annotation
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link ResultCode#PERMISSION_DENIED}
     *                                                                  if the user is not admin
     */
    @GetMapping("/logs")
    @AdminValidation
    public ResultVO<List<SysLog>> getSystemLogs(
            @PageInfo(size = 20, paramName = PageInfoParam.CURRENT_PAGE) PageInfoDTO pageInfo) {

        List<SysLog> logs = logService.getSystemLogs(pageInfo);
        return ResultCreator.okResult(logs);
    }

    /**
     * Get system logs from database directly
     *
     * @param pageInfo pagination information
     * @return system logs
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link AdminValidation} annotation
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link ResultCode#PERMISSION_DENIED}
     *                                                                  if the user is not admin
     */
    @GetMapping("/logs/no-cache")
    @AdminValidation
    public ResultVO<List<SysLog>> getSystemLogsFromDatabaseDirectly(
            @PageInfo(size = 20, paramName = PageInfoParam.CURRENT_PAGE) PageInfoDTO pageInfo) {

        List<SysLog> logs = logService.getSystemLogsFromDatabaseDirectly(pageInfo);
        return ResultCreator.okResult(logs);
    }
}