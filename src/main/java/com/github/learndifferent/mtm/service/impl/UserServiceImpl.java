package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.constant.enums.ResultCode;
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
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * UserService 实现类
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
        // 传入的参数为 null，表示获取所有用户
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

        // 用户不存在，视为密码错误
        ThrowExceptionUtils.throwIfNull(user, ResultCode.PASSWORD_INCORRECT);

        // 加密新密码
        newPassword = Md5Util.getMd5(newPassword);
        // 设置新密码
        UserDO userDO = DozerUtils.convert(user, UserDO.class);
        userDO.setPassword(newPassword);
        // 更新用户
        return userMapper.updateUser(userDO);
    }

    @Override
    public boolean addUser(CreateUserRequest usernameAndPassword, String role) {

        String username = usernameAndPassword.getUserName();
        String notEncryptedPassword = usernameAndPassword.getPassword();

        return cdUserManager.createUser(username, notEncryptedPassword, role);
    }

    /**
     * 根据用户名和密码查找用户（密码需要加密）
     *
     * @param userName 用户名
     * @param password 密码（没有加密）
     * @return 用户
     */
    @Override
    public UserDTO getUserByNameAndPwd(String userName, String password) {
        String pwd = Md5Util.getMd5(password);
        UserDO userDO = userMapper.getUserByNameAndPwd(userName, pwd);
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
            @CacheEvict({"allUsers", "usernamesAndTheirWebs"}),
            @CacheEvict(value = {"getUserByName", "getRoleByName"}, key = "#userName")
    })
    public boolean deleteUserAndWebAndCommentData(String userName, String password) {
        return cdUserManager.deleteUserAndWebAndCommentData(userName, password);
    }

    @Override
    @Cacheable("allUsers")
    public List<UserDTO> getAllUsersCaching() {
        List<UserDO> users = userMapper.getUsers();
        return DozerUtils.convertList(users, UserDTO.class);
    }

    @Override
    @CachePut("allUsers")
    public List<UserDTO> getAllUsersRefreshing() {
        List<UserDO> users = userMapper.getUsers();
        return DozerUtils.convertList(users, UserDTO.class);
    }

    @Override
    @Scheduled(cron = "0 0 * * * ?")
    public void getAllUserRefreshingScheduledTask() {
        UserService userService = ApplicationContextUtils.getBean(UserService.class);
        userService.getAllUsersRefreshing();
    }
}
