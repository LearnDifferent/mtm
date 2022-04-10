package com.github.learndifferent.mtm.query;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body that contains usernames
 *
 * @author zhou
 * @date 2022/4/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsernamesRequest implements Serializable {

    /**
     * Usernames
     */
    private List<String> usernames;

    private static final long serialVersionUID = 1L;
}