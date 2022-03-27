package com.github.learndifferent.mtm.mapper;

import com.github.learndifferent.mtm.dto.UserWithWebCountDTO;
import com.github.learndifferent.mtm.entity.UserDO;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * UserMapper
 *
 * @author zhou
 * @date 2021/09/05
 */
@Repository
public interface UserMapper {

    /**
     * Get user names and the number of public website data that user owns, sorted by the number.
     *
     * @param usernames user names.
     *                  If the {@code usernames} is empty, return all users' data.
     * @return {@link List}<{@link UserWithWebCountDTO}>
     */
    List<UserWithWebCountDTO> getNamesAndCountTheirPublicWebs(List<String> usernames);

    /**
     * Get users
     *
     * @param from from (null if get all)
     * @param size size (null if get all)
     * @return {@link List}<{@link UserDO}> users
     */
    List<UserDO> getUsers(@Param("from") Integer from, @Param("size") Integer size);

    /**
     * Count the number of users
     *
     * @return number of users
     */
    int countUsers();

    /**
     * Add new user
     *
     * @param user new user
     * @return true if success
     */
    boolean addUser(UserDO user);

    /**
     * Find user by username and password
     *
     * @param userName username
     * @param password password
     * @return user
     */
    UserDO getUserByNameAndPwd(@Param("userName") String userName,
                               @Param("password") String password);

    /**
     * Get user role by username
     *
     * @param userName username
     * @return user role
     */
    String getRoleByName(String userName);

    /**
     * Get user role by user ID
     *
     * @param userId user ID
     * @return user role
     */
    String getUserRoleById(String userId);

    /**
     * Get user by username
     *
     * @param userName username
     * @return user
     */
    UserDO getUserByName(String userName);

    /**
     * Get user id by username and password
     *
     * @param userName username
     * @param password password
     * @return user id or null if not exists
     */
    String getUserIdByNameAndPassword(@Param("userName") String userName,
                                      @Param("password") String password);

    /**
     * Delete user by user id
     *
     * @param userId user id
     * @return success or failure
     */
    boolean deleteUserByUserId(String userId);

    /**
     * Update user
     *
     * @param user user data to update
     * @return true if success
     */
    boolean updateUser(UserDO user);

    /**
     * Get User ID by Username
     *
     * @param username username
     * @return User ID or null if there is no user with that username
     */
    String getUserIdByName(String username);
}
