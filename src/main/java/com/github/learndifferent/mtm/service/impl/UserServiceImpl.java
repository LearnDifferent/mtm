package com.github.learndifferent.mtm.service.impl;

import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck;
import com.github.learndifferent.mtm.annotation.modify.string.EmptyStringCheck.ExceptionIfEmpty;
import com.github.learndifferent.mtm.annotation.validation.user.create.NewUserCheck;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.dto.UserWithWebCountDTO;
import com.github.learndifferent.mtm.entity.UserDO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.mapper.UserMapper;
import com.github.learndifferent.mtm.service.UserService;
import com.github.learndifferent.mtm.utils.ApplicationContextUtils;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.Md5Util;
import com.github.learndifferent.mtm.utils.UUIDUtils;
import com.github.learndifferent.mtm.vo.UserBasicInfoVO;
import com.github.learndifferent.mtm.vo.UserChangePwdVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * UserService 实现类
 *
 * @author zhou
 * @date 2021/09/05
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
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
    public boolean changePassword(UserChangePwdVO info) {
        return changePassword(info.getUserName(),
                info.getOldPassword(), info.getNewPassword());
    }

    @Override
    public boolean changePassword(String userName,
                                  String oldPassword,
                                  String newPassword) {

        UserDTO user = getUserByNameAndPwd(userName, oldPassword);

        if (user == null) {
            throw new ServiceException(ResultCode.PASSWORD_INCORRECT);
        }

        // 调用当前类的事务方法，需要使用代理类
        UserService userService = ApplicationContextUtils.getBean(UserService.class);
        return userService.updateUser(user.setPassword(newPassword));
    }


    @Override
    @NewUserCheck(userClass = UserDTO.class,
            usernameFieldName = "userName",
            passwordFieldName = "password")
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean addUser(UserDTO user) {

        // 添加 ID 和创建时间，将密码进行加密处理
        String uuid = UUIDUtils.getUuid();
        Date createTime = new Date();
        String password = Md5Util.getMd5(user.getPassword());
        user.setUserId(uuid)
                .setCreateTime(createTime)
                .setPassword(password);

        try {
            UserDO userDO = DozerUtils.convert(user, UserDO.class);
            return userMapper.addUser(userDO);
        } catch (DuplicateKeyException e) {
            // 因为主键设置为了 userName，所以这里的 DuplicateKeyException 就是重复用户名的意思
            // 相当于捕获：com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
            throw new ServiceException(ResultCode.USER_ALREADY_EXIST);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean addUserByBasicInfo(UserBasicInfoVO userBasicInfo) {

        UserDTO userDTO = DozerUtils.convert(userBasicInfo, UserDTO.class);

        // 启用事务的时候，调用内部类的方法需要通过代理类
        UserService userService = ApplicationContextUtils.getBean(UserService.class);

        return userService.addUser(userDTO);
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
    public String getUserRoleById(String userId) {
        return userMapper.getUserRoleById(userId);
    }

    @Override
    public UserDO getUserById(String userId) {
        return userMapper.getUserById(userId);
    }

    @Override
    public UserDO getUserByName(String userName) {
        return userMapper.getUserByName(userName);
    }

    @EmptyStringCheck
    @Override
    public boolean deleteUserByName(@ExceptionIfEmpty String userName) {
        return userMapper.deleteUserByName(userName);
    }

    /**
     * 更新用户信息（密码需要加密）
     *
     * @param user 需要更新的用户（密码无加密）
     * @return 是否更新成功
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean updateUser(UserDTO user) {
        String pwdNew = Md5Util.getMd5(user.getPassword());
        user.setPassword(pwdNew);
        UserDO u = DozerUtils.convert(user, UserDO.class);
        return userMapper.updateUser(u);
    }

    @Override
    public List<UserDTO> getUsers() {
        List<UserDO> users = userMapper.getUsers();
        return DozerUtils.convertList(users, UserDTO.class);
    }
}
