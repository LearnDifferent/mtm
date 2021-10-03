package com.github.learndifferent.mtm.query;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Username and Password
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest implements Serializable {

    /**
     * Username
     */
    private String userName;

    /**
     * Password
     */
    private String password;

    private static final long serialVersionUID = 1L;
}
