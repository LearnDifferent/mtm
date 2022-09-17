package com.github.learndifferent.mtm.manager;

import com.github.learndifferent.mtm.annotation.common.Password;
import com.github.learndifferent.mtm.annotation.common.Username;
import com.github.learndifferent.mtm.annotation.validation.user.create.UserCreationCheck;
import com.github.learndifferent.mtm.constant.consist.KeyConstant;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.constant.enums.UserRole;
import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.dto.search.UserForSearchDTO;
import com.github.learndifferent.mtm.entity.UserDO;
import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.mapper.CommentMapper;
import com.github.learndifferent.mtm.mapper.UserMapper;
import com.github.learndifferent.mtm.mapper.BookmarkMapper;
import com.github.learndifferent.mtm.utils.Md5Util;
import com.github.learndifferent.mtm.utils.ThrowExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * User account manager
 *
 * @author zhou
 * @date 2021/10/22
 */
@Component
public class UserAccountManager {

    private final BookmarkMapper bookmarkMapper;
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;
    private final NotificationManager notificationManager;
    private final ElasticsearchManager elasticsearchManager;

    @Autowired
    public UserAccountManager(ElasticsearchManager elasticsearchManager,
                              NotificationManager notificationManager,
                              BookmarkMapper bookmarkMapper,
                              UserMapper userMapper,
                              CommentMapper commentMapper) {
        this.elasticsearchManager = elasticsearchManager;
        this.notificationManager = notificationManager;
        this.bookmarkMapper = bookmarkMapper;
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

        // Delete bookmarks related to the user
        bookmarkMapper.deleteUserBookmarks(username);
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
     * Complete the user information by encrypting the password, setting a user ID
     * and setting the creation time. After this operation, add the user to database
     * and return the username.
     *
     * @param username             username
     * @param notEncryptedPassword not encrypted password
     * @param role                 user role
     * @return true if success
     * @throws ServiceException {@link UserCreationCheck} annotation will verify and throw an exception
     *                          if something goes wrong. If the username is already taken, the result code will be
     *                          {@link ResultCode#USER_ALREADY_EXIST}.
     *                          If username contains not only letters and numbers, the result code will be {@link
     *                          ResultCode#USERNAME_ONLY_LETTERS_NUMBERS}.
     *                          If username is empty, the result will be {@link ResultCode#USERNAME_EMPTY}.
     *                          If username is not less than 30 characters, the result code will be {@link
     *                          ResultCode#USERNAME_TOO_LONG}.
     *                          If password is empty, the result will be {@link ResultCode#PASSWORD_EMPTY}.
     *                          If password is not less than 50 characters, the result code will be {@link
     *                          ResultCode#PASSWORD_TOO_LONG}.
     *                          If password is not greater than 8 characters, the result code will be
     *                          {@link ResultCode#PASSWORD_TOO_SHORT}
     */
    @UserCreationCheck
    public String createUserAndGetUsername(@Username String username,
                                           @Password String notEncryptedPassword,
                                           UserRole role) {

        UserDTO user = UserDTO.ofNewUser(username, notEncryptedPassword, role);
        try {
            return createUserAndGetUsername(user);
        } catch (DuplicateKeyException e) {
            // DuplicateKeyException is same as com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
            // the primary key is userName, so duplicate key means username is already taken
            throw new ServiceException(ResultCode.USER_ALREADY_EXIST);
        }
    }

    private String createUserAndGetUsername(UserDTO user) {
        boolean success = userMapper.addUser(user);
        if (success) {
            saveToElasticsearchAsync(user);
            return user.getUserName();
        }
        throw new ServiceException(ResultCode.USER_ALREADY_EXIST);
    }

    private void saveToElasticsearchAsync(UserDTO user) {
        // add to Elasticsearch asynchronously
        UserForSearchDTO data = UserForSearchDTO.builder()
                .userName(user.getUserName())
                .userId(user.getUserId())
                .createTime(user.getCreateTime())
                .role(user.getRole())
                .build();
        elasticsearchManager.saveToElasticsearchAsync(data);
    }

    /**
     * Get user by name and password
     *
     * @param username             username
     * @param notEncryptedPassword not encrypted password
     * @return user
     */
    public UserDO getUserByNameAndPassword(String username, String notEncryptedPassword) {
        String password = Md5Util.getMd5(notEncryptedPassword);
        return userMapper.getUserByNameAndPassword(username, password);
    }
}