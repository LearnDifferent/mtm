package com.github.learndifferent.mtm.dto.search;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
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
     * User ID
     */
    private String userId;

    /**
     * Username
     */
    private String userName;

    /**
     * Creation date
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;

    /**
     * User Role
     */
    private String role;

    private static final long serialVersionUID = 1L;
}
