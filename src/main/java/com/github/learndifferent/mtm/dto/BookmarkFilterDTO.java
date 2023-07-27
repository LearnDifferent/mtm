package com.github.learndifferent.mtm.dto;

import com.github.learndifferent.mtm.constant.enums.Order;
import com.github.learndifferent.mtm.constant.enums.OrderField;
import com.github.learndifferent.mtm.constant.enums.ResultCode;
import com.github.learndifferent.mtm.exception.ServiceException;
import java.io.Serializable;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import org.springframework.util.StringUtils;

/**
 * Filter
 *
 * @author zhou
 * @date 2022/3/20
 */
public class BookmarkFilterDTO implements Serializable {

    public static BookmarkFilterDTO of(List<String> usernames,
                                       Integer load,
                                       String fromTimestamp,
                                       String toTimestamp,
                                       OrderField orderField,
                                       Order order) {
        BookmarkFilterDTO bookmarkFilter = new BookmarkFilterDTO(load, usernames, orderField, order);
        bookmarkFilter.setTimes(fromTimestamp, toTimestamp);
        return bookmarkFilter;
    }

    private void setTimes(String fromTimestamp, String toTimestamp) {
        boolean isNoTime = StringUtils.isEmpty(fromTimestamp)
                && StringUtils.isEmpty(toTimestamp);
        if (isNoTime) {
            // Don't filter by datetime if no datetime is selected
            return;
        }

        // set datetime
        this.fromDatetime = getTime(fromTimestamp);
        this.toDatetime = getTime(toTimestamp);

        // check and change the datetime order if necessary
        checkAndOrderDatetime();
    }

    private Instant getTime(String timestamp) {
        // get the current datetime if null
        return timestamp == null ? Instant.now() : getInstant(timestamp);
    }

    private Instant getInstant(String timestamp) {
        try {
            return Instant.ofEpochMilli(Long.parseLong(timestamp));
        } catch (DateTimeParseException | NumberFormatException e) {
            e.printStackTrace();
            throw new ServiceException(ResultCode.TIMESTAMP_INVALID);
        }
    }

    private void checkAndOrderDatetime() {
        Instant fromDatetime = this.fromDatetime;
        Instant toDatetime = this.toDatetime;

        // Change datetime order if necessary
        if (toDatetime.isBefore(fromDatetime)) {
            this.fromDatetime = toDatetime;
            this.toDatetime = fromDatetime;
        }
    }

    private BookmarkFilterDTO(Integer load,
                              List<String> usernames,
                              OrderField orderField,
                              Order order) {
        this.load = load;
        this.usernames = usernames;
        this.orderField = orderField.getSnakeCaseName();
        this.isDesc = order.isDesc();
    }

    /**
     * Amount of data to load
     */
    private final Integer load;

    /**
     * Filter by Username (Selecting all users if null or empty)
     */
    private final List<String> usernames;

    /**
     * Filter by datetime: Start from this datetime (including this datetime)
     */
    private Instant fromDatetime;

    /**
     * Filter by datetime: The datetime to end (including this datetime).
     */
    private Instant toDatetime;

    /**
     * Order by the field
     */
    private final String orderField;

    /**
     * True if descending order, null or false if ascending order
     */
    private final Boolean isDesc;

    public Integer getLoad() {
        return load;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public Instant getFromDatetime() {
        return fromDatetime;
    }

    public Instant getToDatetime() {
        return toDatetime;
    }

    public String getOrderField() {
        return orderField;
    }

    public Boolean getIsDesc() {
        return isDesc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BookmarkFilterDTO that = (BookmarkFilterDTO) o;
        return Objects.equals(load, that.load) && Objects.equals(usernames, that.usernames)
                && Objects.equals(fromDatetime, that.fromDatetime) && Objects.equals(toDatetime,
                that.toDatetime) && Objects.equals(orderField, that.orderField) && Objects.equals(
                isDesc, that.isDesc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(load, usernames, fromDatetime, toDatetime, orderField, isDesc);
    }

    @Override
    public String toString() {
        return "BookmarkFilterDTO{" +
                "load=" + load +
                ", usernames=" + usernames +
                ", fromDatetime=" + fromDatetime +
                ", toDatetime=" + toDatetime +
                ", orderField='" + orderField + '\'' +
                ", isDesc=" + isDesc +
                '}';
    }

    private static final long serialVersionUID = 1L;
}