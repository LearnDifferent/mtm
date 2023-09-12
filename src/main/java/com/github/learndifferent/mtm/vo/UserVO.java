package com.github.learndifferent.mtm.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.learndifferent.mtm.config.CustomInstantDeserializer;
import com.github.learndifferent.mtm.config.CustomInstantSerializer;
import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User View Object / User Value Object
 *
 * @author zhou
 * @date 2022/4/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO implements Serializable {

    /**
     * ID
     */
    private Integer id;
    /**
     * Username
     */
    private String userName;
    /**
     * Creation Time
     */
    @JsonSerialize(using = CustomInstantSerializer.class)
    @JsonDeserialize(using = CustomInstantDeserializer.class)
    private Instant createTime;
    /**
     * User Role
     */
    private String role;

    private static final long serialVersionUID = 1L;
}