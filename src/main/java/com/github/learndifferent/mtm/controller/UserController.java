package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.common.InvitationCode;
import com.github.learndifferent.mtm.annotation.common.InvitationCodeToken;
import com.github.learndifferent.mtm.annotation.common.UserRole;
import com.github.learndifferent.mtm.annotation.common.VerificationCode;
import com.github.learndifferent.mtm.annotation.common.VerificationCodeToken;
import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.annotation.validation.register.RegisterCodeCheck;
import com.github.learndifferent.mtm.annotation.validation.user.role.admin.AdminValidation;
import com.github.learndifferent.mtm.annotation.validation.user.role.guest.NotGuest;
import com.github.learndifferent.mtm.constant.enums.PageInfoParam;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.RoleType;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.query.ChangePasswordRequest;
import com.github.learndifferent.mtm.query.CreateUserRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.vo.UserBookmarkNumberVO;
import com.github.learndifferent.mtm.vo.UserVO;
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
 * User Controller
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
     * Get usernames of the users and the total numbers of their public bookmarks
     * sorted by the total number
     * <p>
     * Get all usernames in database if {@code usernames} is null or empty.
     * </p>
     *
     * @param usernames usernames of the requested users
     *                  <p>
     *                  get all usernames in database if {@code usernames} is null or empty
     *                  </p>
     * @return usernames of the users and the total number of their public bookmarks sorted by the total number
     */
    @GetMapping
    public List<UserBookmarkNumberVO> getNamesAndPublicBookmarkNums(
            @RequestParam(value = "usernames", required = false) List<String> usernames) {
        return userService.getNamesAndPublicBookmarkNums(usernames);
    }

    /**
     * Change Password
     *
     * @param passwordInfo username, old password and new password
     * @return {@link ResultVO} with the result code of {@link ResultCode#PASSWORD_CHANGED} or {@link ResultCode#UPDATE_FAILED}
     * @throws com.github.learndifferent.mtm.exception.ServiceException an exception with the result code of
     *                                                                  {@link ResultCode#PERMISSION_DENIED} will be
     *                                                                  thrown by the {@link NotGuest} annotation
     *                                                                  if the user is guest
     */
    @NotGuest
    @PostMapping("/change-password")
    public ResultVO<ResultCode> changePassword(@RequestBody ChangePasswordRequest passwordInfo) {

        boolean success = userService.changePassword(passwordInfo);

        return success ? ResultCreator.result(ResultCode.PASSWORD_CHANGED)
                : ResultCreator.result(ResultCode.UPDATE_FAILED);
    }

    /**
     * Create a user
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
     * @return {@link ResultCode#SUCCESS} if success. {@link ResultCode#FAILED} if failure.
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link RegisterCodeCheck} annotation will check
     *                                                                  the codes and {@link UserService#addUser(CreateUserRequest,
     *                                                                  String)} method will verify username, password
     *                                                                  and user role. If failed verification, they
     *                                                                  will throw an exception and the result code could be:
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
        return success ? ResultCreator.okResult() : ResultCreator.failResult();
    }

    /**
     * Delete a user and all of the data associated with that user
     *
     * @param userName The username of the user to be deleted
     * @param password The password that the user entered
     * @return The result code will be {@link ResultCode#SUCCESS} if success and {@link ResultCode#USER_NOT_EXIST} if failure
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link NotGuest} annotation will throw an
     *                                                                  exception with the result code of
     *                                                                  {@link ResultCode#PERMISSION_DENIED}
     *                                                                  if the user role is 'guest' for the reason
     *                                                                  that the 'guest' account can't be deleted.
     *                                                                  <p>
     *                                                                  {@link UserService#deleteUserAndAssociatedData(String,
     *                                                                  String, String)} method will throw an exception
     *                                                                  with the result code of {@link ResultCode#PERMISSION_DENIED}
     *                                                                  if the user that is currently logged in is not
     *                                                                  the user to delete, because only current user
     *                                                                  has permission to delete itself.
     *                                                                  </p>
     *                                                                  <p>
     *                                                                  The method will also verify user's name and
     *                                                                  password, and if there is any mismatch, it will
     *                                                                  throw an exception with the result code of
     *                                                                  {@link ResultCode#USER_NOT_EXIST}
     *                                                                  </p>
     */
    @DeleteMapping
    @NotGuest
    public ResultVO<ResultCode> deleteUser(@RequestParam("userName") String userName,
                                           @RequestParam("password") String password) {

        String currentUsername = StpUtil.getLoginIdAsString();
        boolean success = userService.deleteUserAndAssociatedData(currentUsername, userName, password);
        // Logout after deletion
        StpUtil.logout();
        return success ? ResultCreator.okResult() : ResultCreator.result(ResultCode.USER_NOT_EXIST);
    }

    /**
     * Change user role
     *
     * @param userId  ID of the user
     * @param newRole the new role of the user
     * @return Return {@link ResultCreator#okResult()} if success.
     * <p>Return {@link ResultVO} with the result code of {@link ResultCode#PERMISSION_DENIED}
     * if failure, or the user role is neither {@code admin} nor {@code user}</p>
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link AdminValidation} annotation
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  if the current user is not admin
     */
    @GetMapping("/role")
    @AdminValidation
    public ResultVO<ResultCode> changeUserRole(@RequestParam("userId") String userId,
                                               @RequestParam("newRole") String newRole) {

        boolean success = userService.changeUserRoleAndRecordChanges(userId, newRole);
        return success ? ResultCreator.okResult() : ResultCreator.result(ResultCode.PERMISSION_DENIED);
    }

    /**
     * Get users
     *
     * @param pageInfo Pagination information
     * @return users
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link AdminValidation} annotation
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  if the user is not admin
     */
    @GetMapping("/all")
    @AdminValidation
    public List<UserVO> getUsers(
            @PageInfo(size = 20, paramName = PageInfoParam.CURRENT_PAGE) PageInfoDTO pageInfo) {
        return userService.getUsers(pageInfo);
    }
}