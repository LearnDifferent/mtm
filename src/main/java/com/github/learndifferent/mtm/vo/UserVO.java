package com.github.learndifferent.mtm.vo;

import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User View Object / User Value Object
 *
 * @author zhou
 * @date 2022/4/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO implements Serializable {

    /**
     * Username
     */
    private String userName;

    /**
     * User ID
     */
    private String userId;
    /**
     * Password
     */
    private String password;
    /**
     * Creation Time
     */
    private Instant createTime;
    /**
     * User Role
     */
    private String role;

    private static final long serialVersionUID = 1L;
}