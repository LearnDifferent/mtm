package com.github.learndifferent.mtm.dto;

import com.github.learndifferent.mtm.constant.enums.UserRole;
import com.github.learndifferent.mtm.utils.Md5Util;
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
        // encrypt and set password
        String password = Md5Util.getMd5(notEncryptedPassword);
        // get creation time
        Instant creationTime = Instant.now();
        return new UserDTO(null, username, password, creationTime, role.role());
    }

    public static UserDTO ofPasswordUpdate(String id, String notEncryptedPassword) {
        // encrypt and set the new password
        String newPassword = Md5Util.getMd5(notEncryptedPassword);
        return new UserDTO(id, null, newPassword, null, null);
    }

    public static UserDTO ofRoleUpdate(String id, UserRole role) {
        return new UserDTO(id, null, null, null, role.role());
    }

    private UserDTO(String id, String userName, String password, Instant createTime, String role) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.createTime = createTime;
        this.role = role;
    }

    /**
     * ID
     */
    private final String id;

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

    public String getId() {
        return id;
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
        return Objects.equals(id, userDTO.id) && Objects.equals(userName, userDTO.userName)
                && Objects.equals(password, userDTO.password) && Objects.equals(createTime,
                userDTO.createTime) && Objects.equals(role, userDTO.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, password, createTime, role);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", createTime=" + createTime +
                ", role='" + role + '\'' +
                '}';
    }

    private static final long serialVersionUID = 1L;
}