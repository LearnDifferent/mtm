package com.github.learndifferent.mtm.query;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
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
    @NotBlank(message = "Username cannot be empty")
    private String userName;
    /**
     * Old password
     */
    @NotBlank(message = "Old password cannot be empty")
    private String oldPassword;
    /**
     * New password
     */
    @NotBlank(message = "New password cannot be empty")
    private String newPassword;

    private static final long serialVersionUID = 1L;
}
