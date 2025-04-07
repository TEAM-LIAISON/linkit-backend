package liaison.linkit.profile.domain.repository.profile;

import static liaison.linkit.global.type.StatusType.USABLE;

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

    @Override
    public List<Profile> findHomeTopProfiles(final int limit) {
        QProfile qProfile = QProfile.profile;

        return jpaQueryFactory
                .selectFrom(qProfile)
                .leftJoin(qProfile.member, QMember.member)
                .fetchJoin()
                .where(qProfile.status.eq(StatusType.USABLE).and(qProfile.isProfilePublic.eq(true)))
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
                .limit(limit)
                .fetch();
    }

    public CursorResponse<Profile> findAllExcludingIdsWithCursor(
            final List<Long> excludeProfileIds, final CursorRequest cursorRequest) {

        QProfile qProfile = QProfile.profile;

        try {
            // Step 1. 커서 emailId → profileId 조회
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

            // Step 2. 본 쿼리
            BooleanExpression baseCondition =
                    qProfile.status.eq(USABLE).and(qProfile.isProfilePublic.eq(true));
            if (excludeProfileIds != null && !excludeProfileIds.isEmpty()) {
                baseCondition = baseCondition.and(qProfile.id.notIn(excludeProfileIds));
            }

            if (cursorProfileId != null) {
                baseCondition = baseCondition.and(qProfile.id.lt(cursorProfileId));
            }

            int requestedSize = Math.max(1, cursorRequest.size());
            int pageSize = (requestedSize % 6 == 0) ? requestedSize : (requestedSize / 6 + 1) * 6;

            List<Profile> content =
                    jpaQueryFactory
                            .selectFrom(qProfile)
                            .leftJoin(qProfile.member)
                            .fetchJoin()
                            .where(baseCondition)
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
            log.error("Error in findAllExcludingIdsWithCursor: {}", e.getMessage(), e);
            return CursorResponse.of(List.of(), null);
        }
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

    public List<FlatProfileDTO> findFlatProfilesWithoutCursor(int size) {
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
                .where(qProfile.status.eq(StatusType.USABLE).and(qProfile.isProfilePublic.eq(true)))
                .orderBy(qProfile.id.desc())
                .limit(size * 5)
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
                .where(qProfile.status.eq(StatusType.USABLE).and(qProfile.isProfilePublic.eq(true)))
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
                .limit(size)
                .fetch();
    }
}
