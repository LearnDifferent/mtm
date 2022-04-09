package com.github.learndifferent.mtm.service.impl;

import static com.github.learndifferent.mtm.constant.enums.RoleType.ADMIN;
import static com.github.learndifferent.mtm.constant.enums.RoleType.USER;
import static com.github.learndifferent.mtm.constant.enums.RoleType.valueOf;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.RoleType;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.entity.UserDO;
import com.github.learndifferent.mtm.manager.NotificationManager;
import com.github.learndifferent.mtm.manager.UserAccountManager;
import com.github.learndifferent.mtm.mapper.UserMapper;
import com.github.learndifferent.mtm.query.ChangePasswordRequest;
import com.github.learndifferent.mtm.query.CreateUserRequest;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.utils.CompareStringUtil;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.Md5Util;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.learndifferent.mtm.vo.UserBookmarkNumberVO;
import com.github.learndifferent.mtm.vo.UserVO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

/**
 * User Service implementation
 *
 * @author zhou
 * @date 2021/09/05
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserAccountManager userAccountManager;
    private final NotificationManager notificationManager;

    @Autowired
    public UserServiceImpl(UserMapper userMapper,
                           UserAccountManager userAccountManager,
                           NotificationManager notificationManager) {
        this.userMapper = userMapper;
        this.userAccountManager = userAccountManager;
        this.notificationManager = notificationManager;
    }

    @Override
    @Cacheable(value = "user:names-and-bookmarks")
    public List<UserBookmarkNumberVO> getNamesAndPublicBookmarkNums(List<String> usernames) {
        return userMapper.getNamesAndPublicBookmarkNums(usernames);
    }

    @Override
    public boolean changePassword(ChangePasswordRequest info) {
        String userName = info.getUserName();
        String oldPassword = info.getOldPassword();
        String newPassword = info.getNewPassword();

        // if user does not exist, it will be considered as wrong password
        UserVO user = getUserByNameAndPassword(userName, oldPassword);
        ThrowExceptionUtils.throwIfNull(user, ResultCode.PASSWORD_INCORRECT);

        UserDTO userDTO = UserDTO.ofPasswordUpdate(user.getUserId(), newPassword);
        // update user
        return userMapper.updateUser(userDTO);
    }

    @Override
    public boolean addUser(CreateUserRequest usernameAndPassword, String role) {

        String username = usernameAndPassword.getUserName();
        String notEncryptedPassword = usernameAndPassword.getPassword();
        return userAccountManager.createUser(username, notEncryptedPassword, role);
    }

    @Override
    public UserVO getUserByNameAndPassword(String userName, String notEncryptedPassword) {
        String password = Md5Util.getMd5(notEncryptedPassword);
        UserDO userDO = userMapper.getUserByNameAndPassword(userName, password);
        return DozerUtils.convert(userDO, UserVO.class);
    }

    @Override
    public String getRoleByName(String userName) {
        return userMapper.getRoleByName(userName);
    }

    @Override
    @Cacheable(value = "user:name", key = "#userName")
    public UserVO getUserByName(String userName) {
        UserDO userDO = userMapper.getUserByName(userName);
        return DozerUtils.convert(userDO, UserVO.class);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "user:names-and-bookmarks", allEntries = true),
            @CacheEvict(value = {"user:name"}, key = "#userName")})
    public boolean deleteUserAndAssociatedData(String currentUsername,
                                               String userName,
                                               String notEncryptedPassword) {
        boolean hasNoPermission = CompareStringUtil.notEqualsIgnoreCase(currentUsername, userName);
        ThrowExceptionUtils.throwIfTrue(hasNoPermission, ResultCode.PERMISSION_DENIED);
        return userAccountManager.deleteAllDataRelatedToUser(userName, notEncryptedPassword);
    }

    @Override
    @Cacheable(value = "empty:user:all", unless = "#result != null and #result.size() > 0")
    public List<UserVO> getUsers(PageInfoDTO pageInfo) {
        Integer from = pageInfo.getFrom();
        Integer size = pageInfo.getSize();
        List<UserDO> users = userMapper.getUsers(from, size);
        return DozerUtils.convertList(users, UserVO.class);
    }

    @Override
    public boolean changeUserRoleAndRecordChanges(String userId, String newRole) {
        String curRole = userMapper.getUserRoleById(userId);
        try {
            RoleType currentRole = valueOf(curRole.toUpperCase());
            RoleType newUserRole = valueOf(newRole.toUpperCase());
            return changeUserRoleAndRecordChanges(userId, currentRole, newUserRole);
        } catch (IllegalArgumentException | NullPointerException e) {
            e.printStackTrace();
            // if can't get the role, return false
            return false;
        }
    }

    private boolean changeUserRoleAndRecordChanges(String userId, RoleType curRole, RoleType newRole) {
        if (curRole.equals(newRole)) {
            // return true if no changes need to be done
            return true;
        }

        boolean success = false;
        if (isUpgradeOrDowngrade(curRole, newRole)) {
            success = updateUserRole(userId, newRole);
        }

        if (success) {
            // record changes
            notificationManager.recordRoleChanges(userId, curRole, newRole);
        }
        return success;
    }

    private boolean isUpgradeOrDowngrade(RoleType curRole, RoleType newRole) {
        return (USER.equals(curRole) && ADMIN.equals(newRole))
                || (ADMIN.equals(curRole) && USER.equals(newRole));
    }

    private boolean updateUserRole(String userId, RoleType role) {
        UserDTO user = UserDTO.ofRoleUpdate(userId, role);
        return userMapper.updateUser(user);
    }
}