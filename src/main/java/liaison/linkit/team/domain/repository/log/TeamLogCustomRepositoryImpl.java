package liaison.linkit.team.domain.repository.log;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import liaison.linkit.profile.domain.type.LogType;
import liaison.linkit.team.domain.log.QTeamLog;
import liaison.linkit.team.domain.log.TeamLog;
import liaison.linkit.team.presentation.log.dto.TeamLogRequestDTO.UpdateTeamLogRequest;
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
    public List<TeamLog> getTeamLogsPublic(final Long teamId) {
        QTeamLog qTeamLog = QTeamLog.teamLog;

        return queryFactory
                .selectFrom(qTeamLog)
                .where(qTeamLog.team.id.eq(teamId)
                        .and(qTeamLog.isLogPublic.eq(true)))
                .fetch();
    }

    @Override
    public void updateTeamLogTypeRepresent(final TeamLog teamLog) {
        QTeamLog qTeamLog = QTeamLog.teamLog;

        // QueryDSL을 사용하여 데이터베이스에서 ProfileLog 엔티티를 업데이트
        long updatedCount = queryFactory
                .update(qTeamLog)
                .set(qTeamLog.logType, LogType.REPRESENTATIVE_LOG)
                .where(qTeamLog.id.eq(teamLog.getId()))
                .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) { // 업데이트 성공 확인
            teamLog.setLogType(LogType.REPRESENTATIVE_LOG); // 메모리 내 객체 업데이트
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
    public TeamLog updateTeamLogPublicState(final TeamLog teamLog, final boolean isTeamLogCurrentPublicState) {
        QTeamLog qTeamLog = QTeamLog.teamLog;

        // QueryDSL을 사용하여 데이터베이스에서 ProfileLog 엔티티를 업데이트
        long updatedCount = queryFactory
                .update(qTeamLog)
                .set(qTeamLog.isLogPublic, !isTeamLogCurrentPublicState)
                .where(qTeamLog.id.eq(teamLog.getId()))
                .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) { // 업데이트 성공 확인
            teamLog.setIsLogPublic(!isTeamLogCurrentPublicState); // 메모리 내 객체 업데이트
            return teamLog;
        } else {
            throw new IllegalStateException("프로필 로그 업데이트 실패");
        }
    }

    @Override
    public boolean existsRepresentativeTeamLogByTeam(final Long teamId) {
        QTeamLog qTeamLog = QTeamLog.teamLog;

        return queryFactory
                .selectOne()
                .from(qTeamLog)
                .where(
                        qTeamLog.team.id.eq(teamId)
                                .and(qTeamLog.logType.eq(LogType.REPRESENTATIVE_LOG))
                )
                .fetchFirst() != null;
    }

    @Override
    public TeamLog updateTeamLog(final TeamLog teamLog, final UpdateTeamLogRequest updateTeamLogRequest) {
        QTeamLog qTeamLog = QTeamLog.teamLog;

        long updatedCount = queryFactory
                .update(qTeamLog)
                .set(qTeamLog.logTitle, updateTeamLogRequest.getLogTitle())
                .set(qTeamLog.logContent, updateTeamLogRequest.getLogContent())
                .set(qTeamLog.isLogPublic, updateTeamLogRequest.getIsLogPublic())
                .where(qTeamLog.id.eq(teamLog.getId()))
                .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) {
            // 업데이트된 ProfileActivity 조회 및 반환
            return queryFactory
                    .selectFrom(qTeamLog)
                    .where(qTeamLog.id.eq(teamLog.getId()))
                    .fetchOne();
        } else {
            return null;
        }
    }
}
