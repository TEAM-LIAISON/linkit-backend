package liaison.linkit.profile.domain.repository.profile;

import static liaison.linkit.global.type.StatusType.USABLE;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.querydsl.core.Tuple;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

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

    @Override
    public Page<Profile> findTopCompletionProfiles(final Pageable pageable) {
        QProfile qProfile = QProfile.profile;

        List<Profile> content =
                jpaQueryFactory
                        .selectFrom(qProfile)
                        .where(
                                qProfile.status
                                        .eq(StatusType.USABLE)
                                        .and(qProfile.isProfilePublic.eq(true)))
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
                        .limit(6)
                        .fetch();

        // Pageable 정보와 함께 Page 객체로 반환 (항상 최대 6개의 레코드)
        return PageableExecutionUtils.getPage(content, pageable, content::size);
    }

    public CursorResponse<Profile> findAllExcludingIdsWithCursor(
            final List<Long> excludeProfileIds, final CursorRequest cursorRequest) {
        QProfile qProfile = QProfile.profile;

        try {
            // 기본 쿼리 조건
            BooleanExpression baseCondition =
                    qProfile.status.eq(USABLE).and(qProfile.isProfilePublic.eq(true));

            // ID 제외 조건
            if (excludeProfileIds != null && !excludeProfileIds.isEmpty()) {
                baseCondition = baseCondition.and(qProfile.id.notIn(excludeProfileIds));
            }

            // 커서 조건 - emailId로 profileId를 찾아서 적용
            if (cursorRequest != null
                    && cursorRequest.hasNext()
                    && cursorRequest.getCursor() != null) {
                // emailId로 해당 프로필의 ID를 조회
                String cursorEmailId = cursorRequest.getCursor();
                Long cursorProfileId =
                        jpaQueryFactory
                                .select(qProfile.id)
                                .from(qProfile)
                                .where(qProfile.member.emailId.eq(cursorEmailId))
                                .fetchOne();

                // 찾은 ID가 있으면 해당 ID보다 작은 ID를 가진 프로필만 조회
                if (cursorProfileId != null) {
                    baseCondition = baseCondition.and(qProfile.id.lt(cursorProfileId));
                }
            }

            // 페이지 크기 6의 배수로 설정
            int requestedSize = (cursorRequest != null) ? Math.max(1, cursorRequest.getSize()) : 10;
            int pageSize = (requestedSize % 6 == 0) ? requestedSize : (requestedSize / 6 + 1) * 6;

            // 1. ID와 emailId를 함께 조회
            List<Tuple> profileTuples =
                    jpaQueryFactory
                            .select(qProfile.id, qProfile.member.emailId)
                            .from(qProfile)
                            .where(baseCondition)
                            .orderBy(qProfile.id.desc()) // ID 기준으로 정렬
                            .limit(pageSize + 1)
                            .fetch();

            if (profileTuples.isEmpty()) {
                return CursorResponse.of(List.of(), null);
            }

            List<Long> profileIds = new ArrayList<>();

            // 다음 커서 계산
            String nextCursor = null;
            boolean hasNext = profileTuples.size() > pageSize;

            // profileIds 추출
            for (int i = 0; i < (hasNext ? pageSize : profileTuples.size()); i++) {
                profileIds.add(profileTuples.get(i).get(qProfile.id));
            }

            // 다음 커서 설정
            if (hasNext) {
                nextCursor = profileTuples.get(pageSize).get(qProfile.member.emailId);
            }

            // 2. ID로 프로필 엔터티만 조회하고 생성 시간 기준으로 정렬
            List<Profile> profiles =
                    jpaQueryFactory
                            .selectFrom(qProfile)
                            .where(qProfile.id.in(profileIds))
                            .orderBy(qProfile.createdAt.desc()) // 생성 시간 기준 내림차순 정렬
                            .fetch();

            return CursorResponse.of(profiles, nextCursor);
        } catch (Exception e) {
            log.error("Error in findAllExcludingIdsWithCursor: {}", e.getMessage(), e);
            return CursorResponse.of(List.of(), null);
        }
    }

    /** 필터링 조건으로 프로필을 커서 기반으로 조회합니다. */
    public CursorResponse<Profile> findAllByFilteringWithCursor(
            final List<String> subPosition,
            final List<String> cityName,
            final List<String> profileStateName,
            final CursorRequest cursorRequest) {
        QProfile qProfile = QProfile.profile;

        try {
            // emailId로 profileId를 찾는 로직
            Long cursorProfileId = null;
            if (cursorRequest != null
                    && cursorRequest.hasNext()
                    && cursorRequest.getCursor() != null) {
                String cursorEmailId = cursorRequest.getCursor();
                cursorProfileId =
                        jpaQueryFactory
                                .select(qProfile.id)
                                .from(qProfile)
                                .where(qProfile.member.emailId.eq(cursorEmailId))
                                .fetchOne();
            }

            // 1. 필터링된 프로필 ID와 emailId를 함께 조회
            JPAQuery<Tuple> profileQuery =
                    jpaQueryFactory
                            .select(qProfile.id, qProfile.member.emailId)
                            .distinct()
                            .from(qProfile)
                            .where(
                                    qProfile.status
                                            .eq(USABLE)
                                            .and(qProfile.isProfilePublic.eq(true)));

            // 커서 조건 추가 - 프로필 ID 기준
            if (cursorProfileId != null) {
                profileQuery = profileQuery.where(qProfile.id.lt(cursorProfileId));
            }

            // 포지션 필터링
            if (isNotEmpty(subPosition)) {
                QProfilePosition qProfilePosition = QProfilePosition.profilePosition;
                QPosition qPosition = QPosition.position;

                profileQuery
                        .leftJoin(qProfilePosition)
                        .on(qProfilePosition.profile.eq(qProfile))
                        .leftJoin(qPosition)
                        .on(qProfilePosition.position.eq(qPosition))
                        .where(qPosition.subPosition.in(subPosition));
            }

            // 지역 필터링
            if (isNotEmpty(cityName)) {
                QProfileRegion qProfileRegion = QProfileRegion.profileRegion;
                QRegion qRegion = QRegion.region;

                profileQuery
                        .leftJoin(qProfileRegion)
                        .on(qProfileRegion.profile.eq(qProfile))
                        .leftJoin(qRegion)
                        .on(qProfileRegion.region.eq(qRegion))
                        .where(qRegion.cityName.in(cityName));
            }

            // 상태 필터링
            if (isNotEmpty(profileStateName)) {
                QProfileCurrentState qProfileCurrentState =
                        QProfileCurrentState.profileCurrentState;
                QProfileState qProfileState = QProfileState.profileState;

                profileQuery
                        .leftJoin(qProfileCurrentState)
                        .on(qProfileCurrentState.profile.eq(qProfile))
                        .leftJoin(qProfileState)
                        .on(qProfileCurrentState.profileState.eq(qProfileState))
                        .where(qProfileState.profileStateName.in(profileStateName));
            }

            // ID 기준으로 정렬 및 제한
            int requestedSize = (cursorRequest != null) ? Math.max(1, cursorRequest.getSize()) : 10;
            int pageSize = (requestedSize % 6 == 0) ? requestedSize : (requestedSize / 6 + 1) * 6;

            List<Tuple> profileTuples =
                    profileQuery
                            .orderBy(qProfile.id.desc()) // ID 기준 정렬
                            .limit(pageSize + 1) // 다음 페이지 확인을 위해 +1
                            .fetch();

            // 조회할 프로필이 없는 경우 빈 응답 반환
            if (profileTuples.isEmpty()) {
                return CursorResponse.of(List.of(), null);
            }

            // profileIds 추출
            List<Long> profileIds = new ArrayList<>();
            for (int i = 0;
                    i < (profileTuples.size() > pageSize ? pageSize : profileTuples.size());
                    i++) {
                profileIds.add(profileTuples.get(i).get(qProfile.id));
            }

            // 다음 커서 계산
            boolean hasNext = profileTuples.size() > pageSize;
            String nextCursor = null;

            // 다음 페이지가 있는 경우
            if (hasNext) {
                // 마지막 엔티티의 emailId를 다음 커서로 사용
                nextCursor = profileTuples.get(pageSize).get(qProfile.member.emailId);
            }

            // 2. 실제 데이터 조회 - 생성 시간 기준으로 정렬
            List<Profile> content =
                    jpaQueryFactory
                            .selectFrom(qProfile)
                            .where(qProfile.id.in(profileIds))
                            .orderBy(qProfile.createdAt.desc()) // 생성 시간 기준 내림차순 정렬
                            .fetch();

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
}
