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
     * 获取所有用户的名称及其收藏的网页的个数，并按照网页个数排序
     *
     * @return 包装为只含有 userName 和 webCount 的 User 类列表
     */
    List<UserWithWebCountDTO> getNamesAndCountTheirPublicWebs();

    /**
     * 获取某些用户的名称及其收藏的网页的个数，并按照网页个数排序
     *
     * @param usernames 需要获取的用户名
     * @return 包装为只含有 userName 和 webCount 的 User 类列表
     */
    List<UserWithWebCountDTO> getNamesAndCountTheirPublicWebs(List<String> usernames);

    /**
     * 修改密码
     *
     * @param info username, old password and new password
     * @return 修改是否成功的信息
     */
    boolean changePassword(ChangePwdRequest info);

    /**
     * 传入用户名、未加密的密码和角色，生成用户，并调用添加用户的方法将用户添加到数据库。
     *
     * @param usernameAndPassword 用户名和未加密的密码
     * @param role                角色信息
     * @return 成功与否
     * @throws com.github.learndifferent.mtm.exception.ServiceException 在以下情况会抛出异常：
     *                                                                  <p>如果该用户名除了数字和英文字母外，还包含其他字符，就抛出异常。</p>
     *                                                                  <p>如果该用户已经存在，也会抛出用户已存在的异常。</p>
     *                                                                  <p>如果用户名大于 30 个字符，也会抛出异常。</p>
     *                                                                  <p>如果密码大于 50 个字符，也会抛出异常</p>
     *                                                                  <p>如果用户名或密码为空，抛出异常</p>
     *                                                                  <p>如果没有传入正确的用户角色，抛出异常</p>
     *                                                                  <p>Result Code 为：</p>
     *                                                                  <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#USER_ALREADY_EXIST}</p>
     *                                                                  <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#USERNAME_ONLY_LETTERS_NUMBERS}</p>
     *                                                                  <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#USERNAME_TOO_LONG}</p>
     *                                                                  <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#USERNAME_EMPTY}</p>
     *                                                                  <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#PASSWORD_TOO_LONG}</p>
     *                                                                  <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#PASSWORD_EMPTY}</p>
     *                                                                  <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#USER_ROLE_NOT_FOUND}</p>
     */
    boolean addUser(CreateUserRequest usernameAndPassword, String role);

    /**
     * 根据用户名和密码查找用户
     *
     * @param userName 用户名
     * @param password 密码
     * @return 用户
     */
    UserDTO getUserByNameAndPwd(String userName, String password);

    /**
     * 根据用户名获取用户角色
     *
     * @param userName 用户名
     * @return 用户角色
     */
    String getRoleByName(String userName);

    /**
     * 根据用户名获取用户（不区分大小写，也就是查询 abc 和 Abc 都能查到该用户）
     *
     * @param userName 用户名
     * @return 用户
     */
    UserDTO getUserByName(String userName);

    /**
     * Delete user as well as the comments and websites data belongs to the user.
     *
     * @param userName username
     * @param password password
     * @return boolean success to delete or not
     * @throws com.github.learndifferent.mtm.exception.ServiceException If there is any mismatch while verifying user's
     *                                                                  name, password and permission to delete, it
     *                                                                  will throw exceptions with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#USER_NOT_EXIST}
     *                                                                  or {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     */
    boolean deleteUserAndWebAndCommentData(String userName, String password);

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
