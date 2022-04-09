package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.annotation.validation.user.role.admin.AdminValidation;
import com.github.learndifferent.mtm.constant.enums.PageInfoParam;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.entity.SysLog;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.InvitationCodeService;
import com.github.learndifferent.mtm.service.SystemLogService;
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

    private final InvitationCodeService invitationCodeService;

    @Autowired
    public AdminController(SystemLogService logService,
                           InvitationCodeService invitationCodeService) {
        this.logService = logService;
        this.invitationCodeService = invitationCodeService;
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
     * @param invitationToken Token for invitation code
     * @param email           Email
     * @throws com.github.learndifferent.mtm.exception.ServiceException The invitation code will be assigned to the
     *                                                                  "data" field in a {@code ServiceException} when
     *                                                                  an email setting error occurs.
     *                                                                  And the {@code ServiceException} will be thrown
     *                                                                  with the result code of {@link ResultCode#EMAIL_SET_UP_ERROR}
     *                                                                  if there is an email setting error.
     */
    @GetMapping("/invitation")
    public void send(@RequestParam("invitationToken") String invitationToken,
                     @RequestParam("email") String email) {

        invitationCodeService.send(invitationToken, email);
    }

    /**
     * Get system logs
     *
     * @param isReadFromDb True if data is read from database directly.
     *                     <p>False or null if data is read from database and cache memory.</p>
     * @param pageInfo     Pagination information
     * @return system logs
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link AdminValidation} annotation
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link ResultCode#PERMISSION_DENIED}
     *                                                                  if the user is not admin
     */
    @GetMapping("/logs")
    @AdminValidation
    public ResultVO<List<SysLog>> getSystemLogs(
            @RequestParam(value = "isReadFromDb", required = false) Boolean isReadFromDb,
            @PageInfo(size = 20, paramName = PageInfoParam.CURRENT_PAGE) PageInfoDTO pageInfo) {

        List<SysLog> logs = logService.getSystemLogs(pageInfo, isReadFromDb);
        return ResultCreator.okResult(logs);
    }
}