package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.common.InvitationCode;
import com.github.learndifferent.mtm.annotation.common.InvitationCodeToken;
import com.github.learndifferent.mtm.annotation.common.Password;
import com.github.learndifferent.mtm.annotation.common.UserRole;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.common.VerificationCode;
import com.github.learndifferent.mtm.annotation.common.VerificationCodeToken;
import com.github.learndifferent.mtm.annotation.general.log.SystemLog;
import com.github.learndifferent.mtm.annotation.validation.register.RegisterCodeCheck;
import com.github.learndifferent.mtm.annotation.validation.user.delete.DeleteUserCheck;
import com.github.learndifferent.mtm.annotation.validation.user.role.guest.NotGuest;
import com.github.learndifferent.mtm.constant.enums.OptsType;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.query.ChangePwdRequest;
import com.github.learndifferent.mtm.query.CreateUserRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Get, create, delete and update users
 *
 * @author zhou
 * @date 2021/09/05
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get Users
     *
     * @return {@code ResultVO<List<UserDTO>>} Users
     */
    @SystemLog(optsType = OptsType.READ)
    @GetMapping
    public ResultVO<List<UserDTO>> getUsers() {
        return ResultCreator.okResult(userService.getUsers());
    }

    /**
     * Change Password
     *
     * @param passwordInfo username, old password and new password
     * @return {@code ResultVO<?>} Success or failure
     */
    @NotGuest
    @PostMapping("/changePwd")
    public ResultVO<?> changePassword(@RequestBody ChangePwdRequest passwordInfo) {

        boolean success = userService.changePassword(passwordInfo);

        return success ? ResultCreator.result(ResultCode.PASSWORD_CHANGED)
                : ResultCreator.result(ResultCode.UPDATE_FAILED);
    }

    /**
     * Create User
     *
     * @param basicInfo       Username, Password and User Role
     * @param code            Verification Code
     * @param verifyToken     Token for Verification Code
     * @param role            User Role
     * @param invitationCode  Invitation Code
     * @param invitationToken Token for Invitation Code
     * @return {@code ResultVO<?>} If success, return {@link ResultCreator#okResult()}.
     * <p>If failure, return {@link ResultCreator#defaultFailResult()}</p>
     * @throws ServiceException {@link RegisterCodeCheck} will check the codes and
     *                          {@link UserService#addUser(CreateUserRequest)} will verify username and
     *                          password.
     *                          They will throw an exception if failed verification. The Result Codes are:
     *                          <p>{@link ResultCode#VERIFICATION_CODE_FAILED}</p>
     *                          <p>{@link ResultCode#INVITATION_CODE_FAILED}</p>
     *                          <p>{@link ResultCode#USER_ALREADY_EXIST}</p>
     *                          <p>{@link ResultCode#USERNAME_ONLY_LETTERS_NUMBERS}</p>
     *                          <p>{@link ResultCode#USERNAME_TOO_LONG}</p>
     *                          <p>{@link ResultCode#USERNAME_EMPTY}</p>
     *                          <p>{@link ResultCode#PASSWORD_TOO_LONG}</p>
     *                          <p>{@link ResultCode#PASSWORD_EMPTY}</p>
     */
    @PostMapping("/create")
    @RegisterCodeCheck
    public ResultVO<?> createUser(@RequestBody CreateUserRequest basicInfo,
                                  @VerificationCode String code,
                                  @VerificationCodeToken String verifyToken,
                                  @UserRole String role,
                                  @InvitationCode String invitationCode,
                                  @InvitationCodeToken String invitationToken) {

        boolean success = userService.addUser(basicInfo);

        return success ? ResultCreator.okResult()
                : ResultCreator.defaultFailResult();
    }

    /**
     * Delete user and his website data
     *
     * @param userName Username
     * @param password Password
     * @return {@code ResultVO<?>} Success or failure
     * @throws ServiceException {@link DeleteUserCheck} will verify user's name,
     *                          password and permission to delete. If there is any mismatch, it will throw exceptions.
     *                          The Result Codes are {@link ResultCode#USER_NOT_EXIST} and {@link
     *                          ResultCode#PERMISSION_DENIED}
     */
    @DeleteMapping
    @DeleteUserCheck
    public ResultVO<?> deleteUser(@RequestParam("userName") @Username String userName,
                                  @Password String password) {

        // Delete user and his website data
        boolean success = userService.deleteUserAndHisWebsiteData(userName);

        // Logout
        StpUtil.logout();

        return success ? ResultCreator.okResult() :
                ResultCreator.failResult("User does not exist.");
    }
}
