package com.github.learndifferent.mtm.vo;

import com.github.learndifferent.mtm.dto.UserDTO;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 管理员页面需要展示的数据
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class AdminPageVO implements Serializable {

    /**
     * 是否为管理员
     */
    private Boolean admin;
    /**
     * 如果是管理员，需要展示日志
     */
    private List<SysLog> logs;
    /**
     * 如果是管理员，需要展示所有用户
     */
    private List<UserDTO> users;

    private static final long serialVersionUID = 1L;
}
