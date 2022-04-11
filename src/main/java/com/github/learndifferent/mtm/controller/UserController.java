package com.github.learndifferent.mtm.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.general.page.PageInfo;
import com.github.learndifferent.mtm.annotation.validation.user.role.admin.AdminValidation;
import com.github.learndifferent.mtm.annotation.validation.user.role.guest.NotGuest;
import com.github.learndifferent.mtm.constant.enums.PageInfoParam;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.UserRole;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.query.ChangePasswordRequest;
import com.github.learndifferent.mtm.query.UserIdentificationRequest;
import com.github.learndifferent.mtm.query.UsernamesRequest;
import com.github.learndifferent.mtm.response.ResultCreator;
import com.github.learndifferent.mtm.response.ResultVO;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.service.VerificationService;
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
    private final VerificationService verificationService;

    @Autowired
    public UserController(UserService userService,
                          VerificationService verificationService) {
        this.userService = userService;
        this.verificationService = verificationService;
    }

    /**
     * Create a user
     *
     * @param userIdentification Request body that contains username and password entered by the user
     * @param code               verification code
     * @param token              token for verification Code
     * @param role               user role (verify the invitation code if the user role is {@link UserRole#ADMIN})
     * @param invitationCode     invitation code (the value will be ignored if the user role is not {@link
     *                           UserRole#ADMIN})
     * @param invitationToken    token for invitation code
     *                           (the value will be ignored if the user role is not {@link UserRole#ADMIN})
     * @return {@link ResultCode#SUCCESS} if success. {@link ResultCode#FAILED} if failure.
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link VerificationService#checkRegisterCodes(String,
     *                                                                  String, UserRole, String, String)} will throw
     *                                                                  an
     *                                                                  exception with the result code of
     *                                                                  {@link ResultCode#VERIFICATION_CODE_FAILED} or
     *                                                                  {@link ResultCode#INVITATION_CODE_FAILED}
     *                                                                  if failed verification.
     *                                                                  <p>
     *                                                                  {@link UserService#addUser(UserIdentificationRequest,
     *                                                                  UserRole)} will verify username and
     *                                                                  password, and throw an exception with one of
     *                                                                  these result codes if failed verification:
     *                                                                  <li>{@link ResultCode#USER_ALREADY_EXIST}</li>
     *                                                                  <li>{@link ResultCode#USERNAME_ONLY_LETTERS_NUMBERS}</li>
     *                                                                  <li>{@link ResultCode#USERNAME_TOO_LONG}</li>
     *                                                                  <li>{@link ResultCode#USERNAME_EMPTY}</li>
     *                                                                  <li>{@link ResultCode#PASSWORD_TOO_LONG}</li>
     *                                                                  <li>{@link ResultCode#PASSWORD_EMPTY}</li>
     *                                                                  </p>
     */
    @PostMapping
    public ResultVO<ResultCode> createUser(@RequestBody UserIdentificationRequest userIdentification,
                                           @RequestParam("code") String code,
                                           @RequestParam("token") String token,
                                           @RequestParam("role") UserRole role,
                                           @RequestParam(value = "invitationCode", required = false)
                                                   String invitationCode,
                                           @RequestParam(value = "invitationToken", required = false)
                                                   String invitationToken) {

        verificationService.checkRegisterCodes(code, token, role, invitationCode, invitationToken);
        boolean success = userService.addUser(userIdentification, role);
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
     * Get users
     *
     * @param pageInfo Pagination information
     * @return users
     * @throws com.github.learndifferent.mtm.exception.ServiceException {@link AdminValidation} annotation
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  if the user is not admin
     */
    @GetMapping
    @AdminValidation
    public List<UserVO> getUsers(
            @PageInfo(size = 20, paramName = PageInfoParam.CURRENT_PAGE) PageInfoDTO pageInfo) {
        return userService.getUsers(pageInfo);
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
    @PostMapping("/usernames-and-bookmarks")
    public List<UserBookmarkNumberVO> getNamesAndPublicBookmarkNums(UsernamesRequest usernames) {
        return userService.getNamesAndPublicBookmarkNums(usernames.getUsernames());
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
}