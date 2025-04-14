package liaison.linkit.visit.dailyviewcount.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.visit.dailyviewcount.domain.LogDailyViewCount;
import liaison.linkit.visit.dailyviewcount.domain.LogViewType;
import liaison.linkit.visit.dailyviewcount.domain.QLogDailyViewCount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LogDailyViewCountCustomRepositoryImpl implements LogDailyViewCountCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<LogDailyViewCount> findByLogViewTypeAndLogIdAndDate(
            LogViewType logViewType, Long logId, LocalDate date) {
        QLogDailyViewCount logDailyViewCount = QLogDailyViewCount.logDailyViewCount;
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(logDailyViewCount)
                        .where(
                                logDailyViewCount.logViewType.eq(logViewType),
                                logDailyViewCount.logId.eq(logId),
                                logDailyViewCount.date.eq(date))
                        .fetchOne());
    }

    @Override
    public List<LogDailyViewCount> findTop2ByLogViewTypeAndDateOrderByDailyViewCountDesc(
            LocalDate date) {
        QLogDailyViewCount logDailyViewCount = QLogDailyViewCount.logDailyViewCount;
        return queryFactory
                .selectFrom(logDailyViewCount)
                .where(logDailyViewCount.date.eq(date))
                .orderBy(logDailyViewCount.dailyViewCount.desc())
                .limit(2)
                .fetch();
    }
}
