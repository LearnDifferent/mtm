package com.github.learndifferent.mtm.dto;

import com.github.learndifferent.mtm.exception.ServiceException;
import com.github.learndifferent.mtm.query.FilterBookmarksRequest;
import java.io.Serializable;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

/**
 * Filter public bookmarks
 *
 * @author zhou
 * @date 2022/3/20
 */
@Slf4j
public class BookmarkFilterDTO implements Serializable {

    public static BookmarkFilterDTO of(FilterBookmarksRequest filterRequest) {
        Integer load = filterRequest.getLoad();
        List<String> usernames = filterRequest.getUsernames();
        Boolean isOrderByUsername = filterRequest.getIsOrderByUsername();
        Boolean isDesc = filterRequest.getIsDesc();
        List<String> datetimeList = filterRequest.getDatetimeList();
        return of(load, usernames, datetimeList, isOrderByUsername, isDesc);
    }

    public static BookmarkFilterDTO of(Integer load,
                                       List<String> usernames,
                                       List<String> datetimeList,
                                       Boolean isOrderByUsername,
                                       Boolean isDesc) {
        BookmarkFilterDTO bookmarkFilter =
                new BookmarkFilterDTO(load, usernames, null, null, isOrderByUsername, isDesc);
        bookmarkFilter.setTimes(datetimeList);
        return bookmarkFilter;
    }

    private void setTimes(List<String> datetimeList) {
        if (CollectionUtils.isEmpty(datetimeList)) {
            // Don't filter by datetime if no datetime is selected
            return;
        }

        int len = datetimeList.size();
        Instant fromDatetime;
        Instant toDatetime;

        switch (len) {
            case 1:
                // Select the particular datetime till current datetime
                // if only one datetime is selected
                fromDatetime = getDatetime(datetimeList.get(0));
                toDatetime = Instant.now();
                break;
            case 2:
                // Select between two datetime ranges
                fromDatetime = getDatetime(datetimeList.get(0));
                toDatetime = getDatetime(datetimeList.get(1));
                break;
            default:
                throw new ServiceException("Can't set " + len + " datetime ranges");
        }

        // set datetime
        this.fromDatetime = fromDatetime;
        this.toDatetime = toDatetime;

        // check and change the datetime order if necessary
        checkAndOrderDatetime();
    }

    private Instant getDatetime(String datetime) {
        try {
            return Instant.parse(datetime);
        } catch (NullPointerException | DateTimeParseException e) {
            // Make it be the current datetime if exception
            e.printStackTrace();
            log.warn("Can't get the datetime, change to current datetime instead.");
            return Instant.now();
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
                              Instant fromDatetime,
                              Instant toDatetime,
                              Boolean isOrderByUsername,
                              Boolean isDesc) {
        this.load = load;
        this.usernames = usernames;
        this.fromDatetime = fromDatetime;
        this.toDatetime = toDatetime;
        this.isOrderByUsername = isOrderByUsername;
        this.isDesc = isDesc;
    }

    /**
     * Amount of data to load (The default value is 10)
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
     * True if order by username, null or false if order by datetime
     */
    private final Boolean isOrderByUsername;

    /**
     * True if ascending order, null or false if descending order
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

    public Boolean getOrderByUsername() {
        return isOrderByUsername;
    }

    public Boolean getDesc() {
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
                that.toDatetime) && Objects.equals(isOrderByUsername, that.isOrderByUsername) && Objects
                .equals(isDesc, that.isDesc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(load, usernames, fromDatetime, toDatetime, isOrderByUsername, isDesc);
    }

    @Override
    public String toString() {
        return "BookmarkFilterDTO{" +
                "load=" + load +
                ", usernames=" + usernames +
                ", fromDatetime=" + fromDatetime +
                ", toDatetime=" + toDatetime +
                ", isOrderByUsername=" + isOrderByUsername +
                ", isDesc=" + isDesc +
                '}';
    }

    private static final long serialVersionUID = 1L;
}
