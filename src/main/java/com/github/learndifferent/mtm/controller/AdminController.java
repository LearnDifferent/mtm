package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.validation.user.role.admin.AdminValidation;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.entity.SysLog;
import com.github.learndifferent.mtm.service.SystemLogService;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.vo.AdminPageVO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin Page Controller
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
     * Get logs, all users' information and whether the current user is admin for admin page
     *
     * @return {@link AdminPageVO} logs, all users' information and whether the current user is admin
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link AdminValidation} annotation
     *                                                                  will throw exception if the user is not admin.
     */
    @AdminValidation
    @GetMapping
    public AdminPageVO load() {

        List<SysLog> logs = logService.getSystemLogs();
        List<UserDTO> users = userService.getAllUsersCaching();
        return AdminPageVO.builder().admin(true).logs(logs).users(users).build();
    }
}
