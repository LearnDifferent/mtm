package com.github.learndifferent.mtm.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Filter Website Data by Username and Creation Time
 *
 * @author zhou
 * @date 2022/3/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class WebsiteDataFilterDTO implements Serializable {

    /**
     * Amount of data to load (The default value is 10)
     */
    private Integer load;

    /**
     * Filter by Username (Selecting all users if null or empty)
     */
    private List<String> usernames;

    /**
     * Filter by datetime: Start from this datetime (including this datetime)
     */
    private Instant fromDatetime;

    /**
     * Filter by datetime: The datetime to end (including this datetime).
     */
    private Instant toDatetime;

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
