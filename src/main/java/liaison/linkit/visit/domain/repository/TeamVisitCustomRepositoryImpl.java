package liaison.linkit.visit.domain.repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.visit.domain.QTeamVisit;
import liaison.linkit.visit.domain.TeamVisit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TeamVisitCustomRepositoryImpl implements TeamVisitCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext private EntityManager entityManager; // EntityManager 주입

    @Override
    public List<TeamVisit> getTeamVisitsByVisitedTeamId(final Long visitedTeamId) {
        QTeamVisit qTeamVisit = QTeamVisit.teamVisit;

        return jpaQueryFactory
                .selectFrom(qTeamVisit)
                .where(qTeamVisit.visitedTeamId.eq(visitedTeamId))
                .fetch();
    }

    @Override
    public Map<Long, Long> countVisitsPerTeamWithinLastWeek() {
        QTeamVisit qTeamVisit = QTeamVisit.teamVisit;
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        List<Tuple> results =
                queryFactory
                        .select(qTeamVisit.visitedTeamId, qTeamVisit.count())
                        .from(qTeamVisit)
                        .where(qTeamVisit.visitTime.after(oneWeekAgo))
                        .groupBy(qTeamVisit.visitedTeamId)
                        .fetch();

        Map<Long, Long> visitCountMap = new HashMap<>();

        // 안전하게 값을 Map에 추가
        for (Tuple tuple : results) {
            Long teamId = tuple.get(qTeamVisit.visitedTeamId);
            Long count = tuple.get(qTeamVisit.count());

            if (teamId != null) {
                visitCountMap.put(teamId, count != null ? count : 0L);
            }
        }

        return visitCountMap;
    }

    @Override
    public TeamVisit getTeamVisitByVisitedTeamIdAndVisitorProfileId(
            final Long visitedTeamId, final Long visitorProfileId) {
        QTeamVisit qTeamVisit = QTeamVisit.teamVisit;

        return jpaQueryFactory
                .selectFrom(qTeamVisit)
                .where(
                        qTeamVisit
                                .visitedTeamId
                                .eq(visitedTeamId)
                                .and(qTeamVisit.profile.id.eq(visitorProfileId)))
                .fetchFirst();
    }

    @Override
    public boolean existsByVisitedTeamIdAndVisitorProfileId(
            final Long visitedTeamId, final Long visitorProfileId) {
        QTeamVisit qTeamVisit = QTeamVisit.teamVisit;

        return jpaQueryFactory
                        .selectOne()
                        .from(qTeamVisit)
                        .where(
                                qTeamVisit
                                        .visitedTeamId
                                        .eq(visitedTeamId)
                                        .and(qTeamVisit.profile.id.eq(visitorProfileId)))
                        .fetchFirst()
                != null;
    }

    @Override
    public void removeVisitorByVisitorProfileId(final Long visitorProfileId) {
        QTeamVisit qTeamVisit = QTeamVisit.teamVisit;

        jpaQueryFactory
                .delete(qTeamVisit)
                .where(qTeamVisit.profile.id.eq(visitorProfileId))
                .execute();

        entityManager.flush();
        entityManager.clear();
    }

    @Override
    public void removeVisitorByVisitedTeamId(final Long teamId) {
        QTeamVisit qTeamVisit = QTeamVisit.teamVisit;

        jpaQueryFactory.delete(qTeamVisit).where(qTeamVisit.visitedTeamId.eq(teamId)).execute();

        entityManager.flush();
        entityManager.clear();
    }

    @Override
    public void updateVisitTime(final TeamVisit teamVisit) {
        QTeamVisit qTeamVisit = QTeamVisit.teamVisit;

        long affectedRows =
                jpaQueryFactory
                        .update(qTeamVisit)
                        .set(qTeamVisit.visitTime, teamVisit.getVisitTime())
                        .where(
                                qTeamVisit
                                        .visitedTeamId
                                        .eq(teamVisit.getVisitedTeamId())
                                        .and(
                                                qTeamVisit.profile.id.eq(
                                                        teamVisit.getProfile().getId())))
                        .execute();

        // 변경 사항을 데이터베이스에서 다시 조회
        if (affectedRows > 0) {
            entityManager.flush(); // 동기화
            entityManager.clear(); // 캐시 무효화
        }
    }
}
