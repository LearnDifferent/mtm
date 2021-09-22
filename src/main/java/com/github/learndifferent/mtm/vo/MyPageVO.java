package com.github.learndifferent.mtm.vo;

import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.dto.WebsiteDTO;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
     * Personal info
     */
    private UserDTO user;
    /**
     * First character of my username
     */
    private Character firstCharOfName;
    /**
     * My website data
     */
    private List<WebsiteDTO> myWebs;
    /**
     * Total pages of my website data
     */
    private Integer totalPage;

    /**
     * IP Address
     */
    private String ip;

    private static final long serialVersionUID = 1L;
}
