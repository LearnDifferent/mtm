package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * A Data Transfer Object that represents the user ID and username information
 *
 * @author zhou
 * @date 2023/10/19
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class UserIdAndUsernameDTO implements Serializable {

    private Long userId;

    private String username;

    private static final long serialVersionUID = 1L;
}