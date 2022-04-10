package com.github.learndifferent.mtm.dto;

import com.github.learndifferent.mtm.constant.enums.UserRole;
import com.github.learndifferent.mtm.utils.Md5Util;
import com.github.learndifferent.mtm.utils.UUIDUtils;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * User Data Transfer Object
 *
 * @author zhou
 * @date 2021/09/05
 */
public class UserDTO implements Serializable {

    public static UserDTO ofNewUser(String username, String notEncryptedPassword, UserRole role) {
        // get user ID
        String userId = UUIDUtils.getUuid();
        // encrypt and set password
        String password = Md5Util.getMd5(notEncryptedPassword);
        // get creation time
        Instant creationTime = Instant.now();
        return new UserDTO(userId, username, password, creationTime, role.role());
    }

    public static UserDTO ofPasswordUpdate(String userId, String notEncryptedPassword) {
        // encrypt and set the new password
        String newPassword = Md5Util.getMd5(notEncryptedPassword);
        return new UserDTO(userId, null, newPassword, null, null);
    }

    public static UserDTO ofRoleUpdate(String userId, UserRole role) {
        return new UserDTO(userId, null, null, null, role.role());
    }

    private UserDTO(String userId, String userName, String password, Instant createTime, String role) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.createTime = createTime;
        this.role = role;
    }

    /**
     * User ID
     */
    private final String userId;

    /**
     * Username
     */
    private final String userName;

    /**
     * Password
     */
    private final String password;

    /**
     * Creation date
     */
    private final Instant createTime;

    /**
     * User Role
     */
    private final String role;

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public String getRole() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(userId, userDTO.userId)
                && Objects.equals(userName, userDTO.userName)
                && Objects.equals(password, userDTO.password)
                && Objects.equals(createTime, userDTO.createTime)
                && Objects.equals(role, userDTO.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userName, password, createTime, role);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", createTime=" + createTime +
                ", role='" + role + '\'' +
                '}';
    }

    private static final long serialVersionUID = 1L;
}