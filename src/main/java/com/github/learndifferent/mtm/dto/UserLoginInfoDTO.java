package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * User Login Info
 *
 * @author zhou
 * @date 2023/9/19
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class UserLoginInfoDTO implements Serializable {

    public static UserLoginInfoDTO of(String username, long userId) {
        return new UserLoginInfoDTO(username, userId);
    }

    public static UserLoginInfoDTO empty() {
        return new UserLoginInfoDTO("", -1L);
    }

    private String username;
    private Long userId;

    private static final long serialVersionUID = 1L;
}