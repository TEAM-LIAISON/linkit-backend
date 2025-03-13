package liaison.linkit.visit.domain.repository;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.visit.domain.QTeamVisit;
import liaison.linkit.visit.domain.TeamVisit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TeamVisitCustomRepositoryImpl implements TeamVisitCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<TeamVisit> getTeamVisitsByVisitedTeamId(final Long visitedTeamId) {
        QTeamVisit qTeamVisit = QTeamVisit.teamVisit;

        return jpaQueryFactory
                .selectFrom(qTeamVisit)
                .where(qTeamVisit.visitedTeamId.eq(visitedTeamId))
                .fetch();
    }
}
