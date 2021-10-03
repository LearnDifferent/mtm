package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息 + 该用户收藏的网页的个数
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWithWebCountDTO implements Serializable {

    /**
     * Username
     */
    private String userName;

    /**
     * The number of websites that user owns
     */
    private Integer webCount;

    private static final long serialVersionUID = 1L;
}

