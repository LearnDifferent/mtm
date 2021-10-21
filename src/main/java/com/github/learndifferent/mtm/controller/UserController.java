package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.common.InvitationCode;
import com.github.learndifferent.mtm.annotation.common.InvitationCodeToken;
import com.github.learndifferent.mtm.annotation.common.UserRole;
import com.github.learndifferent.mtm.annotation.common.VerificationCode;
import com.github.learndifferent.mtm.annotation.common.VerificationCodeToken;
import com.github.learndifferent.mtm.annotation.validation.register.RegisterCodeCheck;
import com.github.learndifferent.mtm.annotation.validation.user.role.guest.NotGuest;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.RoleType;
import com.github.learndifferent.mtm.dto.UserDTO;
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
     * Get users and refresh the cache
     *
     * @return {@code ResultVO<List<UserDTO>>} Users
     */
    @GetMapping
    public ResultVO<List<UserDTO>> getUsers() {
        return ResultCreator.okResult(userService.getAllUsersRefreshing());
    }

    /**
     * Change Password
     *
     * @param passwordInfo username, old password and new password
     * @return {@link ResultCode#PASSWORD_CHANGED} or {@link ResultCode#UPDATE_FAILED}
     */
    @NotGuest
    @PostMapping("/changePwd")
    public ResultVO<ResultCode> changePassword(@RequestBody ChangePwdRequest passwordInfo) {

        boolean success = userService.changePassword(passwordInfo);

        return success ? ResultCreator.result(ResultCode.PASSWORD_CHANGED)
                : ResultCreator.result(ResultCode.UPDATE_FAILED);
    }

    /**
     * Create User
     *
     * @param userInfo        Username and Password
     * @param role            User Role: The value will be {@link UserRole#defaultRole()} when the request parameter is
     *                        not provided or the role value does not match any value of {@link RoleType}, setting by
     *                        {@link RegisterCodeCheck} annotation.
     *                        If the user role is {@code admin}, {@link RegisterCodeCheck} annotation will
     *                        verify the invitation code and the token for invitation code.
     * @param code            Verification Code: {@link RegisterCodeCheck} annotation will use the value of it for
     *                        verification
     * @param verifyToken     Token for Verification Code: {@link RegisterCodeCheck} annotation will use the value of it
     *                        for verification
     * @param invitationCode  Invitation Code: If the user role is not {@code admin}, then the value will be ignored.
     *                        <p>{@link RegisterCodeCheck} annotation will use the value of it for verification if
     *                        necessary.</p>
     * @param invitationToken Token for Invitation Code: If the user role is not {@code admin}, then the value will be
     *                        ignored.
     *                        <p>{@link RegisterCodeCheck} annotation will use the value of it for verification if
     *                        necessary.</p>
     * @return If success, return {@link ResultCreator#okResult()}. If failure, return {@link
     * ResultCreator#defaultFailResult()}
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link RegisterCodeCheck} annotation will check
     *                                                                  the codes and {@link UserService#addUser(CreateUserRequest,
     *                                                                  String)} method will verify username, password
     *                                                                  and user role. If failed verification, they
     *                                                                  will throw exception and the result codes are:
     *                                                                  <p>{@link ResultCode#VERIFICATION_CODE_FAILED}</p>
     *                                                                  <p>{@link ResultCode#INVITATION_CODE_FAILED}</p>
     *                                                                  <p>{@link ResultCode#USER_ALREADY_EXIST}</p>
     *                                                                  <p>{@link ResultCode#USERNAME_ONLY_LETTERS_NUMBERS}</p>
     *                                                                  <p>{@link ResultCode#USERNAME_TOO_LONG}</p>
     *                                                                  <p>{@link ResultCode#USERNAME_EMPTY}</p>
     *                                                                  <p>{@link ResultCode#PASSWORD_TOO_LONG}</p>
     *                                                                  <p>{@link ResultCode#PASSWORD_EMPTY}</p>
     *                                                                  <p>{@link ResultCode#USER_ROLE_NOT_FOUND}</p>
     */
    @PostMapping("/create")
    @RegisterCodeCheck
    public ResultVO<ResultCode> createUser(@RequestBody CreateUserRequest userInfo,
                                           @UserRole(defaultRole = RoleType.USER) String role,
                                           @VerificationCode String code,
                                           @VerificationCodeToken String verifyToken,
                                           @InvitationCode(required = false) String invitationCode,
                                           @InvitationCodeToken(required = false) String invitationToken) {

        boolean success = userService.addUser(userInfo, role);

        return success ? ResultCreator.okResult()
                : ResultCreator.defaultFailResult();
    }

    /**
     * Delete user as well as his comments and website data
     *
     * @param userName Username
     * @param password Password
     * @return Success or failure
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link UserService#deleteUserAndWebAndCommentData(String,
     *                                                                  String)} will verify user's name, password and
     *                                                                  permission to delete. If there is any mismatch,
     *                                                                  it will throw exceptions.
     *                                                                  The result code will be {@link ResultCode#USER_NOT_EXIST}
     *                                                                  or {@link ResultCode#PERMISSION_DENIED}
     */
    @DeleteMapping
    public ResultVO<String> deleteUser(@RequestParam("userName") String userName,
                                       @RequestParam("password") String password) {

        boolean success = userService.deleteUserAndWebAndCommentData(userName, password);

        // Logout
        StpUtil.logout();

        return success ? ResultCreator.okResult() : ResultCreator.failResult("User does not exist.");
    }
}
