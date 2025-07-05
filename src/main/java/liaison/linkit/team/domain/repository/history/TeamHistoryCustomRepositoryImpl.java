package liaison.linkit.team.domain.repository.history;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.history.QTeamHistory;
import liaison.linkit.team.domain.history.TeamHistory;
import liaison.linkit.team.presentation.history.dto.TeamHistoryRequestDTO.UpdateTeamHistoryRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TeamHistoryCustomRepositoryImpl implements TeamHistoryCustomRepository {

    private final JPAQueryFactory queryFactory;

    @PersistenceContext private EntityManager entityManager; // EntityManager 주입

    private static final Logger log =
            LoggerFactory.getLogger(TeamHistoryCustomRepositoryImpl.class);

    @Override
    public List<TeamHistory> getTeamHistories(final String teamCode) {
        QTeamHistory qTeamHistory = QTeamHistory.teamHistory;

        return queryFactory
                .selectFrom(qTeamHistory)
                .where(qTeamHistory.team.teamCode.eq(teamCode))
                .fetch();
    }

    @Override
    public TeamHistory updateTeamHistory(
            final Long teamHistoryId, final UpdateTeamHistoryRequest updateTeamHistoryRequest) {
        QTeamHistory qTeamHistory = QTeamHistory.teamHistory;

        // 프로필 활동 업데이트
        long updatedCount =
                queryFactory
                        .update(qTeamHistory)
                        .set(qTeamHistory.historyName, updateTeamHistoryRequest.getHistoryName())
                        .set(
                                qTeamHistory.historyStartDate,
                                updateTeamHistoryRequest.getHistoryStartDate())
                        .set(
                                qTeamHistory.historyEndDate,
                                updateTeamHistoryRequest.getHistoryEndDate())
                        .set(
                                qTeamHistory.isHistoryInProgress,
                                updateTeamHistoryRequest.getIsHistoryInProgress())
                        .set(
                                qTeamHistory.historyDescription,
                                updateTeamHistoryRequest.getHistoryDescription())
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
    public void deleteAllTeamHistories(final Long teamId) {
        QTeamHistory qTeamHistory = QTeamHistory.teamHistory;

        long deletedCount =
                queryFactory.delete(qTeamHistory).where(qTeamHistory.team.id.eq(teamId)).execute();

        log.debug("Deleted {} team histories for team ID: {}", deletedCount, teamId);

        entityManager.flush();
        entityManager.clear();
    }
}
