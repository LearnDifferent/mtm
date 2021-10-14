package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.annotation.common.Password;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.validation.user.create.NewUserCheck;
import com.github.learndifferent.mtm.annotation.validation.user.delete.DeleteUserCheck;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.dto.UserWithWebCountDTO;
import com.github.learndifferent.mtm.entity.UserDO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.manager.DeleteUserManager;
import com.github.learndifferent.mtm.mapper.UserMapper;
import com.github.learndifferent.mtm.query.ChangePwdRequest;
import com.github.learndifferent.mtm.query.CreateUserRequest;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.utils.ApplicationContextUtils;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.Md5Util;
import com.github.learndifferent.mtm.utils.UUIDUtils;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserService 实现类
 *
 * @author zhou
 * @date 2021/09/05
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final DeleteUserManager deleteUserManager;

    @Autowired
    public UserServiceImpl(UserMapper userMapper,
                           DeleteUserManager deleteUserManager) {
        this.userMapper = userMapper;
        this.deleteUserManager = deleteUserManager;
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
        return changePassword(info.getUserName(),
                info.getOldPassword(), info.getNewPassword());
    }

    /**
     * 修改密码
     *
     * @param userName    用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改是否成功的信息
     */
    private boolean changePassword(String userName, String oldPassword, String newPassword) {
        UserServiceImpl userService = ApplicationContextUtils.getBean(UserServiceImpl.class);

        UserDTO user = userService.getUserByNameAndPwd(userName, oldPassword);

        if (user == null) {
            throw new ServiceException(ResultCode.PASSWORD_INCORRECT);
        }

        UserDO userDO = DozerUtils.convert(user, UserDO.class);
        UserDO userWhoChangedPwd = userDO.setPassword(newPassword);

        return userService.updateUser(userWhoChangedPwd);
    }

    /**
     * 更新用户信息（密码需要加密）。
     * 因为主要是修改密码，所以要使用 {@link CacheEvict} 来删除修改密码的缓存
     *
     * @param user 需要更新的用户（密码无加密）
     * @return 是否更新成功
     */
    @CacheEvict(value = "usernameAndPassword", key = "#user.userName")
    public boolean updateUser(UserDO user) {
        String pwdNew = Md5Util.getMd5(user.getPassword());
        user.setPassword(pwdNew);
        return userMapper.updateUser(user);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean addUser(CreateUserRequest usernameAndPassword, String role) {

        UserDO user = DozerUtils.convert(usernameAndPassword, UserDO.class);
        user.setRole(role);

        UserServiceImpl userServiceImpl = ApplicationContextUtils.getBean(UserServiceImpl.class);
        return userServiceImpl.addUser(user);
    }

    /**
     * 添加用户，并给用户增加 ID 和 CreateTime，将密码加密。
     *
     * @param user 被添加的用户
     * @return 成功与否
     * @throws ServiceException <p>{@link NewUserCheck} 注解会检查该用户名除了数字和英文字母外，是否还包含其他字符，如果有就抛出异常。</p>
     *                          <p>如果该用户已经存在，也会抛出用户已存在的异常。</p>
     *                          <p>如果用户名大于 30 个字符，也会抛出异常。</p>
     *                          <p>如果密码大于 50 个字符，也会抛出异常</p>
     *                          <p>如果用户名或密码为空，抛出异常</p>
     *                          <p>如果没有传入正确的用户角色，抛出异常</p>
     *                          <p>Result Code 为：</p>
     *                          <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#USER_ALREADY_EXIST}</p>
     *                          <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#USERNAME_ONLY_LETTERS_NUMBERS}</p>
     *                          <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#USERNAME_TOO_LONG}</p>
     *                          <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#USERNAME_EMPTY}</p>
     *                          <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#PASSWORD_TOO_LONG}</p>
     *                          <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#PASSWORD_EMPTY}</p>
     *                          <p>{@link com.github.learndifferent.mtm.constant.enums.ResultCode#USER_ROLE_NOT_FOUND}</p>
     */
    @NewUserCheck(userClass = UserDO.class,
                  usernameFieldName = "userName",
                  passwordFieldName = "password",
                  roleFieldName = "role")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean addUser(UserDO user) {

        // 添加 ID 和创建时间，将密码进行加密处理
        String uuid = UUIDUtils.getUuid();
        Date createTime = new Date();
        String password = Md5Util.getMd5(user.getPassword());
        user.setUserId(uuid)
                .setCreateTime(createTime)
                .setPassword(password);

        try {
            return userMapper.addUser(user);
        } catch (DuplicateKeyException e) {
            // 因为主键设置为了 userName，所以这里的 DuplicateKeyException 就是重复用户名的意思
            // 相当于捕获：com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
            throw new ServiceException(ResultCode.USER_ALREADY_EXIST);
        }
    }

    /**
     * 根据用户名和密码查找用户（密码需要加密）
     *
     * @param userName 用户名
     * @param password 密码（没有加密）
     * @return 用户
     */
    @Override
    @Cacheable(value = "usernameAndPassword", key = "#userName")
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
    @DeleteUserCheck
    @Caching(evict = {
            @CacheEvict({"allUsers", "usernamesAndTheirWebs"}),
            @CacheEvict(value = {"getUserByName", "usernameAndPassword", "getRoleByName"},
                        key = "#userName")
    })
    public boolean deleteUserAndWebAndCommentData(@Username String userName, @Password String password) {
        return deleteUserManager.deleteUserAndWebAndCommentData(userName);
    }

    @Override
    @Cacheable("allUsers")
    public List<UserDTO> getAllUsersCaching() {
        List<UserDO> users = userMapper.getUsers();
        return DozerUtils.convertList(users, UserDTO.class);
    }

    @Override
    @CacheEvict(value = "allUsers", beforeInvocation = true)
    public List<UserDTO> getAllUsersRefreshing() {
        UserService userService = ApplicationContextUtils.getBean(UserService.class);
        return userService.getAllUsersCaching();
    }

    @Override
    @Scheduled(cron = "0 0 * * * ?")
    public void getAllUserRefreshingScheduledTask() {
        UserService userService = ApplicationContextUtils.getBean(UserService.class);
        userService.getAllUsersRefreshing();
    }
}
