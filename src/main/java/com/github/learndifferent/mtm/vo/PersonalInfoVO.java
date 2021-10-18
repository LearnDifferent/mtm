package com.github.learndifferent.mtm.vo;

import com.github.learndifferent.mtm.dto.UserDTO;
import java.io.Serializable;
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
public class PersonalInfoVO implements Serializable {

    /**
     * Personal info
     */
    private UserDTO user;

    /**
     * IP Address
     */
    private String ip;

    /**
     * Count the total number of reply notifications
     */
    private Long totalReplyNotifications;

    private static final long serialVersionUID = 1L;
}
