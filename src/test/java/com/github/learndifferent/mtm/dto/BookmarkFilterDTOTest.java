package com.github.learndifferent.mtm.dto;

import com.github.learndifferent.mtm.constant.enums.Order;
import com.github.learndifferent.mtm.constant.enums.OrderField;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookmarkFilterDTOTest {

    @Test
    @DisplayName("End date should be greater than start date")
    void endDateShouldBeGreaterThanStartDate() {
        List<String> usernames = Arrays.asList("username1", "username2");
        Integer load = 10;

        LocalDateTime now = LocalDateTime.now();
        long current = now.toInstant(ZoneOffset.UTC).toEpochMilli();
        long tenDaysBefore = now.minusDays(10L).toInstant(ZoneOffset.UTC).toEpochMilli();
        String from = Long.toString(current);
        String to = Long.toString(tenDaysBefore);

        BookmarkFilterDTO bookmarkFilter = BookmarkFilterDTO
                .of(usernames, load, from, to, OrderField.CREATION_TIME, Order.DESC);
        Instant startDate = bookmarkFilter.getFromDatetime();
        Instant endDate = bookmarkFilter.getToDatetime();
        Assertions.assertTrue(endDate.isAfter(startDate));
    }
}