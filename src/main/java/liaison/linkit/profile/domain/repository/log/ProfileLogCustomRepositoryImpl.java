package liaison.linkit.profile.domain.repository.log;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import liaison.linkit.profile.domain.log.ProfileLog;
import liaison.linkit.profile.domain.log.QProfileLog;
import liaison.linkit.profile.domain.type.LogType;
import liaison.linkit.profile.presentation.log.dto.ProfileLogRequestDTO.UpdateProfileLogRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProfileLogCustomRepositoryImpl implements ProfileLogCustomRepository {

    private final JPAQueryFactory queryFactory;

    @PersistenceContext
    private EntityManager entityManager; // EntityManager 주입

    @Override
    public List<ProfileLog> getProfileLogs(final Long memberId) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        return queryFactory
                .selectFrom(qProfileLog)
                .where(qProfileLog.profile.member.id.eq(memberId))
                .fetch();
    }

    @Override
    public ProfileLog updateProfileLogTypeRepresent(final ProfileLog profileLog) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        // QueryDSL을 사용하여 데이터베이스에서 ProfileLog 엔티티를 업데이트
        long updatedCount = queryFactory
                .update(qProfileLog)
                .set(qProfileLog.logType, LogType.REPRESENTATIVE_LOG)
                .where(qProfileLog.id.eq(profileLog.getId()))
                .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) { // 업데이트 성공 확인
            profileLog.setLogType(LogType.REPRESENTATIVE_LOG); // 메모리 내 객체 업데이트
            return profileLog;
        } else {
            throw new IllegalStateException("프로필 로그 업데이트 실패");
        }
    }

    @Override
    public ProfileLog updateProfileLogTypeToNormal(final ProfileLog profileLog) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        // QueryDSL을 사용하여 데이터베이스에서 ProfileLog 엔티티를 업데이트
        long updatedCount = queryFactory
                .update(qProfileLog)
                .set(qProfileLog.logType, LogType.GENERAL_LOG)
                .where(qProfileLog.id.eq(profileLog.getId()))
                .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) { // 업데이트 성공 확인
            profileLog.setLogType(LogType.GENERAL_LOG); // 메모리 내 객체 업데이트
            return profileLog;
        } else {
            throw new IllegalStateException("대표 로그 일반 로그로 업데이트 실패");
        }
    }

    @Override
    public ProfileLog updateProfileLogPublicState(final ProfileLog profileLog, final boolean isProfileLogCurrentPublicState) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        // QueryDSL을 사용하여 데이터베이스에서 ProfileLog 엔티티를 업데이트
        long updatedCount = queryFactory
                .update(qProfileLog)
                .set(qProfileLog.isLogPublic, !isProfileLogCurrentPublicState)
                .where(qProfileLog.id.eq(profileLog.getId()))
                .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) { // 업데이트 성공 확인
            profileLog.setIsLogPublic(!isProfileLogCurrentPublicState); // 메모리 내 객체 업데이트
            return profileLog;
        } else {
            throw new IllegalStateException("프로필 로그 업데이트 실패");
        }
    }

    @Override
    public Optional<ProfileLog> findRepresentativeProfileLog(final Long profileId) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        ProfileLog profileLog = queryFactory
                .selectFrom(qProfileLog)
                .where(
                        qProfileLog.profile.id.eq(profileId)
                                .and(qProfileLog.logType.eq(LogType.REPRESENTATIVE_LOG))
                )
                .fetchFirst();

        return Optional.ofNullable(profileLog);
    }

    @Override
    public boolean existsProfileLogByProfileId(final Long profileId) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        return queryFactory
                .selectOne()
                .from(qProfileLog)
                .where(qProfileLog.profile.id.eq(profileId))
                .fetchFirst() != null;
    }

    @Override
    public boolean existsRepresentativeProfileLogByProfile(final Long profileId) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        return queryFactory
                .selectOne()
                .from(qProfileLog)
                .where(
                        qProfileLog.profile.id.eq(profileId)
                                .and(qProfileLog.logType.eq(LogType.REPRESENTATIVE_LOG))
                )
                .fetchFirst() != null;
    }

    @Override
    public ProfileLog updateProfileLog(final ProfileLog profileLog, final UpdateProfileLogRequest updateProfileLogRequest) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        // 프로필 활동 업데이트
        long updatedCount = queryFactory
                .update(qProfileLog)
                .set(qProfileLog.logTitle, updateProfileLogRequest.getLogTitle())
                .set(qProfileLog.logContent, updateProfileLogRequest.getLogContent())
                .set(qProfileLog.isLogPublic, updateProfileLogRequest.getIsLogPublic())
                .where(qProfileLog.id.eq(profileLog.getId()))
                .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) {
            // 업데이트된 ProfileActivity 조회 및 반환
            return queryFactory
                    .selectFrom(qProfileLog)
                    .where(qProfileLog.id.eq(profileLog.getId()))
                    .fetchOne();
        } else {
            return null;
        }
    }

    @Override
    public List<ProfileLog> findTopView(final int limit) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        return queryFactory
                .selectFrom(qProfileLog)
                .where(qProfileLog.isLogPublic.eq(true)) // 공개 여부가 true인 것만
                .orderBy(qProfileLog.viewCount.desc())   // 조회수 높은 순
                .limit(limit)
                .fetch();
    }
}
