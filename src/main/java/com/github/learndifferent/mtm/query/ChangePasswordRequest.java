package com.github.learndifferent.mtm.query;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request Body that contains username, old password and new password
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest implements Serializable {

    /**
     * Username
     */
    String userName;
    /**
     * Old password
     */
    String oldPassword;
    /**
     * New password
     */
    String newPassword;

    private static final long serialVersionUID = 1L;
}
