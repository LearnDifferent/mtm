package com.github.learndifferent.mtm.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户基本信息
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBasicInfoVO implements Serializable {

    private String userName;
    private String password;
    private String role;

    private static final long serialVersionUID = 1L;
}
