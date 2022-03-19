package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 用户信息
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserDTO implements Serializable {

    /**
     * User ID
     */
    private String userId;

    /**
     * Username
     */
    private String userName;

    /**
     * Password
     */
    private String password;

    /**
     * Creation date
     */
    private Instant createTime;

    /**
     * User Role
     */
    private String role;

    private static final long serialVersionUID = 1L;
}