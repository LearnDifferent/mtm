package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.validation.user.role.admin.AdminValidation;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.SystemLogService;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.vo.AdminPageVO;
import com.github.learndifferent.mtm.vo.SysLog;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Get Info of Admin Page
 *
 * @author zhou
 * @date 2021/09/05
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final SystemLogService logService;

    @Autowired
    public AdminController(UserService userService,
                           SystemLogService logService) {
        this.userService = userService;
        this.logService = logService;
    }

    /**
     * Get logs, users' information and whether the current user is admin for admin page.
     *
     * @return {@link ResultVO}<{@link AdminPageVO}> information
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link AdminValidation}
     *                                                                  will throw exception if the user is not admin.
     */
    @AdminValidation
    @GetMapping
    public ResultVO<AdminPageVO> getAdminPageInfo() {

        List<SysLog> logs = logService.getLogs();
        List<UserDTO> users = userService.getUsers();
        AdminPageVO data = AdminPageVO.builder().admin(true).logs(logs).users(users).build();

        return ResultCreator.okResult(data);
    }
}
