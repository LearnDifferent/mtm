package com.github.learndifferent.mtm.vo;

import com.github.learndifferent.mtm.dto.WebsitePatternDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Home 页面需要展示的数据
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class HomeVO implements Serializable {

    /**
     * 当前用户
     */
    private String currentUser;
    /**
     * 需要展示的网页数据和总页数
     */
    private WebsitePatternDTO websInfo;
    /**
     * 需要进行操作的用户的名称
     */
    private String optUsername;

    private static final long serialVersionUID = 1L;
}