package liaison.linkit.global.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    private static final DateTimeFormatter DB_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter KOREAN_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

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

    // 시간 변환기
    /**
     * 과거의 특정 시각(LocalDateTime)으로부터 현재 시각까지의 상대적 경과 시간을 문자열로 반환한다.
     *
     * @param dateTime 과거 시점 (LocalDateTime)
     * @return 상대 시간 ("방금 전", "n분 전", "n시간 전", "n일 전", "yyyy년 MM월 dd일")
     */
    public static String formatRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("dateTime must not be null");
        }

        LocalDateTime now = LocalDateTime.now();
        long diffSeconds = ChronoUnit.SECONDS.between(dateTime, now);

        // 미래 시각인 경우
        if (diffSeconds < 0) {
            throw new IllegalArgumentException("미래 시각은 지원하지 않습니다.");
        }

        // 1분 미만이면 "방금 전"
        if (diffSeconds < 60) {
            return "방금 전";
        }

        // 1) 분(minute) 단위 판단 (1분 ~ 60분 미만)
        long diffMinutes = ChronoUnit.MINUTES.between(dateTime, now);
        if (diffMinutes < 60) {
            return diffMinutes + "분 전";
        }

        // 2) 시(hour) 단위 판단 (1시간 ~ 24시간 미만)
        long diffHours = ChronoUnit.HOURS.between(dateTime, now);
        if (diffHours < 24) {
            return diffHours + "시간 전";
        }

        // 3) 일(day) 단위 판단 (1일 ~ 7일 미만)
        long diffDays = ChronoUnit.DAYS.between(dateTime, now);
        if (diffDays < 7) {
            return diffDays + "일 전";
        }

        // 4) 7일 이상인 경우 "yyyy년 MM월 dd일" 형식으로 표시
        return dateTime.format(KOREAN_DATE_FORMATTER);
    }
}

