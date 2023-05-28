package com.github.learndifferent.mtm.query;

import com.github.learndifferent.mtm.constant.consist.ConstraintConstant;
import com.github.learndifferent.mtm.constant.consist.ErrorInfoConstant;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

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
    @NotBlank(message = ErrorInfoConstant.USERNAME_EMPTY)
    @Length(min = ConstraintConstant.USERNAME_MIN_LENGTH,
            max = ConstraintConstant.USERNAME_MAX_LENGTH,
            message = ErrorInfoConstant.USERNAME_LENGTH)
    private String userName;

    /**
     * Old password
     */
    @NotBlank(message = ErrorInfoConstant.OLD_PASSWORD_EMPTY)
    @Length(min = ConstraintConstant.PASSWORD_MIN_LENGTH,
            max = ConstraintConstant.PASSWORD_MAX_LENGTH,
            message = ErrorInfoConstant.OLD_PASSWORD_LENGTH)
    private String oldPassword;

    /**
     * New password
     */
    @NotBlank(message = ErrorInfoConstant.NEW_PASSWORD_EMPTY)
    @Length(min = ConstraintConstant.PASSWORD_MIN_LENGTH,
            max = ConstraintConstant.PASSWORD_MAX_LENGTH,
            message = ErrorInfoConstant.NEW_PASSWORD_LENGTH)
    private String newPassword;

    private static final long serialVersionUID = 1L;
}
