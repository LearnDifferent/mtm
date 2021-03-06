package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.constant.enums.UserRole;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.query.ChangePasswordRequest;
import com.github.learndifferent.mtm.query.UserIdentificationRequest;
import com.github.learndifferent.mtm.vo.UserBookmarkNumberVO;
import com.github.learndifferent.mtm.vo.UserVO;
import java.util.List;

/**
 * User Service
 *
 * @author zhou
 * @date 2021/09/05
 */
public interface UserService {

    /**
     * Get usernames of the users and the total numbers of their public bookmarks
     * sorted by the total number.
     * <p>
     * Get all usernames in database if {@code usernames} is null or empty.
     * </p>
     * <p>
     * This will also store the result in cache.
     * </p>
     *
     * @param usernames usernames of the requested users
     *                  <p>
     *                  get all usernames in database if {@code usernames} is null or empty
     *                  </p>
     * @return {@link List}<{@link UserBookmarkNumberVO}>
     */
    List<UserBookmarkNumberVO> getNamesAndPublicBookmarkNums(List<String> usernames);

    /**
     * Change password
     *
     * @param info username, old password and new password
     * @return true if success
     */
    boolean changePassword(ChangePasswordRequest info);

    /**
     * Add a user with username, user role and not encrypted password
     *
     * @param userIdentification Request body that contains username and password entered by the user
     * @param role               user role
     * @return Username of the user
     * @throws com.github.learndifferent.mtm.exception.ServiceException This method will verify and throw an exception
     *                                                                  if something goes wrong. If the username is
     *                                                                  already taken, the result will be {@link
     *                                                                  com.github.learndifferent.mtm.constant.enums.ResultCode#USER_ALREADY_EXIST}.
     *                                                                  If username contains not only letters and
     *                                                                  numbers, the result will be {@link
     *                                                                  com.github.learndifferent.mtm.constant.enums.ResultCode#USERNAME_ONLY_LETTERS_NUMBERS}.
     *                                                                  If username is empty, the result will be {@link
     *                                                                  com.github.learndifferent.mtm.constant.enums.ResultCode#USERNAME_EMPTY}.
     *                                                                  If username is not less than 30 characters, the
     *                                                                  result will be {@link
     *                                                                  com.github.learndifferent.mtm.constant.enums.ResultCode#USERNAME_TOO_LONG}.
     *                                                                  If password is empty, the result will be {@link
     *                                                                  com.github.learndifferent.mtm.constant.enums.ResultCode#PASSWORD_EMPTY}.
     *                                                                  If password is not less than 50 characters, the
     *                                                                  result will be {@link
     *                                                                  com.github.learndifferent.mtm.constant.enums.ResultCode#PASSWORD_TOO_LONG}.
     *                                                                  If password is not greater than 8 characters, the result code will be
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PASSWORD_TOO_SHORT}
     */
    String addUserAndGetUsername(UserIdentificationRequest userIdentification, UserRole role);

    /**
     * Get user role by username
     *
     * @param userName username
     * @return user role
     */
    String getRoleByName(String userName);

    /**
     * Get user by username
     *
     * @param userName username (ignore case)
     * @return user
     */
    UserVO getUserByName(String userName);

    /**
     * Delete a user and all of the data associated with that user
     *
     * @param currentUsername      The username of the user that is currently logged in
     * @param userName             The username of the user to be deleted
     * @param notEncryptedPassword The password that the user entered
     * @return true if success
     * @throws com.github.learndifferent.mtm.exception.ServiceException This method will throw an exception
     *                                                                  with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     *                                                                  if the user that is currently logged in is not
     *                                                                  the user to delete, because only current user
     *                                                                  has permission to delete itself.
     *                                                                  <p>
     *                                                                  This method will also verify user's name and
     *                                                                  password, and if there is any mismatch, it will
     *                                                                  throw an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#USER_NOT_EXIST}
     *                                                                  </p>
     */
    boolean deleteUserAndAssociatedData(String currentUsername, String userName, String notEncryptedPassword);

    /**
     * Get users.
     * <p>
     * If no users found, the empty result will be stored in the cache.
     * </p>
     *
     * @param pageInfo pagination information
     * @return {@link List}<{@link UserDTO}> users
     */
    List<UserVO> getUsers(PageInfoDTO pageInfo);

    /**
     * Change user role and record the changes
     *
     * @param userId  ID of the user
     * @param newRole the new role of the user
     * @return true if success
     * <p>false if failure, or the user role is neither {@code admin} nor {@code user}</p>
     */
    boolean changeUserRoleAndRecordChanges(String userId, String newRole);
}
