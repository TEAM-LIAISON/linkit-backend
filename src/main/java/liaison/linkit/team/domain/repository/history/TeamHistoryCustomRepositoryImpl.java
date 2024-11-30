package liaison.linkit.team.domain.repository.history;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import liaison.linkit.team.domain.QTeamHistory;
import liaison.linkit.team.domain.TeamHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TeamHistoryCustomRepositoryImpl implements TeamHistoryCustomRepository {

    private final JPAQueryFactory queryFactory;

    @PersistenceContext
    private EntityManager entityManager; // EntityManager 주입

    @Override
    public List<TeamHistory> getTeamHistories(final Long teamId) {
        QTeamHistory qTeamHistory = QTeamHistory.teamHistory;

        return queryFactory
                .selectFrom(qTeamHistory)
                .where(qTeamHistory.team.id.eq(teamId))
                .fetch();
    }
}
