package liaison.linkit.visit.dailyviewcount.repository;

import static liaison.linkit.visit.dailyviewcount.domain.QLogDailyViewCount.logDailyViewCount;

import java.time.LocalDate;
import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.visit.dailyviewcount.domain.LogDailyViewCount;
import liaison.linkit.visit.dailyviewcount.domain.LogViewType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LogDailyViewCountCustomRepositoryImpl implements LogDailyViewCountCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<LogDailyViewCount> findByLogViewTypeAndLogIdAndDate(
            LogViewType logViewType, Long logId, LocalDate date) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(logDailyViewCount)
                        .where(
                                logDailyViewCount.logViewType.eq(logViewType),
                                logDailyViewCount.logId.eq(logId),
                                logDailyViewCount.date.eq(date))
                        .fetchOne());
    }
}
