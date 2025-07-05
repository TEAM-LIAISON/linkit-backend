package liaison.linkit.visit.domain.repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.visit.domain.ProfileVisit;
import liaison.linkit.visit.domain.QProfileVisit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ProfileVisitCustomRepositoryImpl implements ProfileVisitCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext private EntityManager entityManager; // EntityManager 주입

    @Override
    public List<ProfileVisit> getProfileVisitsByVisitedProfileId(final Long visitedProfileId) {
        QProfileVisit qProfileVisit = QProfileVisit.profileVisit;

        return jpaQueryFactory
                .selectFrom(qProfileVisit)
                .where(qProfileVisit.visitedProfileId.eq(visitedProfileId))
                .fetch();
    }

    @Override
    public List<ProfileVisit> getOneWeekAgoProfileVisitsProfileVisits(
            final LocalDateTime oneWeekAgo) {
        QProfileVisit qProfileVisit = QProfileVisit.profileVisit;

        return jpaQueryFactory
                .selectFrom(qProfileVisit)
                .where(qProfileVisit.visitTime.after(oneWeekAgo))
                .orderBy(qProfileVisit.visitTime.desc())
                .fetch();
    }

    @Override
    public boolean existsByVisitedProfileIdAndVisitorProfileId(
            final Long visitedProfileId, final Long visitorProfileId) {
        QProfileVisit qProfileVisit = QProfileVisit.profileVisit;

        return jpaQueryFactory
                        .selectOne()
                        .from(qProfileVisit)
                        .where(
                                qProfileVisit
                                        .visitedProfileId
                                        .eq(visitedProfileId)
                                        .and(qProfileVisit.profile.id.eq(visitorProfileId)))
                        .fetchFirst()
                != null;
    }

    @Override
    public void removeVisitorByVisitorProfileId(final Long visitorProfileId) {
        QProfileVisit qProfileVisit = QProfileVisit.profileVisit;

        jpaQueryFactory
                .delete(qProfileVisit)
                .where(qProfileVisit.profile.id.eq(visitorProfileId))
                .execute();

        entityManager.flush();
        entityManager.clear();
    }

    @Override
    public void removeVisitorByVisitedProfileId(final Long visitedProfileId) {
        QProfileVisit qProfileVisit = QProfileVisit.profileVisit;

        jpaQueryFactory
                .delete(qProfileVisit)
                .where(qProfileVisit.visitedProfileId.eq(visitedProfileId))
                .execute();

        entityManager.flush();
        entityManager.clear();
    }

    @Override
    public ProfileVisit getProfileVisitByVisitedProfileIdAndVisitorProfileId(
            final Long visitedProfileId, final Long visitorProfileId) {
        QProfileVisit qProfileVisit = QProfileVisit.profileVisit;

        return jpaQueryFactory
                .selectFrom(qProfileVisit)
                .where(
                        qProfileVisit
                                .visitedProfileId
                                .eq(visitedProfileId)
                                .and(qProfileVisit.profile.id.eq(visitorProfileId)))
                .fetchFirst();
    }

    @Override
    public void updateVisitTime(final ProfileVisit profileVisit) {
        QProfileVisit qProfileVisit = QProfileVisit.profileVisit;

        long affectedRows =
                jpaQueryFactory
                        .update(qProfileVisit)
                        .set(qProfileVisit.visitTime, profileVisit.getVisitTime())
                        .where(
                                qProfileVisit
                                        .visitedProfileId
                                        .eq(profileVisit.getVisitedProfileId())
                                        .and(
                                                qProfileVisit.profile.id.eq(
                                                        profileVisit.getProfile().getId())))
                        .execute();

        // 변경 사항을 데이터베이스에서 다시 조회
        if (affectedRows > 0) {
            entityManager.flush(); // 동기화
            entityManager.clear(); // 캐시 무효화
        }
    }

    /**
     * 프로필별 일주일 이내 방문자 수를 계산합니다. visitedProfileId 기준으로 집계합니다.
     *
     * @return 방문받은 프로필 ID를 키로, 방문자 수를 값으로 하는 맵
     */
    @Override
    public Map<Long, Long> countVisitsPerProfileWithinLastWeek() {
        QProfileVisit qProfileVisit = QProfileVisit.profileVisit;
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        List<Tuple> results =
                queryFactory
                        .select(qProfileVisit.visitedProfileId, qProfileVisit.count())
                        .from(qProfileVisit)
                        .where(qProfileVisit.visitTime.after(oneWeekAgo))
                        .groupBy(qProfileVisit.visitedProfileId)
                        .fetch();

        Map<Long, Long> visitCountMap = new HashMap<>();

        // 안전하게 값을 Map에 추가
        for (Tuple tuple : results) {
            Long profileId = tuple.get(qProfileVisit.visitedProfileId);
            Long count = tuple.get(qProfileVisit.count());

            if (profileId != null) {
                visitCountMap.put(profileId, count != null ? count : 0L);
            }
        }

        return visitCountMap;
    }
}
