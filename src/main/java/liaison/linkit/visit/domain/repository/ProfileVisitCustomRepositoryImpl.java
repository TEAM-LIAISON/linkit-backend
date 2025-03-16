package liaison.linkit.visit.domain.repository;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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
}
