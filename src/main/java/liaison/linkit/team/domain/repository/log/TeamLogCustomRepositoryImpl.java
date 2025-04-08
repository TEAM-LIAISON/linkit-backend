package liaison.linkit.team.domain.repository.log;

import static liaison.linkit.global.type.StatusType.USABLE;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.type.LogType;
import liaison.linkit.team.domain.log.QTeamLog;
import liaison.linkit.team.domain.log.QTeamLogImage;
import liaison.linkit.team.domain.log.TeamLog;
import liaison.linkit.team.domain.team.QTeam;
import liaison.linkit.team.presentation.log.dto.TeamLogDynamicResponse;
import liaison.linkit.team.presentation.log.dto.TeamLogRequestDTO.UpdateTeamLogRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TeamLogCustomRepositoryImpl implements TeamLogCustomRepository {

    private final JPAQueryFactory queryFactory;

    @PersistenceContext private EntityManager entityManager; // EntityManager 주입

    @Override
    public List<TeamLog> getTeamLogs(final Long teamId) {
        QTeamLog qTeamLog = QTeamLog.teamLog;

        return queryFactory
                .selectFrom(qTeamLog)
                .where(qTeamLog.team.id.eq(teamId))
                .orderBy(
                        // 대표 로그는 1, 일반 로그는 0으로 부여하고, 내림차순 정렬하여 대표 로그가 항상 최상단에 오도록 함
                        new CaseBuilder()
                                .when(qTeamLog.logType.eq(LogType.REPRESENTATIVE_LOG))
                                .then(1)
                                .otherwise(0)
                                .desc(),
                        // 그 후 나머지 로그는 수정일(modifiedAt)을 내림차순 정렬 (최신 로그가 위쪽에 오도록)
                        qTeamLog.createdAt.desc())
                .fetch();
    }

    @Override
    public List<TeamLog> getTeamLogsPublic(final Long teamId) {
        QTeamLog qTeamLog = QTeamLog.teamLog;

        return queryFactory
                .selectFrom(qTeamLog)
                .where(qTeamLog.team.id.eq(teamId).and(qTeamLog.isLogPublic.eq(true)))
                .orderBy(
                        // 대표 로그는 1, 일반 로그는 0으로 부여하고, 내림차순 정렬하여 대표 로그가 항상 최상단에 오도록 함
                        new CaseBuilder()
                                .when(qTeamLog.logType.eq(LogType.REPRESENTATIVE_LOG))
                                .then(1)
                                .otherwise(0)
                                .desc(),
                        // 그 후 나머지 로그는 수정일(modifiedAt)을 내림차순 정렬 (최신 로그가 위쪽에 오도록)
                        qTeamLog.createdAt.desc())
                .fetch();
    }

    @Override
    public void updateTeamLogTypeRepresent(final TeamLog teamLog) {
        QTeamLog qTeamLog = QTeamLog.teamLog;

        // QueryDSL을 사용하여 데이터베이스에서 ProfileLog 엔티티를 업데이트
        long updatedCount =
                queryFactory
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
    public void updateTeamLogTypeGeneral(final TeamLog teamLog) {
        QTeamLog qTeamLog = QTeamLog.teamLog;

        // QueryDSL을 사용하여 데이터베이스에서 ProfileLog 엔티티를 업데이트
        long updatedCount =
                queryFactory
                        .update(qTeamLog)
                        .set(qTeamLog.logType, LogType.GENERAL_LOG)
                        .where(qTeamLog.id.eq(teamLog.getId()))
                        .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) { // 업데이트 성공 확인
            teamLog.setLogType(LogType.GENERAL_LOG); // 메모리 내 객체 업데이트
        } else {
            throw new IllegalStateException("팀 로그 업데이트 실패");
        }
    }

    @Override
    public Optional<TeamLog> findRepresentativeTeamLog(final Long teamId) {
        QTeamLog qTeamLog = QTeamLog.teamLog;

        TeamLog teamLog =
                queryFactory
                        .selectFrom(qTeamLog)
                        .where(
                                qTeamLog.team
                                        .id
                                        .eq(teamId)
                                        .and(qTeamLog.logType.eq(LogType.REPRESENTATIVE_LOG)))
                        .fetchFirst();

        return Optional.ofNullable(teamLog);
    }

    @Override
    public Optional<TeamLog> findRepresentativePublicTeamLog(final Long teamId) {
        QTeamLog qTeamLog = QTeamLog.teamLog;

        TeamLog teamLog =
                queryFactory
                        .selectFrom(qTeamLog)
                        .where(
                                qTeamLog.team
                                        .id
                                        .eq(teamId)
                                        .and(qTeamLog.logType.eq(LogType.REPRESENTATIVE_LOG))
                                        .and(qTeamLog.isLogPublic.eq(true)))
                        .fetchFirst();

        return Optional.ofNullable(teamLog);
    }

    @Override
    public TeamLog updateTeamLogPublicState(
            final TeamLog teamLog, final boolean isTeamLogCurrentPublicState) {
        QTeamLog qTeamLog = QTeamLog.teamLog;

        // QueryDSL을 사용하여 데이터베이스에서 ProfileLog 엔티티를 업데이트
        long updatedCount =
                queryFactory
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
                                qTeamLog.team
                                        .id
                                        .eq(teamId)
                                        .and(qTeamLog.logType.eq(LogType.REPRESENTATIVE_LOG)))
                        .fetchFirst()
                != null;
    }

    @Override
    public boolean existsRepresentativePublicTeamLogByTeam(final Long teamId) {
        QTeamLog qTeamLog = QTeamLog.teamLog;

        return queryFactory
                        .selectOne()
                        .from(qTeamLog)
                        .where(
                                qTeamLog.team
                                        .id
                                        .eq(teamId)
                                        .and(qTeamLog.logType.eq(LogType.REPRESENTATIVE_LOG))
                                        .and(qTeamLog.isLogPublic.eq(true)))
                        .fetchFirst()
                != null;
    }

    @Override
    public TeamLog updateTeamLog(
            final TeamLog teamLog, final UpdateTeamLogRequest updateTeamLogRequest) {
        QTeamLog qTeamLog = QTeamLog.teamLog;

        long updatedCount =
                queryFactory
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

    @Override
    public List<TeamLog> findTopView(final int limit) {
        QTeamLog qTeamLog = QTeamLog.teamLog;

        return queryFactory
                .selectFrom(qTeamLog)
                .where(qTeamLog.isLogPublic.eq(true)) // 공개 여부가 true인 것만
                .orderBy(qTeamLog.viewCount.desc()) // 조회수 높은 순
                .limit(limit)
                .fetch();
    }

    @Override
    public void deleteAllTeamLogs(final Long teamId) {
        QTeamLog qTeamLog = QTeamLog.teamLog;
        QTeamLogImage qTeamLogImage = QTeamLogImage.teamLogImage;

        // 1) 먼저, teamId로 해당되는 모든 TeamLog의 ID들을 조회
        List<Long> teamLogIds =
                queryFactory
                        .select(qTeamLog.id)
                        .from(qTeamLog)
                        .where(qTeamLog.team.id.eq(teamId))
                        .fetch();

        if (!teamLogIds.isEmpty()) {
            // 2) TeamLogImage 삭제 (teamLog_id IN (...))
            long deletedImageCount =
                    queryFactory
                            .delete(qTeamLogImage)
                            .where(qTeamLogImage.teamLog.id.in(teamLogIds))
                            .execute();

            // 3) TeamLog 삭제
            long deletedLogCount =
                    queryFactory.delete(qTeamLog).where(qTeamLog.id.in(teamLogIds)).execute();
        }
    }

    @Override
    public List<TeamLogDynamicResponse> findAllDynamicVariablesWithTeamLog() {
        QTeamLog qTeamLog = QTeamLog.teamLog;
        QTeam qTeam = QTeam.team;

        return queryFactory
                .select(
                        Projections.constructor(
                                TeamLogDynamicResponse.class,
                                qTeam.teamName,
                                qTeam.teamCode,
                                qTeamLog.id,
                                qTeamLog.createdAt))
                .from(qTeamLog)
                .leftJoin(qTeamLog.team, qTeam)
                .where(qTeam.status.eq(USABLE).and(qTeam.isTeamPublic.eq(true)))
                .orderBy(qTeam.id.desc())
                .fetch();
    }
}
