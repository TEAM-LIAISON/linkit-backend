package liaison.linkit.team.domain.repository.log;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import liaison.linkit.profile.domain.type.LogType;
import liaison.linkit.team.domain.log.QTeamLog;
import liaison.linkit.team.domain.log.TeamLog;
import liaison.linkit.team.presentation.log.dto.TeamLogRequestDTO.UpdateTeamLogType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TeamLogCustomRepositoryImpl implements TeamLogCustomRepository {
    private final JPAQueryFactory queryFactory;

    @PersistenceContext
    private EntityManager entityManager; // EntityManager 주입

    @Override
    public List<TeamLog> getTeamLogs(final Long teamId) {
        QTeamLog qTeamLog = QTeamLog.teamLog;

        return queryFactory
                .selectFrom(qTeamLog)
                .where(qTeamLog.team.id.eq(teamId))
                .fetch();
    }

    @Override
    public TeamLog updateTeamLogType(final TeamLog teamLog, final UpdateTeamLogType updateTeamLogType) {
        QTeamLog qTeamLog = QTeamLog.teamLog;

        // QueryDSL을 사용하여 데이터베이스에서 ProfileLog 엔티티를 업데이트
        long updatedCount = queryFactory
                .update(qTeamLog)
                .set(qTeamLog.logType, updateTeamLogType.getLogType())
                .where(qTeamLog.id.eq(teamLog.getId()))
                .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) { // 업데이트 성공 확인
            teamLog.setLogType(updateTeamLogType.getLogType()); // 메모리 내 객체 업데이트
            return teamLog;
        } else {
            throw new IllegalStateException("프로필 로그 업데이트 실패");
        }
    }

    @Override
    public Optional<TeamLog> findRepresentativeTeamLog(final Long teamId) {
        QTeamLog qTeamLog = QTeamLog.teamLog;

        TeamLog teamLog = queryFactory
                .selectFrom(qTeamLog)
                .where(
                        qTeamLog.team.id.eq(teamId)
                                .and(qTeamLog.logType.eq(LogType.REPRESENTATIVE_LOG))
                )
                .fetchFirst();

        return Optional.ofNullable(teamLog);
    }

    @Override
    public boolean existsTeamLogByTeamId(final Long teamId) {
        QTeamLog qTeamLog = QTeamLog.teamLog;

        return queryFactory
                .selectOne()
                .from(qTeamLog)
                .where(qTeamLog.team.id.eq(teamId))
                .fetchFirst() != null;
    }
}
