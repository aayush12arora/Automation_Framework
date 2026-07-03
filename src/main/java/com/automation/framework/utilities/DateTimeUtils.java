package com.automation.framework.utilities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Date/time formatting helpers. Application-agnostic.
 */
public final class DateTimeUtils {

    public static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    public static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateTimeUtils() {
    }

    public static String nowTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP);
    }

    public static String today() {
        return LocalDate.now().format(ISO_DATE);
    }

    public static String futureDate(int daysAhead) {
        return LocalDate.now().plusDays(daysAhead).format(ISO_DATE);
    }

    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
}
