package liaison.linkit.global.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    private static final DateTimeFormatter DB_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM");

    // 디데이 계산
    public static int calculateDDay(String endDate) {
        if (endDate == null || endDate.isEmpty()) {
            throw new IllegalArgumentException("endDate must not be null or empty");
        }

        // "2020.12" 형태를 YearMonth로 파싱
        YearMonth yearMonth = YearMonth.parse(endDate, DB_DATE_FORMATTER);

        // 해당 월의 마지막 날로 LocalDate 생성
        LocalDate end = yearMonth.atEndOfMonth();

        // 현재 날짜 기준으로 디데이 계산
        LocalDate today = LocalDate.now();
        return (int) ChronoUnit.DAYS.between(today, end);
    }
}

