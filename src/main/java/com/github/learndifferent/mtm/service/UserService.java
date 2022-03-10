package com.github.learndifferent.mtm.service;

import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.dto.UserWithWebCountDTO;
import com.github.learndifferent.mtm.query.ChangePwdRequest;
import com.github.learndifferent.mtm.query.CreateUserRequest;
import java.util.List;

/**
 * UserService
 *
 * @author zhou
 * @date 2021/09/05
 */
public interface UserService {

    /**
     * Get all users' name and the number of websites that user owns, sorted by the number
     *
     * @return {@link List}<{@link UserWithWebCountDTO}>
     */
    List<UserWithWebCountDTO> getNamesAndCountTheirPublicWebs();

    /**
     * Get users' name and the number of websites that user owns, sorted by the number
     *
     * @param usernames users' name
     * @return {@link List}<{@link UserWithWebCountDTO}>
     */
    List<UserWithWebCountDTO> getNamesAndCountTheirPublicWebs(List<String> usernames);

    /**
     * Change password
     *
     * @param info username, old password and new password
     * @return true if success
     */
    boolean changePassword(ChangePwdRequest info);

    /**
     * Add a user with username, user role and not encrypted password
     *
     * @param usernameAndPassword username and not encrypted password
     * @param role                user role
     * @return true if success
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
     *                                                                  If user role is not found, the result will be
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#USER_ROLE_NOT_FOUND}.
     */
    boolean addUser(CreateUserRequest usernameAndPassword, String role);

    /**
     * Get user by name and password
     *
     * @param userName             username
     * @param notEncryptedPassword not encrypted password
     * @return user
     */
    UserDTO getUserByNameAndPwd(String userName, String notEncryptedPassword);

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
    UserDTO getUserByName(String userName);

    /**
     * Delete all data related to the user
     *
     * @param userName             username
     * @param notEncryptedPassword not encrypted password
     * @return false if deletion unsuccessful, which means the user does not exist
     * @throws com.github.learndifferent.mtm.exception.ServiceException If there is any mismatch while verifying user's
     *                                                                  name, password and permission to delete, it
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#USER_NOT_EXIST}
     *                                                                  or {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     */
    boolean deleteUserAndWebAndCommentData(String userName, String notEncryptedPassword);

    /**
     * Get all users. The content will be cached for 1 hour.
     *
     * @return {@link List}<{@link UserDTO}> users
     * @see com.github.learndifferent.mtm.config.RedisConfig
     */
    List<UserDTO> getAllUsersCaching();

    /**
     * Get all users and refresh the cache
     *
     * @return {@link List}<{@link UserDTO}> users
     */
    List<UserDTO> getAllUsersRefreshing();

    /**
     * A scheduled task to run {@link #getAllUsersRefreshing()} every hour on the hour automatically
     */
    void getAllUserRefreshingScheduledTask();
}
