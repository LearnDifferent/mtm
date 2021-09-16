package com.github.learndifferent.mtm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 没有 ID、用户名和创建时间的网页数据
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class WebWithNoIdentityDTO implements Serializable {

    private String title;
    private String url;
    private String img;
    private String desc;

    private static final long serialVersionUID = 1L;
}
