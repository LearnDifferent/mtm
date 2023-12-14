package com.github.learndifferent.mtm.mapper;

import com.github.learndifferent.mtm.dto.UserBookmarkRankingByRoleDTO;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.dto.UserIdAndUsernameDTO;
import com.github.learndifferent.mtm.dto.search.UserForSearchWithMoreInfo;
import com.github.learndifferent.mtm.entity.UserDO;
import com.github.learndifferent.mtm.vo.UserBookmarkNumberVO;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * User Mapper
 *
 * @author zhou
 * @date 2021/09/05
 */
@Repository
public interface UserMapper {

    /**
     * Get the ranking of the number of bookmarks for each role
     *
     * @return the ranking of the number of bookmarks for each role
     */
    List<UserBookmarkRankingByRoleDTO> getRankingBookmarkNumByRole();

    /**
     * Get usernames of the users and the total numbers of their public bookmarks
     * sorted by the total number.
     *
     * @param usernames usernames of the requested users
     *                  <p>
     *                  get all usernames in database if null or emtpy
     *                  </p>
     * @return {@link List}<{@link UserBookmarkNumberVO}>
     */
    List<UserBookmarkNumberVO> getNamesAndPublicBookmarkNums(List<String> usernames);

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
    boolean addUser(UserDTO user);

    /**
     * Get user by username and password
     *
     * @param userName username
     * @param password password
     * @return user
     */
    UserDO getUserByNameAndPassword(@Param("userName") String userName,
                                    @Param("password") String password);

    /**
     * Get user role
     *
     * @param userId User ID
     * @return user role
     */
    String getRoleByUserId(long userId);

    /**
     * Get user role by ID
     *
     * @param id ID
     * @return user role
     */
    String getUserRoleById(long id);

    /**
     * Get user by username
     *
     * @param userName username
     * @return user
     */
    UserDO getUserByName(String userName);

    /**
     * Get ID by username and password
     *
     * @param userName username
     * @param password password
     * @return user ID or null if not exists
     */
    Long getUserIdByNameAndPassword(@Param("userName") String userName,
                                    @Param("password") String password);

    /**
     * Delete user by user ID
     *
     * @param id ID
     * @return true if success
     */
    boolean deleteUserByUserId(long id);

    /**
     * Update user
     *
     * @param user user data to update
     * @return true if success
     */
    boolean updateUser(UserDTO user);

    /**
     * Get User ID by Username
     *
     * @param username username
     * @return User ID or null if there is no user with that username
     */
    Long getUserIdByUsername(String username);

    /**
     * Search user data by keyword
     *
     * @param keyword keyword
     * @param from    from
     * @param size    size
     * @return user data
     */
    List<UserForSearchWithMoreInfo> searchUserDataByKeyword(@Param("keyword") String keyword,
                                                            @Param("from") int from,
                                                            @Param("size") int size);

    /**
     * Count the number of users by keyword
     *
     * @param keyword keyword
     * @return the number of users
     */
    Long countUserByKeyword(String keyword);

    /**
     * Check if the username is present
     *
     * @param username username
     * @return true if the username exists (including the case of deleted user)
     */
    boolean checkIfUsernamePresent(String username);

    /**
     * Retrieves a map of user IDs and corresponding usernames for the given set of user IDs.
     * <p>
     * The {@code @MapKey("userId")} annotation indicates that
     * the {@code userId} field of the {@link UserIdAndUsernameDTO} class should be used
     * as the key in the resulting map
     * </p>
     *
     * @param userIds The set of user IDs
     * @return A map with user IDs as keys and {@link UserIdAndUsernameDTO} objects as values
     */
    @MapKey("userId")
    Map<Long, UserIdAndUsernameDTO> getUserIdAndUsernameMap(@Param("userIds") Set<Long> userIds);
}