package com.github.learndifferent.mtm.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户名和密码的 vo
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserNameAndPwdVO implements Serializable {

    private String userName;
    private String password;

    private static final long serialVersionUID = 1L;
}