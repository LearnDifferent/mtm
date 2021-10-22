package com.github.learndifferent.mtm.manager;

import cn.dev33.satoken.stp.StpUtil;
import com.github.learndifferent.mtm.annotation.validation.user.create.NewUserCheck;
import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.RoleType;
import com.github.learndifferent.mtm.dto.search.UserForSearchDTO;
import com.github.learndifferent.mtm.entity.UserDO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.mapper.CommentMapper;
import com.github.learndifferent.mtm.mapper.UserMapper;
import com.github.learndifferent.mtm.mapper.WebsiteMapper;
import com.github.learndifferent.mtm.utils.ApplicationContextUtils;
import com.github.learndifferent.mtm.utils.CompareStringUtil;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.Md5Util;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import com.github.learndifferent.mtm.utils.UUIDUtils;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * User Creation and Deletion
 *
 * @author zhou
 * @date 2021/10/22
 */
@Component
public class CdUserManager {

    private final WebsiteMapper websiteMapper;
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;
    private final NotificationManager notificationManager;
    private final ElasticsearchManager elasticsearchManager;

    @Autowired
    public CdUserManager(ElasticsearchManager elasticsearchManager,
                         NotificationManager notificationManager,
                         WebsiteMapper websiteMapper,
                         UserMapper userMapper,
                         CommentMapper commentMapper) {
        this.elasticsearchManager = elasticsearchManager;
        this.notificationManager = notificationManager;
        this.websiteMapper = websiteMapper;
        this.userMapper = userMapper;
        this.commentMapper = commentMapper;
    }

    /**
     * 删除用户的网页数据和评论数据（包括回复）
     *
     * @param username 用户名
     * @return 返回 false 表示删除失败，也就是没有该用户
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean deleteUserAndWebAndCommentData(String username, String password) {

        String userId = checkUserExistsAndReturnUserId(username, password);
        checkDeletePermission(username);

        // 删除该用户收藏的所有网页数据
        websiteMapper.deleteWebsiteDataByUsername(username);
        // 删除用户的评论数据
        commentMapper.deleteCommentsByUsername(username);

        // 删除该用户的评论的通知（注意，没有设置 redis 的事务，不过按照执行情况也不需要 redis 的事务）
        String key = KeyConstant.REPLY_NOTIFICATION_PREFIX + username.toLowerCase();
        notificationManager.deleteNotificationByKey(key);

        // 异步删除 Elasticsearch 中的用户数据
        elasticsearchManager.removeUserFromElasticsearchAsync(userId);

        // 删除该用户（false 表示没有该用户）
        return userMapper.deleteUserByUserId(userId);
    }

    /**
     * 检查删除用户的权限：只有该用户有删除该用户的权限，且 guest 用户无法被删除
     *
     * @param userName 用户名
     * @throws com.github.learndifferent.mtm.exception.ServiceException 没有权限 {@link ResultCode#PERMISSION_DENIED}
     */
    private void checkDeletePermission(String userName) {

        String currentUsername = (String) StpUtil.getLoginId();

        // 如果不是当前用户删除自己的帐号，就抛出异常；如果删除的是 Guest 用户，也抛出异常
        boolean hasNoPermission = StpUtil.hasRole(RoleType.GUEST.role())
                || CompareStringUtil.notEqualsIgnoreCase(currentUsername, userName);

        ThrowExceptionUtils.throwIfTrue(hasNoPermission, ResultCode.PERMISSION_DENIED);
    }

    /**
     * 检查用户是否存在并返回 user id
     *
     * @param username             用户名
     * @param notEncryptedPassword 未加密的密码
     * @throws com.github.learndifferent.mtm.exception.ServiceException 用户不存在 {@link ResultCode#USER_NOT_EXIST}
     */
    private String checkUserExistsAndReturnUserId(String username, String notEncryptedPassword) {
        String password = Md5Util.getMd5(notEncryptedPassword);
        String userId = userMapper.getUserIdByNameAndPassword(username, password);
        ThrowExceptionUtils.throwIfNull(userId, ResultCode.USER_NOT_EXIST);
        return userId;
    }

    public boolean createUser(String username, String notEncryptedPassword, String role) {

        UserDO user = UserDO.builder().userName(username).role(role)
                .password(notEncryptedPassword).build();

        CdUserManager cdUserManager = ApplicationContextUtils.getBean(CdUserManager.class);
        return cdUserManager.addUserWithNoEncryptedPwdNoIdNoTime(user);
    }


    /**
     * Add a user: encrypt the password, set an ID and creation time
     *
     * @param user 被添加的用户
     * @return 成功与否
     * @throws ServiceException {@link NewUserCheck} 注解会检查该用户名除了数字和英文字母外，是否还包含其他字符，如果有就抛出异常。
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
     *                          <br>
     *                          <p>因为主键设置为了 userName，所以这里捕获的 {@link DuplicateKeyException} 异常就是重复用户名的意思，
     *                          相当于捕获了 {@link com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException}。</p>
     */
    @NewUserCheck(userClass = UserDO.class,
                  usernameFieldName = "userName",
                  passwordFieldName = "password",
                  roleFieldName = "role")
    public boolean addUserWithNoEncryptedPwdNoIdNoTime(UserDO user) {

        // 添加 ID
        String uuid = UUIDUtils.getUuid();
        // 添加创建时间
        Date createTime = new Date();
        // 将密码进行加密处理
        String password = Md5Util.getMd5(user.getPassword());
        // 设置属性
        user.setUserId(uuid).setCreateTime(createTime).setPassword(password);

        // 异步放入 Elasticsearch 中
        UserForSearchDTO userDataToEs = DozerUtils.convert(user, UserForSearchDTO.class);
        elasticsearchManager.addUserDataToElasticsearchAsync(userDataToEs);

        try {
            return userMapper.addUser(user);
        } catch (DuplicateKeyException e) {
            throw new ServiceException(ResultCode.USER_ALREADY_EXIST);
        }
    }
}
