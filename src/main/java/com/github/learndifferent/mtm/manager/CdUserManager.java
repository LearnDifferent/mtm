package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.annotation.common.Password;
import com.github.learndifferent.mtm.annotation.common.UserRole;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.validation.user.create.UserCreationCheck;
import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.RoleType;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.dto.search.UserForSearchDTO;
import com.github.learndifferent.mtm.entity.UserDO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.mapper.CommentMapper;
import com.github.learndifferent.mtm.mapper.UserMapper;
import com.github.learndifferent.mtm.mapper.WebsiteMapper;
import com.github.learndifferent.mtm.utils.DozerUtils;
import com.github.learndifferent.mtm.utils.Md5Util;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
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
     * Delete all data related to the user
     *
     * @param username             username
     * @param notEncryptedPassword not encrypted password
     * @return false if deletion unsuccessful, which means the user does not exist
     * @throws com.github.learndifferent.mtm.exception.ServiceException If there is any mismatch while verifying user's
     *                                                                  name, password and permission to delete, it
     *                                                                  will throw an exception with the result code of
     *                                                                  {@link com.github.learndifferent.mtm.constant.enums.ResultCode#USER_NOT_EXIST}
     *                                                                  or {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PERMISSION_DENIED}
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean deleteAllDataRelatedToUser(String username, String notEncryptedPassword) {

        String userId = checkUserExistsAndReturnUserId(username, notEncryptedPassword);

        // Delete website data related to the user
        websiteMapper.deleteWebsiteDataByUsername(username);
        // Delete comment data related to the user
        commentMapper.deleteCommentsByUsername(username);

        // Delete all notifications related to the user (Redis don't need transaction in this situation)
        String key = KeyConstant.REPLY_NOTIFICATION_PREFIX + username.toLowerCase();
        notificationManager.deleteByKey(key);
        notificationManager.deleteFromReadSysNot(username);

        // Remove user data from Elasticsearch asynchronously
        elasticsearchManager.removeUserFromElasticsearchAsync(userId);

        // Remove user data from database (false if the user does not exist)
        return userMapper.deleteUserByUserId(userId);
    }

    /**
     * Check if the user exists and return the user ID
     *
     * @param username             username
     * @param notEncryptedPassword not encrypted password
     * @return User ID
     * @throws com.github.learndifferent.mtm.exception.ServiceException An exception will be thrown if the user does
     *                                                                  not exist, with the result code of {@link
     *                                                                  ResultCode#USER_NOT_EXIST}
     */
    private String checkUserExistsAndReturnUserId(String username, String notEncryptedPassword) {
        String password = Md5Util.getMd5(notEncryptedPassword);
        String userId = userMapper.getUserIdByNameAndPassword(username, password);
        ThrowExceptionUtils.throwIfNull(userId, ResultCode.USER_NOT_EXIST);
        return userId;
    }

    /**
     * Add a user: encrypt the password, set a user ID and creation time
     *
     * @param username             username
     * @param notEncryptedPassword not encrypted password
     * @param role                 user role
     * @return true if success
     * @throws ServiceException {@link UserCreationCheck} annotation will verify and throw an exception
     *                          if something goes wrong. If the username is already taken, the result will be {@link
     *                          com.github.learndifferent.mtm.constant.enums.ResultCode#USER_ALREADY_EXIST}.
     *                          If username contains not only letters and numbers, the result will be {@link
     *                          com.github.learndifferent.mtm.constant.enums.ResultCode#USERNAME_ONLY_LETTERS_NUMBERS}.
     *                          If username is empty, the result will be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#USERNAME_EMPTY}.
     *                          If username is not less than 30 characters, the result will be {@link
     *                          com.github.learndifferent.mtm.constant.enums.ResultCode#USERNAME_TOO_LONG}.
     *                          If password is empty, the result will be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#PASSWORD_EMPTY}.
     *                          If password is not less than 50 characters, the result will be {@link
     *                          com.github.learndifferent.mtm.constant.enums.ResultCode#PASSWORD_TOO_LONG}.
     *                          If user role is not found, the result will be {@link com.github.learndifferent.mtm.constant.enums.ResultCode#USER_ROLE_NOT_FOUND}.
     */
    @UserCreationCheck
    public boolean createUser(@Username String username,
                              @Password String notEncryptedPassword,
                              @UserRole(defaultRole = RoleType.USER) String role) {

        UserDTO userDTO = UserDTO.createUser(username, notEncryptedPassword, role);
        UserDO userDO = DozerUtils.convert(userDTO, UserDO.class);
        try {
            return createUser(userDO);
        } catch (DuplicateKeyException e) {
            // DuplicateKeyException is same as com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
            // the primary key is userName, so duplicate key means username is already taken
            throw new ServiceException(ResultCode.USER_ALREADY_EXIST);
        }
    }

    private boolean createUser(UserDO user) {
        boolean success = userMapper.addUser(user);
        if (success) {
            // add to Elasticsearch asynchronously
            UserForSearchDTO userToEs = DozerUtils.convert(user, UserForSearchDTO.class);
            elasticsearchManager.addUserDataToElasticsearchAsync(userToEs);
        }
        return success;
    }
}