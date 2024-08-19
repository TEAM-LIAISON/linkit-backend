package liaison.linkit.team.domain.repository.history;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.history.History;
import liaison.linkit.team.domain.history.QHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class HistoryRepositoryCustomImpl implements HistoryRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByTeamProfileId(final Long teamProfileId) {
        QHistory history = QHistory.history;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(history)
                .where(history.teamProfile.id.eq(teamProfileId))
                .fetchFirst();

        return count != null;
    }

    @Override
    public Optional<History> findByTeamProfileId(final Long teamProfileId) {
        QHistory history = QHistory.history;

        History result = jpaQueryFactory
                .selectFrom(history)
                .where(history.teamProfile.id.eq(teamProfileId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<History> findAllByTeamProfileId(final Long teamProfileId) {
        QHistory history = QHistory.history;

        return jpaQueryFactory
                .selectFrom(history)
                .where(history.teamProfile.id.eq(teamProfileId))
                .fetch();
    }

    @Override
    @Transactional
    public void deleteAllByTeamProfileId(final Long teamProfileId) {
        QHistory history = QHistory.history;

        jpaQueryFactory
                .delete(history)
                .where(history.teamProfile.id.eq(teamProfileId))
                .execute();
    }
}
