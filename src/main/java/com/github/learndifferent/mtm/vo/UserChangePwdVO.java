package com.github.learndifferent.mtm.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 修改密码的 vo
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserChangePwdVO implements Serializable {

    String userName;
    String oldPassword;
    String newPassword;

    private static final long serialVersionUID = 1L;
}
