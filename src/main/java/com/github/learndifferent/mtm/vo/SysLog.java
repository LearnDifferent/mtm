package com.github.learndifferent.mtm.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 系统日志
 *
 * @author zhou
 * @date 2021/09/05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class SysLog implements Serializable {

    private String title;

    /**
     * 0 CREATE,
     * 1 READ,
     * 2 UPDATE,
     * 3 DELETE,
     * 4 OTHERS
     */
    private String optType;

    private String method;

    private String msg;

    /**
     * Normal,
     * Error
     */
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date optTime;

    private static final long serialVersionUID = 1L;

}
