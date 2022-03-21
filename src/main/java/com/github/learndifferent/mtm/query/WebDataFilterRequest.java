package com.github.learndifferent.mtm.query;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Filter Public Website Data Request
 *
 * @author zhou
 * @date 2022/3/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebDataFilterRequest implements Serializable {

    /**
     * Amount of data to load (The default value is 10)
     */
    private Integer load;

    /**
     * Filter by Username (Selecting all users if null or empty)
     */
    private List<String> usernames;

    /**
     * Filter by datetime
     */
    private List<String> datetimeList;

    /**
     * True if order by username, null or false if order by datetime
     */
    private Boolean isOrderByUsername;

    /**
     * True if ascending order, null or false if descending order
     */
    private Boolean isDesc;

    private static final long serialVersionUID = 1L;
}
