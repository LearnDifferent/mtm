package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.annotation.validation.register.RegisterCodeCheck;
import com.github.learndifferent.mtm.annotation.validation.user.delete.DeleteUserCheck;
import com.github.learndifferent.mtm.annotation.validation.user.role.guest.NotGuest;
import com.github.learndifferent.mtm.constant.consist.CodeConstant;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.manager.DeleteUserManager;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.vo.UserBasicInfoVO;
import com.github.learndifferent.mtm.vo.UserChangePwdVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用于注册、更新和删除用户
 *
 * @author zhou
 * @date 2021/09/05
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final DeleteUserManager deleteUserManager;

    @Autowired
    public UserController(UserService userService,
                          DeleteUserManager deleteUserManager) {
        this.userService = userService;
        this.deleteUserManager = deleteUserManager;
    }

    @SystemLog(optsType = OptsType.READ)
    @GetMapping
    public ResultVO<List<UserDTO>> getUsers() {
        return ResultCreator.okResult(userService.getUsers());
    }

    @NotGuest
    @PostMapping("/changePwd")
    public ResultVO<?> changePassword(@RequestBody UserChangePwdVO user) {

        boolean success = userService.changePassword(user);

        return success ? ResultCreator.result(ResultCode.PASSWORD_CHANGED)
                : ResultCreator.result(ResultCode.UPDATE_FAILED);
    }

    /**
     * 创建用户。
     * <p>@RegisterCodeCheck 注解会判断验证码和邀请码是否正确，并抛出相应异常。</p>
     * <p>UserService 接口的 addUserByBasicInfo 也判断用户信息是否合适并抛出相应异常</p>
     *
     * @param basicInfo 基本信息
     * @return {@code ResultVO<?>} 相应的状态码及信息
     * @throws ServiceException 验证码错误时的状态码为：ResultCode.VERIFICATION_CODE_FAILED，
     *                          邀请码错误的状态码为：ResultCode.INVITATION_CODE_FAILED。
     *                          用户信息出错时，错误代码分别为：
     *                          ResultCode.USER_ALREADY_EXIST、
     *                          ResultCode.USERNAME_ONLY_LETTERS_NUMBERS、
     *                          ResultCode.USERNAME_TOO_LONG、
     *                          ResultCode.USERNAME_EMPTY、
     *                          ResultCode.PASSWORD_TOO_LONG 和
     *                          ResultCode.PASSWORD_EMPTY
     */
    @RegisterCodeCheck(codeParamName = CodeConstant.CODE,
            verifyTokenParamName = CodeConstant.VERIFY_TOKEN,
            roleParamName = "role",
            invitationCodeParamName = CodeConstant.INVITATION_CODE)
    @PostMapping("/create")
    public ResultVO<?> createUser(@RequestBody UserBasicInfoVO basicInfo) {

        boolean success = userService.addUserByBasicInfo(basicInfo);

        return success ? ResultCreator.okResult()
                : ResultCreator.defaultFailResult();
    }

    /**
     * 删除用户及其收藏的网页数据。其中 @DeleteUserCheck 注解会检查用户是否存在，
     * 及是否是当前用户在删除当前用户，然后视情况抛出相应异常。
     *
     * @param userName 用户名
     * @return {@code ResultVO<?>} 响应状态码
     * @throws ServiceException ResultCode.USER_NOT_EXIST 和 ResultCode.PERMISSION_DENIED
     */
    @DeleteMapping
    @DeleteUserCheck(usernameParamName = "userName", passwordParamName = "password")
    public ResultVO<?> deleteUser(@RequestParam("userName") String userName) {

        // 删除用户及其收藏的网页
        boolean success = deleteUserManager.deleteUserAndHisWebsiteData(userName);

        // 退出登陆
        StpUtil.logout();

        return success ? ResultCreator.okResult() :
                ResultCreator.failResult("User does not exist.");
    }
}
