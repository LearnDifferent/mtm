package com.github.learndifferent.mtm.query;

import com.github.learndifferent.mtm.exception.ServiceException;
import java.io.Serializable;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Filter Website Data by Username and Creation Time
 *
 * @author zhou
 * @date 2021/09/05
 */
@Component
@Slf4j
public class WebFilterRequest implements Serializable {

    /**
     * Amount of data to load
     */
    @Value("${website-filter.load}")
    private int load;

    /**
     * Filter by Username. Null or empty means selecting all users.
     */
    private List<String> usernames;

    /**
     * Filter by Creation Time: Start from this date (including this date). Null means not selecting date.
     */
    private Instant fromDate;

    /**
     * Filter by date: The date to end (including this date). Null means not selecting date.
     */
    private Instant toDate;

    /**
     * Order by which field
     */
    @Value("${website-filter.order}")
    private String order;

    /**
     * True if ascending order, false if descending order
     */
    @Value("${website-filter.isDesc}")
    private Boolean desc;

    private static final String CREATION_TIME = "creation_time";

    private static final String USER_NAME = "user_name";

    public WebFilterRequest() {
    }

    public void setDates(List<String> dates) {
        int len = dates.size();

        switch (len) {
            case 0:
                // Don't filter by datetime if no datetime is selected
                this.fromDate = null;
                this.toDate = null;
                break;
            case 1:
                // Select the particular datetime till current datetime
                // if only one datetime is selected
                this.fromDate = getDate(dates.get(0));
                this.toDate = Instant.now();
                break;
            case 2:
                // Select between two datetime ranges
                this.fromDate = getDate(dates.get(0));
                this.toDate = getDate(dates.get(1));
                break;
            default:
                throw new ServiceException("Can't set " + len + " datetime ranges");
        }

        // Check and change the datetime order if necessary
        checkAndOrderDatetime();
    }

    private Instant getDate(String date) {
        try {
            return Instant.parse(date);
        } catch (NullPointerException | DateTimeParseException e) {
            e.printStackTrace();
            log.warn("Can't get the datetime, change to current datetime instead.");
            return Instant.now();
        }
    }

    private void checkAndOrderDatetime() {
        if (this.fromDate == null && this.toDate == null) {
            // Don't filter by datetime if no datetime is selected
            return;
        }
        // Make it be the current datetime if one of them is null
        if (this.fromDate == null) {
            this.fromDate = Instant.now();
        }
        if (this.toDate == null) {
            this.toDate = Instant.now();
        }
        // Change datetime order if necessary
        if (this.toDate.isBefore(this.fromDate)) {
            Instant tmp = this.toDate;
            this.toDate = this.fromDate;
            this.fromDate = tmp;
        }
    }

    /**
     * Set if order by time
     *
     * @param ifOrderByTime if order by time
     */
    public void setIfOrderByTime(Boolean ifOrderByTime) {
        if (BooleanUtils.isTrue(ifOrderByTime)) {
            this.order = CREATION_TIME;
        } else {
            // 这种情况包括了 null
            this.order = USER_NAME;
        }
    }

    /**
     * Set if descending order
     *
     * @param ifDesc if descending order
     */
    public void setIfDesc(Boolean ifDesc) {
        this.desc = ifDesc;
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    @Override
    public String toString() {
        return "WebFilterRequest{" +
                "load=" + load +
                ", usernames=" + usernames +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", order='" + order + '\'' +
                ", desc=" + desc +
                '}';
    }

    private static final long serialVersionUID = 1L;
}
