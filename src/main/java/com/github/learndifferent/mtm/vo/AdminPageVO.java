package com.github.learndifferent.mtm.vo;

import com.github.learndifferent.mtm.dto.UserDTO;
import com.github.learndifferent.mtm.entity.SysLog;
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
     * True if the role is Admin
     */
    private Boolean admin;
    /**
     * System Logs (Only Admin)
     */
    private List<SysLog> logs;
    /**
     * All Users (Only Admin)
     */
    private List<UserDTO> users;

    private static final long serialVersionUID = 1L;
}
