package com.github.learndifferent.mtm.dto.search;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.learndifferent.mtm.config.CustomInstantSerializer;
import java.io.Serializable;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * User Data for search
 *
 * @author zhou
 * @date 2021/10/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@SuperBuilder
public class UserForSearchDTO implements Serializable {

    /**
     * ID
     */
    private String id;

    /**
     * Username
     */
    private String userName;

    /**
     * Creation date
     */
    @JsonSerialize(using = CustomInstantSerializer.class)
    private Instant createTime;

    /**
     * User Role
     */
    private String role;

    private static final long serialVersionUID = 1L;
}
