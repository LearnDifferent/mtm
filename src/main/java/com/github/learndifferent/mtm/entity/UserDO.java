package com.github.learndifferent.mtm.entity;

import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Data Object
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDO implements Serializable {

    /**
     * ID
     */
    private Long id;
    /**
     * Username
     */
    private String userName;
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