package com.github.learndifferent.mtm.mapper;

import com.github.learndifferent.mtm.dto.UserWithWebCountDTO;
import com.github.learndifferent.mtm.entity.UserDO;
import java.util.List;
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
     * 获取某些用户的名称及其收藏的网页的个数，并按照网页个数排序
     * <p>如果传入的 usernames 列表为空，表示：获取所有用户的名称及其收藏的网页的个数，并按照网页个数排序。</p>
     *
     * @param usernames 需要获取的用户名
     * @return 包装为只含有 userName 和 webCount 的 User 类列表
     */
    List<UserWithWebCountDTO> getNamesAndCountMarkedWebsDesc(List<String> usernames);

    /**
     * 获取全部用户
     *
     * @return 全部用户列表
     */
    List<UserDO> getUsers();

    /**
     * 添加用户
     *
     * @param user 被添加的用户
     * @return 成功返回 true
     */
    boolean addUser(UserDO user);

    /**
     * 根据用户名和密码查找用户
     *
     * @param userName 用户名
     * @param password 密码
     * @return 用户
     */
    UserDO getUserByNameAndPwd(String userName, String password);

    /**
     * 根据用户名获取用户角色
     *
     * @param userName 用户名
     * @return 用户角色
     */
    String getRoleByName(String userName);

    /**
     * 根据用户 ID，获取用户角色
     *
     * @param userId 用户 ID
     * @return 用户角色
     */
    String getUserRoleById(String userId);


    /**
     * 根据 ID 获取用户
     *
     * @param userId 用户 ID
     * @return 用户
     */
    UserDO getUserById(String userId);

    /**
     * 根据用户名获取用户
     *
     * @param userName 用户名
     * @return 用户
     */
    UserDO getUserByName(String userName);

    /**
     * 根据用户名删除用户
     *
     * @param userName 用户名
     * @return 是否成功删除
     */
    boolean deleteUserByName(String userName);

    /**
     * 更新用户数据
     *
     * @param user 用户数据
     * @return 是否更新成功
     */
    boolean updateUser(UserDO user);
}
