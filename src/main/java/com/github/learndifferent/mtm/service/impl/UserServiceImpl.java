package com.github.learndifferent.mtm.service.impl;

import static com.github.learndifferent.mtm.constant.enums.UserRole.ADMIN;
import static com.github.learndifferent.mtm.constant.enums.UserRole.USER;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.UserRole;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.UserBookmarkRankingByRoleDTO;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.entity.UserDO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.manager.NotificationManager;
import com.github.learndifferent.mtm.manager.UserManager;
import com.github.learndifferent.mtm.mapper.UserMapper;
import com.github.learndifferent.mtm.query.ChangePasswordRequest;
import com.github.learndifferent.mtm.query.UserIdentificationRequest;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.utils.BeanUtils;
import com.github.learndifferent.mtm.utils.CustomStringUtils;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.learndifferent.mtm.vo.UserBookmarkNumberVO;
import com.github.learndifferent.mtm.vo.UserVO;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserManager userManager;
    private final NotificationManager notificationManager;

    @Override
    public List<UserBookmarkRankingByRoleDTO> getRankingBookmarkNumByRole() {
        return userMapper.getRankingBookmarkNumByRole();
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
        UserDO user = userManager.getUserByNameAndPassword(userName, oldPassword);

        UserDTO userDTO = Optional.ofNullable(user)
                .map(userDO -> UserDTO.ofPasswordUpdate(userDO.getId(), oldPassword, newPassword))
                .orElseThrow(() -> new ServiceException(ResultCode.PASSWORD_INCORRECT));

        return userMapper.updateUser(userDTO);
    }

    @Override
    public String addUserAndGetUsername(UserIdentificationRequest userIdentification, UserRole role) {

        String username = userIdentification.getUserName();
        String notEncryptedPassword = userIdentification.getPassword();
        return userManager.createUserAndGetUsername(username, notEncryptedPassword, role);
    }

    @Override
    public String getRoleByName(String userName) {
        return userMapper.getRoleByName(userName);
    }

    @Override
    @Cacheable(value = "user:name", key = "#userName")
    public UserVO getUserByName(String userName) {
        UserDO userDO = userMapper.getUserByName(userName);
        return BeanUtils.convert(userDO, UserVO.class);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "user:names-and-bookmarks", allEntries = true),
            @CacheEvict(value = {"user:name"}, key = "#userName")})
    public boolean deleteUserAndAssociatedData(String currentUsername,
                                               String userName,
                                               String notEncryptedPassword) {
        boolean hasNoPermission = CustomStringUtils.notEqualsIgnoreCase(currentUsername, userName);
        ThrowExceptionUtils.throwIfTrue(hasNoPermission, ResultCode.PERMISSION_DENIED);
        return userManager.deleteAllDataRelatedToUser(userName, notEncryptedPassword);
    }

    @Override
    @Cacheable(value = "empty:user:all", unless = "#result != null and #result.size() > 0")
    public List<UserVO> getUsers(PageInfoDTO pageInfo) {
        Integer from = pageInfo.getFrom();
        Integer size = pageInfo.getSize();
        List<UserDO> users = userMapper.getUsers(from, size);
        return BeanUtils.convertList(users, UserVO.class);
    }

    @Override
    public boolean changeUserRoleAndRecordChanges(Integer id, String newRole) {
        return Optional.ofNullable(id)
                // get the current role by ID
                .map(userMapper::getUserRoleById)
                // change the role (log the role changes if success)
                // return the result of whether the role has been changed successfully
                .map(curRole -> changeUserRoleAndRecordChanges(id, curRole, newRole))
                // return false if the ID is null to indicate that the user role can't be changed
                .orElse(false);
    }

    private boolean changeUserRoleAndRecordChanges(int id, String curRole, String newRole) {
        try {
            UserRole currentRole = UserRole.valueOf(curRole.toUpperCase());
            UserRole newUserRole = UserRole.valueOf(newRole.toUpperCase());
            return changeUserRoleAndRecordChanges(id, currentRole, newUserRole);
        } catch (IllegalArgumentException | NullPointerException e) {
            log.error("Invalid role: {}", newRole, e);
            // return false if the role is invalid
            return false;
        }
    }

    private boolean changeUserRoleAndRecordChanges(int id, UserRole curRole, UserRole newRole) {
        if (curRole.equals(newRole)) {
            // return true if no changes need to be done
            return true;
        }

        boolean success = false;
        if (checkIfRoleCanBeChanged(curRole, newRole)) {
            success = updateUserRole(id, newRole);
        }

        if (success) {
            // log user role changes asynchronously
            notificationManager.logRoleChangesAsync(id, curRole, newRole);
        }
        return success;
    }

    private boolean checkIfRoleCanBeChanged(UserRole curRole, UserRole newRole) {
        return (USER.equals(curRole) && ADMIN.equals(newRole))
                || (ADMIN.equals(curRole) && USER.equals(newRole));
    }

    private boolean updateUserRole(Integer id, UserRole role) {
        UserDTO user = UserDTO.ofRoleUpdate(id, role);
        return userMapper.updateUser(user);
    }
}