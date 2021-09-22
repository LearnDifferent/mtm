package com.github.learndifferent.mtm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 用户信息 + 该用户收藏的网页的个数
 *
 * @author zhou
 * @date 2021/09/05
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWithWebCountDTO extends UserDTO {

    /**
     * New field: the number of websites that user owns
     */
    private Integer webCount;
}

