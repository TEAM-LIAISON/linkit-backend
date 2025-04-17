package liaison.linkit.profile.domain.repository.profile;

import static liaison.linkit.global.type.StatusType.USABLE;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.common.domain.QPosition;
import liaison.linkit.common.domain.QProfileState;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.member.domain.QMember;
import liaison.linkit.profile.domain.position.QProfilePosition;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.profile.QProfile;
import liaison.linkit.profile.domain.region.QProfileRegion;
import liaison.linkit.profile.domain.region.QRegion;
import liaison.linkit.profile.domain.state.QProfileCurrentState;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.search.presentation.dto.profile.FlatProfileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ProfileCustomRepositoryImpl implements ProfileCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Profile> findByMemberId(Long memberId) {
        QProfile profile = QProfile.profile;

        Profile result =
                jpaQueryFactory
                        .selectFrom(profile)
                        .where(profile.member.id.eq(memberId))
                        .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Profile> findByEmailId(final String emailId) {
        QProfile profile = QProfile.profile;

        Profile result =
                jpaQueryFactory
                        .selectFrom(profile)
                        .where(profile.member.emailId.eq(emailId))
                        .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public boolean existsByMemberId(Long memberId) {
        QProfile profile = QProfile.profile;

        Integer count =
                jpaQueryFactory
                        .selectOne()
                        .from(profile)
                        .where(profile.member.id.eq(memberId))
                        .fetchFirst();

        return count != null;
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        QProfile profile = QProfile.profile;

        jpaQueryFactory
                .update(profile)
                .set(profile.status, StatusType.DELETED)
                .where(profile.member.id.eq(memberId))
                .execute();
    }

    public CursorResponse<Profile> findAllByFilteringWithCursor(
            final List<String> subPosition,
            final List<String> cityName,
            final List<String> profileStateName,
            final CursorRequest cursorRequest) {

        QProfile qProfile = QProfile.profile;
        QProfilePosition qProfilePosition = QProfilePosition.profilePosition;
        QPosition qPosition = QPosition.position;
        QProfileRegion qProfileRegion = QProfileRegion.profileRegion;
        QRegion qRegion = QRegion.region;
        QProfileCurrentState qProfileCurrentState = QProfileCurrentState.profileCurrentState;
        QProfileState qProfileState = QProfileState.profileState;

        try {
            // Step 1. emailId → profileId 커서 변환
            Long cursorProfileId = null;
            if (cursorRequest != null
                    && cursorRequest.hasNext()
                    && cursorRequest.cursor() != null) {
                String emailId = cursorRequest.cursor();
                cursorProfileId =
                        jpaQueryFactory
                                .select(qProfile.id)
                                .from(qProfile)
                                .where(qProfile.member.emailId.eq(emailId))
                                .fetchOne();
            }

            BooleanExpression baseCondition =
                    qProfile.status.eq(USABLE).and(qProfile.isProfilePublic.eq(true));

            if (cursorProfileId != null) {
                baseCondition = baseCondition.and(qProfile.id.lt(cursorProfileId));
            }

            JPAQuery<Profile> query =
                    jpaQueryFactory
                            .selectFrom(qProfile)
                            .distinct()
                            .leftJoin(qProfile.member)
                            .fetchJoin();

            if (isNotEmpty(subPosition)) {
                query =
                        query.leftJoin(qProfilePosition)
                                .on(qProfilePosition.profile.eq(qProfile))
                                .leftJoin(qPosition)
                                .on(qProfilePosition.position.eq(qPosition))
                                .where(qPosition.subPosition.in(subPosition));
            }

            if (isNotEmpty(cityName)) {
                query =
                        query.leftJoin(qProfileRegion)
                                .on(qProfileRegion.profile.eq(qProfile))
                                .leftJoin(qRegion)
                                .on(qProfileRegion.region.eq(qRegion))
                                .where(qRegion.cityName.in(cityName));
            }

            if (isNotEmpty(profileStateName)) {
                query =
                        query.leftJoin(qProfileCurrentState)
                                .on(qProfileCurrentState.profile.eq(qProfile))
                                .leftJoin(qProfileState)
                                .on(qProfileCurrentState.profileState.eq(qProfileState))
                                .where(qProfileState.profileStateName.in(profileStateName));
            }

            int requestedSize = Math.max(1, cursorRequest.size());
            int pageSize = (requestedSize % 6 == 0) ? requestedSize : (requestedSize / 6 + 1) * 6;

            List<Profile> content =
                    query.where(baseCondition)
                            .orderBy(qProfile.id.desc())
                            .limit(pageSize + 1)
                            .fetch();

            boolean hasNext = content.size() > pageSize;
            String nextCursor = hasNext ? content.get(pageSize).getMember().getEmailId() : null;
            if (hasNext) {
                content = content.subList(0, pageSize);
            }
            return CursorResponse.of(content, nextCursor);
        } catch (Exception e) {
            log.error("Error in findAllByFilteringWithCursor: {}", e.getMessage(), e);
            return CursorResponse.of(List.of(), null);
        }
    }

    private boolean isNotEmpty(List<?> list) {
        return list != null && !list.isEmpty();
    }

    public List<Profile> findByMarketingConsentAndMajorPosition(final String majorPosition) {
        QProfile qProfile = QProfile.profile;
        QProfilePosition qProfilePosition = QProfilePosition.profilePosition;
        QPosition qPosition = QPosition.position;

        return jpaQueryFactory
                .selectFrom(qProfile)
                .leftJoin(qProfilePosition)
                .on(qProfilePosition.profile.eq(qProfile))
                .leftJoin(qPosition)
                .on(qProfilePosition.position.eq(qPosition))
                .where(
                        qProfile.status.eq(StatusType.USABLE),
                        qProfile.isProfilePublic.eq(true),
                        qPosition.majorPosition.eq(majorPosition),
                        qProfile.member.memberBasicInform.marketingAgree.isTrue())
                .fetch();
    }

    public List<FlatProfileDTO> findHomeTopProfiles(int size) {
        QProfile qProfile = QProfile.profile;
        QMember qMember = QMember.member;
        QProfileRegion qProfileRegion = QProfileRegion.profileRegion;
        QRegion qRegion = QRegion.region;
        QProfilePosition qProfilePosition = QProfilePosition.profilePosition;
        QPosition qPosition = QPosition.position;
        QProfileCurrentState qProfileCurrentState = QProfileCurrentState.profileCurrentState;
        QProfileState qProfileState = QProfileState.profileState;

        // 1단계: 원하는 Profile ID들을 먼저 조회
        List<Long> targetProfileIds =
                jpaQueryFactory
                        .select(qProfile.id)
                        .from(qProfile)
                        .where(
                                qProfile.status
                                        .eq(StatusType.USABLE)
                                        .and(qProfile.isProfilePublic.eq(true))
                                        .and(
                                                qProfile.id.in(
                                                        58L, 57L, 55L, 73L, 63L, 74L, 77L, 37L)))
                        .orderBy(
                                new CaseBuilder()
                                        .when(qProfile.id.eq(58L))
                                        .then(0)
                                        .when(qProfile.id.eq(57L))
                                        .then(1)
                                        .when(qProfile.id.eq(55L))
                                        .then(2)
                                        .when(qProfile.id.eq(73L))
                                        .then(3)
                                        .when(qProfile.id.eq(63L))
                                        .then(4)
                                        .when(qProfile.id.eq(74L))
                                        .then(5)
                                        .when(qProfile.id.eq(77L))
                                        .then(6)
                                        .when(qProfile.id.eq(37L))
                                        .then(7)
                                        .otherwise(8)
                                        .asc())
                        .limit(size)
                        .fetch();

        // 2단계: 실제 데이터 조회
        return jpaQueryFactory
                .select(
                        Projections.fields(
                                FlatProfileDTO.class,
                                qProfile.id.as("profileId"),
                                qMember.memberBasicInform.memberName.as("memberName"),
                                qMember.emailId.as("emailId"),
                                qProfile.profileImagePath.as("profileImagePath"),
                                qPosition.majorPosition.as("majorPosition"),
                                qPosition.subPosition.as("subPosition"),
                                qRegion.cityName.as("cityName"),
                                qRegion.divisionName.as("divisionName"),
                                qProfileState.profileStateName.as("profileStateName")))
                .from(qProfile)
                .leftJoin(qProfile.member, qMember)
                .leftJoin(qMember.memberBasicInform)
                .leftJoin(qProfile.profileRegion, qProfileRegion)
                .leftJoin(qProfileRegion.region, qRegion)
                .leftJoin(qProfile.profilePositions, qProfilePosition)
                .leftJoin(qProfilePosition.position, qPosition)
                .leftJoin(qProfile.profileCurrentStates, qProfileCurrentState)
                .leftJoin(qProfileCurrentState.profileState, qProfileState)
                .where(qProfile.id.in(targetProfileIds))
                .orderBy(
                        new CaseBuilder()
                                .when(qProfile.id.eq(42L))
                                .then(0)
                                .when(qProfile.id.eq(58L))
                                .then(1)
                                .when(qProfile.id.eq(57L))
                                .then(2)
                                .when(qProfile.id.eq(55L))
                                .then(3)
                                .when(qProfile.id.eq(73L))
                                .then(4)
                                .when(qProfile.id.eq(63L))
                                .then(5)
                                .otherwise(6)
                                .asc())
                .fetch();
    }

    public List<FlatProfileDTO> findTopCompletionProfiles(int size) {
        QProfile qProfile = QProfile.profile;
        QMember qMember = QMember.member;
        QProfileRegion qProfileRegion = QProfileRegion.profileRegion;
        QRegion qRegion = QRegion.region;
        QProfilePosition qProfilePosition = QProfilePosition.profilePosition;
        QPosition qPosition = QPosition.position;
        QProfileCurrentState qProfileCurrentState = QProfileCurrentState.profileCurrentState;
        QProfileState qProfileState = QProfileState.profileState;

        // 1단계: 원하는 Profile ID들을 먼저 조회
        List<Long> targetProfileIds =
                jpaQueryFactory
                        .select(qProfile.id)
                        .from(qProfile)
                        .where(
                                qProfile.status
                                        .eq(StatusType.USABLE)
                                        .and(qProfile.isProfilePublic.eq(true))
                                        .and(
                                                qProfile.id.in(
                                                        58L, 57L, 55L, 73L, 63L, 74L, 77L, 37L)))
                        .orderBy(
                                new CaseBuilder()
                                        .when(qProfile.id.eq(58L))
                                        .then(0)
                                        .when(qProfile.id.eq(57L))
                                        .then(1)
                                        .when(qProfile.id.eq(55L))
                                        .then(2)
                                        .when(qProfile.id.eq(73L))
                                        .then(3)
                                        .when(qProfile.id.eq(63L))
                                        .then(4)
                                        .when(qProfile.id.eq(74L))
                                        .then(5)
                                        .when(qProfile.id.eq(77L))
                                        .then(6)
                                        .when(qProfile.id.eq(37L))
                                        .then(7)
                                        .otherwise(8)
                                        .asc())
                        .limit(size)
                        .fetch();

        // 2단계: 실제 데이터 조회
        return jpaQueryFactory
                .select(
                        Projections.fields(
                                FlatProfileDTO.class,
                                qProfile.id.as("profileId"),
                                qMember.memberBasicInform.memberName.as("memberName"),
                                qMember.emailId.as("emailId"),
                                qProfile.profileImagePath.as("profileImagePath"),
                                qPosition.majorPosition.as("majorPosition"),
                                qPosition.subPosition.as("subPosition"),
                                qRegion.cityName.as("cityName"),
                                qRegion.divisionName.as("divisionName"),
                                qProfileState.profileStateName.as("profileStateName")))
                .from(qProfile)
                .leftJoin(qProfile.member, qMember)
                .leftJoin(qMember.memberBasicInform)
                .leftJoin(qProfile.profileRegion, qProfileRegion)
                .leftJoin(qProfileRegion.region, qRegion)
                .leftJoin(qProfile.profilePositions, qProfilePosition)
                .leftJoin(qProfilePosition.position, qPosition)
                .leftJoin(qProfile.profileCurrentStates, qProfileCurrentState)
                .leftJoin(qProfileCurrentState.profileState, qProfileState)
                .where(qProfile.id.in(targetProfileIds))
                .orderBy(
                        new CaseBuilder()
                                .when(qProfile.id.eq(42L))
                                .then(0)
                                .when(qProfile.id.eq(58L))
                                .then(1)
                                .when(qProfile.id.eq(57L))
                                .then(2)
                                .when(qProfile.id.eq(55L))
                                .then(3)
                                .when(qProfile.id.eq(73L))
                                .then(4)
                                .when(qProfile.id.eq(63L))
                                .then(5)
                                .when(qProfile.id.eq(33L))
                                .then(6)
                                .when(qProfile.id.eq(26L))
                                .then(7)
                                .otherwise(8)
                                .asc())
                .fetch();
    }

    public List<FlatProfileDTO> findFlatProfilesWithoutCursor(
            List<Long> excludeProfileIds, int size) {
        QProfile qProfile = QProfile.profile;
        QMember qMember = QMember.member;
        QProfileRegion qProfileRegion = QProfileRegion.profileRegion;
        QRegion qRegion = QRegion.region;
        QProfilePosition qProfilePosition = QProfilePosition.profilePosition;
        QPosition qPosition = QPosition.position;
        QProfileCurrentState qProfileCurrentState = QProfileCurrentState.profileCurrentState;
        QProfileState qProfileState = QProfileState.profileState;

        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                FlatProfileDTO.class,
                                qProfile.id,
                                qMember.memberBasicInform.memberName,
                                qMember.emailId,
                                qProfile.profileImagePath,
                                qPosition.majorPosition,
                                qPosition.subPosition,
                                qRegion.cityName,
                                qRegion.divisionName,
                                qProfileState.profileStateName))
                .from(qProfile)
                .leftJoin(qProfile.member, qMember)
                .leftJoin(qMember.memberBasicInform)
                .leftJoin(qProfile.profileRegion, qProfileRegion)
                .leftJoin(qProfileRegion.region, qRegion)
                .leftJoin(qProfile.profilePositions, qProfilePosition)
                .leftJoin(qProfilePosition.position, qPosition)
                .leftJoin(qProfile.profileCurrentStates, qProfileCurrentState)
                .leftJoin(qProfileCurrentState.profileState, qProfileState)
                .where(
                        qProfile.status
                                .eq(StatusType.USABLE)
                                .and(qProfile.isProfilePublic.eq(true))
                                .and(qProfile.id.notIn(excludeProfileIds)))
                .orderBy(qProfile.createdAt.desc())
                .limit(size * 5)
                .fetch();
    }

    public List<FlatProfileDTO> findAllProfilesWithoutFilter(
            final List<Long> excludeProfileIds, final CursorRequest cursorRequest) {

        QProfile qProfile = QProfile.profile;
        QMember qMember = QMember.member;
        QProfileRegion qProfileRegion = QProfileRegion.profileRegion;
        QRegion qRegion = QRegion.region;
        QProfilePosition qProfilePosition = QProfilePosition.profilePosition;
        QPosition qPosition = QPosition.position;
        QProfileCurrentState qProfileCurrentState = QProfileCurrentState.profileCurrentState;
        QProfileState qProfileState = QProfileState.profileState;

        try {
            // Step 1. 커서 emailId → profileId + createdAt
            LocalDateTime cursorCreatedAt = null;
            if (cursorRequest != null
                    && cursorRequest.hasNext()
                    && cursorRequest.cursor() != null) {
                String emailId = cursorRequest.cursor();
                cursorCreatedAt =
                        jpaQueryFactory
                                .select(qProfile.createdAt)
                                .from(qProfile)
                                .where(qProfile.member.emailId.eq(emailId))
                                .fetchOne();
            }

            // Step 2. 조건 설정
            BooleanExpression condition =
                    qProfile.status.eq(StatusType.USABLE).and(qProfile.isProfilePublic.isTrue());

            if (excludeProfileIds != null && !excludeProfileIds.isEmpty()) {
                condition = condition.and(qProfile.id.notIn(excludeProfileIds));
            }

            if (cursorCreatedAt != null) {
                condition = condition.and(qProfile.createdAt.lt(cursorCreatedAt));
            }

            int requestedSize = Math.max(1, cursorRequest.size());
            int pageSize = (requestedSize % 6 == 0) ? requestedSize : (requestedSize / 6 + 1) * 6;

            // Step 3. 조회
            return jpaQueryFactory
                    .select(
                            Projections.constructor(
                                    FlatProfileDTO.class,
                                    qProfile.id,
                                    qMember.memberBasicInform.memberName,
                                    qMember.emailId,
                                    qProfile.profileImagePath,
                                    qPosition.majorPosition,
                                    qPosition.subPosition,
                                    qRegion.cityName,
                                    qRegion.divisionName,
                                    qProfileState.profileStateName))
                    .from(qProfile)
                    .leftJoin(qProfile.member, qMember)
                    .leftJoin(qMember.memberBasicInform)
                    .leftJoin(qProfile.profileRegion, qProfileRegion)
                    .leftJoin(qProfileRegion.region, qRegion)
                    .leftJoin(qProfile.profilePositions, qProfilePosition)
                    .leftJoin(qProfilePosition.position, qPosition)
                    .leftJoin(qProfile.profileCurrentStates, qProfileCurrentState)
                    .leftJoin(qProfileCurrentState.profileState, qProfileState)
                    .where(condition)
                    .orderBy(qProfile.createdAt.desc())
                    .limit(pageSize)
                    .fetch();

        } catch (Exception e) {
            log.error("Error in findAllProfilesWithoutFilter: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public List<FlatProfileDTO> findFilteredFlatProfilesWithCursor(
            final List<String> subPosition,
            final List<String> cityName,
            final List<String> profileStateName,
            final CursorRequest cursorRequest) {

        QProfile qProfile = QProfile.profile;
        QMember qMember = QMember.member;
        QProfileRegion qProfileRegion = QProfileRegion.profileRegion;
        QRegion qRegion = QRegion.region;
        QProfilePosition qProfilePosition = QProfilePosition.profilePosition;
        QPosition qPosition = QPosition.position;
        QProfileCurrentState qProfileCurrentState = QProfileCurrentState.profileCurrentState;
        QProfileState qProfileState = QProfileState.profileState;

        try {
            // Step 1. emailId → profileId 커서 변환
            LocalDateTime cursorCreatedAt = null;
            if (cursorRequest != null
                    && cursorRequest.hasNext()
                    && cursorRequest.cursor() != null) {
                String emailId = cursorRequest.cursor();
                cursorCreatedAt =
                        jpaQueryFactory
                                .select(qProfile.createdAt)
                                .from(qProfile)
                                .where(qProfile.member.emailId.eq(emailId))
                                .fetchOne();
            }

            // Step 2. 기본 조건
            BooleanExpression baseCondition =
                    qProfile.status.eq(USABLE).and(qProfile.isProfilePublic.isTrue());

            if (cursorCreatedAt != null) {
                baseCondition = baseCondition.and(qProfile.createdAt.lt(cursorCreatedAt));
            }

            int requestedSize = Math.max(1, cursorRequest.size());
            int pageSize = (requestedSize % 6 == 0) ? requestedSize : ((requestedSize / 6) + 1) * 6;

            // Step 3. 쿼리 구성
            var query =
                    jpaQueryFactory
                            .select(
                                    Projections.constructor(
                                            FlatProfileDTO.class,
                                            qProfile.id,
                                            qMember.memberBasicInform.memberName,
                                            qMember.emailId,
                                            qProfile.profileImagePath,
                                            qPosition.majorPosition,
                                            qPosition.subPosition,
                                            qRegion.cityName,
                                            qRegion.divisionName,
                                            qProfileState.profileStateName))
                            .from(qProfile)
                            .leftJoin(qProfile.member, qMember)
                            .leftJoin(qMember.memberBasicInform)
                            .leftJoin(qProfile.profileRegion, qProfileRegion)
                            .leftJoin(qProfileRegion.region, qRegion)
                            .leftJoin(qProfile.profilePositions, qProfilePosition)
                            .leftJoin(qProfilePosition.position, qPosition)
                            .leftJoin(qProfile.profileCurrentStates, qProfileCurrentState)
                            .leftJoin(qProfileCurrentState.profileState, qProfileState)
                            .where(baseCondition)
                            .orderBy(qProfile.createdAt.desc());

            // Step 4. 필터 적용
            if (isNotEmpty(subPosition)) {
                query = query.where(qPosition.subPosition.in(subPosition));
            }

            if (isNotEmpty(cityName)) {
                query = query.where(qRegion.cityName.in(cityName));
            }

            if (isNotEmpty(profileStateName)) {
                query = query.where(qProfileState.profileStateName.in(profileStateName));
            }

            // Step 5. 페이징 처리
            List<FlatProfileDTO> content = query.limit(pageSize + 1).fetch();

            // 필요시 커서 처리 정보는 추후 CursorResponse<FlatProfileDTO>로 확장 가능
            return content.size() > pageSize ? content.subList(0, pageSize) : content;

        } catch (Exception e) {
            log.error("Error in findFilteredFlatProfilesWithCursor: {}", e.getMessage(), e);
            return List.of();
        }
    }
}
