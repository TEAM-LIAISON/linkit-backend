package liaison.linkit.profile.domain.repository.log;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.member.domain.QMember;
import liaison.linkit.member.domain.type.MemberState;
import liaison.linkit.profile.domain.log.ProfileLog;
import liaison.linkit.profile.domain.log.QProfileLog;
import liaison.linkit.profile.domain.log.QProfileLogImage;
import liaison.linkit.profile.domain.profile.QProfile;
import liaison.linkit.profile.domain.type.LogType;
import liaison.linkit.profile.presentation.log.dto.ProfileLogDynamicResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogRequestDTO.UpdateProfileLogRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProfileLogCustomRepositoryImpl implements ProfileLogCustomRepository {

    private final JPAQueryFactory queryFactory;

    @PersistenceContext private EntityManager entityManager; // EntityManager 주입

    @Override
    public List<ProfileLog> getProfileLogs(final Long memberId) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        return queryFactory
                .selectFrom(qProfileLog)
                .where(qProfileLog.profile.member.id.eq(memberId))
                .orderBy(
                        // 대표 로그는 1, 일반 로그는 0으로 부여하고, 내림차순 정렬하여 대표 로그가 항상 최상단에 오도록 함
                        new CaseBuilder()
                                .when(qProfileLog.logType.eq(LogType.REPRESENTATIVE_LOG))
                                .then(1)
                                .otherwise(0)
                                .desc(),
                        // 그 후 나머지 로그는 수정일(modifiedAt)을 내림차순 정렬 (최신 로그가 위쪽에 오도록)
                        qProfileLog.createdAt.desc())
                .fetch();
    }

    @Override
    public List<ProfileLog> getProfileLogsPublic(final Long memberId) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        return queryFactory
                .selectFrom(qProfileLog)
                .where(
                        qProfileLog
                                .profile
                                .member
                                .id
                                .eq(memberId)
                                .and(qProfileLog.isLogPublic.eq(true)))
                .orderBy(
                        // 대표 로그는 1, 일반 로그는 0으로 부여하고, 내림차순 정렬하여 대표 로그가 항상 최상단에 오도록 함
                        new CaseBuilder()
                                .when(qProfileLog.logType.eq(LogType.REPRESENTATIVE_LOG))
                                .then(1)
                                .otherwise(0)
                                .desc(),
                        // 그 후 나머지 로그는 수정일(modifiedAt)을 내림차순 정렬 (최신 로그가 위쪽에 오도록)
                        qProfileLog.createdAt.desc())
                .fetch();
    }

    @Override
    public ProfileLog updateProfileLogTypeRepresent(final ProfileLog profileLog) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        // QueryDSL을 사용하여 데이터베이스에서 ProfileLog 엔티티를 업데이트
        long updatedCount =
                queryFactory
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
    public void updateProfileLogTypeGeneral(final ProfileLog profileLog) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        // QueryDSL을 사용하여 데이터베이스에서 ProfileLog 엔티티를 업데이트
        long updatedCount =
                queryFactory
                        .update(qProfileLog)
                        .set(qProfileLog.logType, LogType.GENERAL_LOG)
                        .where(qProfileLog.id.eq(profileLog.getId()))
                        .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) { // 업데이트 성공 확인
            profileLog.setLogType(LogType.GENERAL_LOG); // 메모리 내 객체 업데이트
        } else {
            throw new IllegalStateException("팀 로그 업데이트 실패");
        }
    }

    @Override
    public ProfileLog updateProfileLogPublicState(
            final ProfileLog profileLog, final boolean isProfileLogCurrentPublicState) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        // QueryDSL을 사용하여 데이터베이스에서 ProfileLog 엔티티를 업데이트
        long updatedCount =
                queryFactory
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

        ProfileLog profileLog =
                queryFactory
                        .selectFrom(qProfileLog)
                        .where(
                                qProfileLog
                                        .profile
                                        .id
                                        .eq(profileId)
                                        .and(qProfileLog.logType.eq(LogType.REPRESENTATIVE_LOG)))
                        .fetchFirst();

        return Optional.ofNullable(profileLog);
    }

    @Override
    public Optional<ProfileLog> findRepresentativePublicProfileLog(final Long profileId) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        ProfileLog profileLog =
                queryFactory
                        .selectFrom(qProfileLog)
                        .where(
                                qProfileLog
                                        .profile
                                        .id
                                        .eq(profileId)
                                        .and(qProfileLog.logType.eq(LogType.REPRESENTATIVE_LOG))
                                        .and(qProfileLog.isLogPublic.eq(true)))
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
                        .fetchFirst()
                != null;
    }

    @Override
    public boolean existsRepresentativeProfileLogByProfile(final Long profileId) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        return queryFactory
                        .selectOne()
                        .from(qProfileLog)
                        .where(
                                qProfileLog
                                        .profile
                                        .id
                                        .eq(profileId)
                                        .and(qProfileLog.logType.eq(LogType.REPRESENTATIVE_LOG)))
                        .fetchFirst()
                != null;
    }

    @Override
    public boolean existsRepresentativePublicProfileLogByProfile(final Long profileId) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        return queryFactory
                        .selectOne()
                        .from(qProfileLog)
                        .where(
                                qProfileLog
                                        .profile
                                        .id
                                        .eq(profileId)
                                        .and(qProfileLog.logType.eq(LogType.REPRESENTATIVE_LOG))
                                        .and(qProfileLog.isLogPublic.eq(true)))
                        .fetchFirst()
                != null;
    }

    @Override
    public ProfileLog updateProfileLog(
            final ProfileLog profileLog, final UpdateProfileLogRequest updateProfileLogRequest) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        // 프로필 활동 업데이트
        long updatedCount =
                queryFactory
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
                .orderBy(qProfileLog.viewCount.desc()) // 조회수 높은 순
                .limit(limit)
                .fetch();
    }

    @Override
    public void deleteAllProfileLogs(final Long profileId) {
        QProfileLog qProfileLog = QProfileLog.profileLog;
        QProfileLogImage qProfileLogImage =
                QProfileLogImage.profileLogImage; // 가정: ProfileLogImage가 있다면

        // 1) 삭제 대상 ProfileLog의 ID 목록 조회
        List<Long> profileLogIds =
                queryFactory
                        .select(qProfileLog.id)
                        .from(qProfileLog)
                        .where(qProfileLog.profile.id.eq(profileId))
                        .fetch();

        if (profileLogIds.isEmpty()) {
            return;
        }

        // 2) ProfileLogImage 먼저 삭제 (자식 테이블)
        long deletedImageCount =
                queryFactory
                        .delete(qProfileLogImage)
                        .where(qProfileLogImage.profileLog.id.in(profileLogIds))
                        .execute();

        // 3) ProfileLog 삭제 (부모 테이블)
        long deletedLogCount =
                queryFactory.delete(qProfileLog).where(qProfileLog.id.in(profileLogIds)).execute();
    }

    @Override
    public List<ProfileLogDynamicResponse> findAllDynamicVariablesWithProfileLog() {
        QProfileLog qProfileLog = QProfileLog.profileLog;
        QProfile qProfile = QProfile.profile;
        QMember qMember = QMember.member;

        return queryFactory
                .select(
                        Projections.constructor(
                                ProfileLogDynamicResponse.class,
                                qMember.emailId,
                                qMember.memberBasicInform.memberName,
                                qProfileLog.id,
                                qProfileLog.createdAt))
                .from(qProfileLog)
                .leftJoin(qProfileLog.profile, qProfile)
                .leftJoin(qProfile.member, qMember)
                .where(
                        qMember.memberState
                                .eq(MemberState.ACTIVE)
                                .and(qProfile.isProfilePublic.eq(true)))
                .orderBy(qMember.id.desc())
                .fetch();
    }

    @Override
    public ProfileLog findRandomProfileLog() {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        return queryFactory
                .selectFrom(qProfileLog)
                .where(qProfileLog.isLogPublic.isTrue()) // 공개된 로그만 대상으로
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(1)
                .fetchOne();
    }
}
