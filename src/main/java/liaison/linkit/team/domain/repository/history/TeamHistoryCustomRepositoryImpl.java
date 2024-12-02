package liaison.linkit.team.domain.repository.history;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import liaison.linkit.team.domain.QTeamHistory;
import liaison.linkit.team.domain.TeamHistory;
import liaison.linkit.team.presentation.history.dto.TeamHistoryRequestDTO.UpdateTeamHistoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TeamHistoryCustomRepositoryImpl implements TeamHistoryCustomRepository {

    private final JPAQueryFactory queryFactory;

    @PersistenceContext
    private EntityManager entityManager; // EntityManager 주입

    @Override
    public List<TeamHistory> getTeamHistories(final String teamName) {
        QTeamHistory qTeamHistory = QTeamHistory.teamHistory;

        return queryFactory
                .selectFrom(qTeamHistory)
                .where(qTeamHistory.team.teamName.eq(teamName))
                .fetch();
    }

    @Override
    public TeamHistory updateTeamHistory(final Long teamHistoryId, final UpdateTeamHistoryRequest updateTeamHistoryRequest) {
        QTeamHistory qTeamHistory = QTeamHistory.teamHistory;

        // 프로필 활동 업데이트
        long updatedCount = queryFactory
                .update(qTeamHistory)
                .set(qTeamHistory.historyName, updateTeamHistoryRequest.getHistoryName())
                .set(qTeamHistory.historyStartDate, updateTeamHistoryRequest.getHistoryStartDate())
                .set(qTeamHistory.historyEndDate, updateTeamHistoryRequest.getHistoryEndDate())
                .set(qTeamHistory.isHistoryInProgress, updateTeamHistoryRequest.getIsHistoryInProgress())
                .set(qTeamHistory.historyDescription, updateTeamHistoryRequest.getHistoryDescription())
                .where(qTeamHistory.id.eq(teamHistoryId))
                .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) {
            return queryFactory
                    .selectFrom(qTeamHistory)
                    .where(qTeamHistory.id.eq(teamHistoryId))
                    .fetchOne();
        } else {
            return null;
        }
    }

    @Override
    public boolean existsByTeamId(final Long teamId) {
        QTeamHistory qTeamHistory = QTeamHistory.teamHistory;

        return queryFactory
                .selectOne()
                .from(qTeamHistory)
                .where(qTeamHistory.team.id.eq(teamId))
                .fetchFirst() != null;
    }
}
