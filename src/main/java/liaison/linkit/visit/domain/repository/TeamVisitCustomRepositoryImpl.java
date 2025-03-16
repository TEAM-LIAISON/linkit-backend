package liaison.linkit.visit.domain.repository;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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
}
