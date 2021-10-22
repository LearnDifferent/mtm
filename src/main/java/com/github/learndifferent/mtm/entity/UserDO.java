package com.github.learndifferent.mtm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * User Data Object
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserDO implements Serializable {

    /**
     * Username is the primary key
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
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;
    /**
     * User Role
     */
    private String role;
}
