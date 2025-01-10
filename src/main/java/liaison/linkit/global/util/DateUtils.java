package liaison.linkit.global.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    private static final DateTimeFormatter DB_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // 디데이 계산
    public static int calculateDDay(String endDate) {
        if (endDate == null || endDate.isEmpty()) {
            throw new IllegalArgumentException("endDate must not be null or empty");
        }

        // "yyyy-MM-dd" 형태를 LocalDate로 직접 파싱
        LocalDate end = LocalDate.parse(endDate, DB_DATE_FORMATTER);

        // 현재 날짜 기준으로 디데이 계산
        LocalDate today = LocalDate.now();
        return (int) ChronoUnit.DAYS.between(today, end);
    }
}

