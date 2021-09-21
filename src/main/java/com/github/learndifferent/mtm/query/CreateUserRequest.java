package com.github.learndifferent.mtm.query;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建用户的 Request Body，包含创建用户的基本信息
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest implements Serializable {

    private String userName;
    private String password;
    private String role;

    private static final long serialVersionUID = 1L;
}
