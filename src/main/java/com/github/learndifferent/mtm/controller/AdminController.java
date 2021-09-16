package com.github.learndifferent.mtm.controller;

import com.github.learndifferent.mtm.annotation.validation.user.role.admin.AdminValidation;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.manager.AsyncLogManager;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.vo.AdminPageVO;
import com.github.learndifferent.mtm.vo.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 获取管理员信息
 *
 * @author zhou
 * @date 2021/09/05
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final AsyncLogManager logManager;

    @Autowired
    public AdminController(UserService userService,
                           AsyncLogManager logManager) {
        this.userService = userService;
        this.logManager = logManager;
    }

    @AdminValidation
    @GetMapping
    public ResultVO<AdminPageVO> getLogsAndUsersAndIsAdminAndCode() {

        // @AdminValidation 注解：如果不是管理员，就抛出异常
        // 如果是管理员账户，就获取相应数据
        List<SysLog> logs = logManager.getLogs();
        List<UserDTO> users = userService.getUsers();
        AdminPageVO data = AdminPageVO.builder().admin(true).logs(logs).users(users).build();

        return ResultCreator.okResult(data);
    }
}
