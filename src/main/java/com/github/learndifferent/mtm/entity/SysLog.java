package com.github.learndifferent.mtm.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.learndifferent.mtm.config.CustomInstantDeserializer;
import com.github.learndifferent.mtm.config.CustomInstantSerializer;
import java.io.Serializable;
import java.time.Instant;
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

    /**
     * Title
     */
    private String title;

    /**
     * CREATE,
     * READ,
     * UPDATE,
     * DELETE,
     * OTHERS
     */
    private String optType;

    /**
     * Method
     */
    private String method;

    /**
     * Message
     */
    private String msg;

    /**
     * Normal or Error
     */
    private String status;

    /**
     * Creation time
     */
    @JsonSerialize(using = CustomInstantSerializer.class)
    @JsonDeserialize(using = CustomInstantDeserializer.class)
    private Instant optTime;

    private static final long serialVersionUID = 1L;

}
