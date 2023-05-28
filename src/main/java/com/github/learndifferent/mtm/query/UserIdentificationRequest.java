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
    @NotBlank(message = ErrorInfoConstant.USERNAME_EMPTY)
    @Length(min = ConstraintConstant.USERNAME_MIN_LENGTH,
            max = ConstraintConstant.USERNAME_MAX_LENGTH,
            message = ErrorInfoConstant.USERNAME_LENGTH)
    private String userName;

    /**
     * Password
     */
    @NotBlank(message = ErrorInfoConstant.PASSWORD_EMPTY)
    @Length(min = ConstraintConstant.PASSWORD_MIN_LENGTH,
            max = ConstraintConstant.PASSWORD_MAX_LENGTH,
            message = ErrorInfoConstant.PASSWORD_LENGTH)
    private String password;

    private static final long serialVersionUID = 1L;
}