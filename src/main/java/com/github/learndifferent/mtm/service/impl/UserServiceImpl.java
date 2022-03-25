package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.PageInfoDTO;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.dto.UserWithWebCountDTO;
import com.github.learndifferent.mtm.entity.UserDO;
import com.github.learndifferent.mtm.manager.CdUserManager;
import com.github.learndifferent.mtm.mapper.UserMapper;
import com.github.learndifferent.mtm.query.ChangePwdRequest;
import com.github.learndifferent.mtm.query.CreateUserRequest;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.utils.ApplicationContextUtils;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.Md5Util;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

/**
 * UserService implementation
 *
 * @author zhou
 * @date 2021/09/05
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final CdUserManager cdUserManager;

    @Autowired
    public UserServiceImpl(UserMapper userMapper,
                           CdUserManager cdUserManager) {
        this.userMapper = userMapper;
        this.cdUserManager = cdUserManager;
    }

    @Override
    @Cacheable(value = "usernamesAndTheirWebs")
    public List<UserWithWebCountDTO> getNamesAndCountTheirPublicWebs() {
        // null means get all user's
        return userMapper.getNamesAndCountTheirPublicWebs(null);
    }

    @Override
    public List<UserWithWebCountDTO> getNamesAndCountTheirPublicWebs(List<String> usernames) {
        return userMapper.getNamesAndCountTheirPublicWebs(usernames);
    }

    @Override
    public boolean changePassword(ChangePwdRequest info) {
        String userName = info.getUserName();
        String oldPassword = info.getOldPassword();
        String newPassword = info.getNewPassword();

        UserServiceImpl userService = ApplicationContextUtils.getBean(UserServiceImpl.class);
        UserDTO user = userService.getUserByNameAndPwd(userName, oldPassword);

        // if user does not exist, it will be considered as wrong password
        ThrowExceptionUtils.throwIfNull(user, ResultCode.PASSWORD_INCORRECT);

        // encrypt the password
        newPassword = Md5Util.getMd5(newPassword);
        // set the new password
        UserDO userDO = DozerUtils.convert(user, UserDO.class);
        userDO.setPassword(newPassword);
        // update user
        return userMapper.updateUser(userDO);
    }

    @Override
    public boolean addUser(CreateUserRequest usernameAndPassword, String role) {

        String username = usernameAndPassword.getUserName();
        String notEncryptedPassword = usernameAndPassword.getPassword();

        return cdUserManager.createUser(username, notEncryptedPassword, role);
    }

    @Override
    public UserDTO getUserByNameAndPwd(String userName, String notEncryptedPassword) {
        String password = Md5Util.getMd5(notEncryptedPassword);
        UserDO userDO = userMapper.getUserByNameAndPwd(userName, password);
        return DozerUtils.convert(userDO, UserDTO.class);
    }

    @Override
    @Cacheable(value = "getRoleByName", key = "#userName")
    public String getRoleByName(String userName) {
        return userMapper.getRoleByName(userName);
    }

    @Override
    @Cacheable(value = "getUserByName", key = "#userName")
    public UserDTO getUserByName(String userName) {
        UserDO userDO = userMapper.getUserByName(userName);
        return DozerUtils.convert(userDO, UserDTO.class);
    }

    @Override
    @Caching(evict = {
            @CacheEvict("usernamesAndTheirWebs"),
            @CacheEvict(value = {"getUserByName", "getRoleByName"}, key = "#userName")
    })
    public boolean deleteUserAndWebAndCommentData(String userName, String notEncryptedPassword) {
        return cdUserManager.deleteAllDataRelatedToUser(userName, notEncryptedPassword);
    }

    @Override
    public List<UserDTO> getUsers(PageInfoDTO pageInfo) {
        Integer from = pageInfo.getFrom();
        Integer size = pageInfo.getSize();
        List<UserDO> users = userMapper.getUsers(from, size);
        return DozerUtils.convertList(users, UserDTO.class);
    }
}
