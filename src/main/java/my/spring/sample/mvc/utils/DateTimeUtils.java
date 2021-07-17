package my.spring.sample.mvc.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DateTimeUtils {

    public static LocalDateTime getDaysAgoDateTime(Integer days) {
        Date now = new Date();
        long milis = days * 60 * 60 * 24 * 1000L;
        long time = now.getTime() - milis;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    }

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }

    public static String toString(LocalDateTime ldt) {
        if(ldt == null)
            return null;
        return ldt.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
    }

    public static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_FORMAT));
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public static LocalDate toLocalDate(String date) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_FORMAT));
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
