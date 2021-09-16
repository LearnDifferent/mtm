package com.github.learndifferent.mtm.vo;

import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 专门返回 MyPage 页面需要展示的数据
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class MyPageVO implements Serializable {

    /**
     * 我的用户数据
     */
    private UserDTO user;
    /**
     * 我的用户名首个字符
     */
    private Character firstCharOfName;
    /**
     * 我收藏的网页
     */
    private List<WebsiteDTO> myWebs;
    /**
     * 我收藏网页的总页数
     */
    private Integer totalPage;

    /**
     * IP 地址
     */
    private String ip;

    private static final long serialVersionUID = 1L;
}
