package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck.ExceptionIfEmpty;
import com.github.learndifferent.mtm.annotation.validation.user.create.NewUserCheck;
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
import org.springframework.dao.DuplicateKeyException;
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
    public List<UserWithWebCountDTO> getNamesAndCountMarkedWebsDesc() {
        // 传入的参数为 null，表示获取所有用户
        return userMapper.getNamesAndCountMarkedWebsDesc(null);
    }

    @Override
    public List<UserWithWebCountDTO> getNamesAndCountMarkedWebsDesc(List<String> usernames) {
        return userMapper.getNamesAndCountMarkedWebsDesc(usernames);
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
    private boolean changePassword(String userName,
                                   String oldPassword,
                                   String newPassword) {

        UserDTO user = getUserByNameAndPwd(userName, oldPassword);

        if (user == null) {
            throw new ServiceException(ResultCode.PASSWORD_INCORRECT);
        }

        UserDO userDO = DozerUtils.convert(user, UserDO.class);
        UserDO userWhoChangedPwd = userDO.setPassword(newPassword);
        // 调用当前类的事务方法，需要使用代理类
        UserServiceImpl userService = ApplicationContextUtils.getBean(UserServiceImpl.class);
        return userService.updateUser(userWhoChangedPwd);
    }

    /**
     * 更新用户信息（密码需要加密）
     *
     * @param user 需要更新的用户（密码无加密）
     * @return 是否更新成功
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean updateUser(UserDO user) {
        String pwdNew = Md5Util.getMd5(user.getPassword());
        user.setPassword(pwdNew);
        return userMapper.updateUser(user);
    }

    /**
     * 添加用户，并给用户增加 ID 和 CreateTime，将密码加密。
     * <p>{@link NewUserCheck} 注解会检查该用户名除了数字和英文字母外，是否还包含其他字符，如果有就抛出异常。</p>
     * <p>如果该用户已经存在，也会抛出用户已存在的异常。</p>
     * <p>如果用户名大于 30 个字符，也会抛出异常。</p>
     * <p>如果密码大于 50 个字符，也会抛出异常</p>
     * <p>如果用户名或密码为空，抛出异常</p>
     *
     * @param user 被添加的用户
     * @return 成功与否
     * @throws ServiceException 错误代码为：ResultCode.USER_ALREADY_EXIST、
     *                          ResultCode.USERNAME_ONLY_LETTERS_NUMBERS、
     *                          ResultCode.USERNAME_TOO_LONG 和 ResultCode.USERNAME_EMPTY、
     *                          ResultCode.PASSWORD_TOO_LONG 和 ResultCode.PASSWORD_EMPTY
     */
    @NewUserCheck(userClass = UserDO.class,
                  usernameFieldName = "userName",
                  passwordFieldName = "password")
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

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean addUser(CreateUserRequest userBasicInfo) {

        UserDO user = DozerUtils.convert(userBasicInfo, UserDO.class);

        UserServiceImpl userServiceImpl = ApplicationContextUtils.getBean(UserServiceImpl.class);
        return userServiceImpl.addUser(user);
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
    public String getRoleByName(String userName) {
        return userMapper.getRoleByName(userName);
    }

    @Override
    public UserDO getUserByName(String userName) {
        return userMapper.getUserByName(userName);
    }

    @Override
    @EmptyStringCheck
    public boolean deleteUserAndHisWebsiteData(@ExceptionIfEmpty String userName) {
        return deleteUserManager.deleteUserAndHisWebsiteData(userName);
    }

    @Override
    public List<UserDTO> getUsers() {
        List<UserDO> users = userMapper.getUsers();
        return DozerUtils.convertList(users, UserDTO.class);
    }
}
