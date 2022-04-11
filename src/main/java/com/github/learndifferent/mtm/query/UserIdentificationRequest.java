package com.github.learndifferent.mtm.query;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body that contains username and password entered by the user
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserIdentificationRequest implements Serializable {

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