package liaison.linkit.visit.dailyviewcount.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import liaison.linkit.visit.dailyviewcount.domain.LogDailyViewCount;
import liaison.linkit.visit.dailyviewcount.domain.LogViewType;

public interface LogDailyViewCountCustomRepository {
    Optional<LogDailyViewCount> findByLogViewTypeAndLogIdAndDate(
            LogViewType logViewType, Long logId, LocalDate date);

    List<LogDailyViewCount> findTop2ByLogViewTypeAndDateOrderByDailyViewCountDesc(LocalDate date);
}
