package liaison.linkit.profile.domain.repository.profile;

import static liaison.linkit.global.type.StatusType.USABLE;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.common.domain.QPosition;
import liaison.linkit.common.domain.QProfileState;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.global.util.QueryDslUtil;
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
import org.springframework.data.domain.PageImpl;
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
    public Page<Profile> findAll(
            final List<String> subPosition,
            final List<String> cityName,
            final List<String> profileStateName,
            final Pageable pageable) {
        QProfile qProfile = QProfile.profile;

        JPAQuery<Long> profileIdQuery =
                jpaQueryFactory
                        .select(qProfile.id)
                        .distinct()
                        .from(qProfile)
                        .where(
                                qProfile.status
                                        .eq(StatusType.USABLE)
                                        .and(qProfile.isProfilePublic.eq(true)));

        // 2. 필터 조건에 따라 동적으로 조인 추가
        if (isNotEmpty(subPosition)) {
            QProfilePosition qProfilePosition = QProfilePosition.profilePosition;
            QPosition qPosition = QPosition.position;

            profileIdQuery
                    .leftJoin(qProfilePosition)
                    .on(qProfilePosition.profile.eq(qProfile))
                    .leftJoin(qPosition)
                    .on(qProfilePosition.position.eq(qPosition))
                    .where(qPosition.subPosition.in(subPosition));
        }

        if (isNotEmpty(cityName)) {
            QProfileRegion qProfileRegion = QProfileRegion.profileRegion;
            QRegion qRegion = QRegion.region;

            profileIdQuery
                    .leftJoin(qProfileRegion)
                    .on(qProfileRegion.profile.eq(qProfile))
                    .leftJoin(qRegion)
                    .on(qProfileRegion.region.eq(qRegion))
                    .where(qRegion.cityName.in(cityName));
        }

        if (isNotEmpty(profileStateName)) {
            QProfileCurrentState qProfileCurrentState = QProfileCurrentState.profileCurrentState;
            QProfileState qProfileState = QProfileState.profileState;

            profileIdQuery
                    .leftJoin(qProfileCurrentState)
                    .on(qProfileCurrentState.profile.eq(qProfile))
                    .leftJoin(qProfileState)
                    .on(qProfileCurrentState.profileState.eq(qProfileState))
                    .where(qProfileState.profileStateName.in(profileStateName));
        }

        // 3. 페이징 처리된 ID 목록 조회
        List<Long> profileIds =
                profileIdQuery.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();

        // 2. 실제 데이터 조회 - OneToOne 관계는 fetch join 사용
        List<Profile> content =
                jpaQueryFactory
                        .selectFrom(qProfile)
                        .leftJoin(qProfile.profileRegion)
                        .fetchJoin() // OneToOne은 fetch join 안전
                        .leftJoin(qProfile.profilePositions) // OneToMany는 fetch join 제한적 사용
                        .leftJoin(qProfile.profileCurrentStates)
                        .where(qProfile.id.in(profileIds))
                        .orderBy(
                                QueryDslUtil.getOrderProfileSpecifier(
                                        pageable.getSort(),
                                        qProfile,
                                        QProfilePosition.profilePosition,
                                        QProfileRegion.profileRegion,
                                        QProfileCurrentState.profileCurrentState))
                        .distinct()
                        .fetch();

        // 3. Count 쿼리
        JPAQuery<Long> countQuery =
                jpaQueryFactory
                        .select(qProfile.countDistinct())
                        .from(qProfile)
                        .where(
                                qProfile.status
                                        .eq(StatusType.USABLE)
                                        .and(qProfile.isProfilePublic.eq(true)));

        applyFiltersToCountQuery(countQuery, qProfile, subPosition, cityName, profileStateName);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private void applyFiltersToCountQuery(
            JPAQuery<Long> countQuery,
            QProfile qProfile,
            List<String> subPosition,
            List<String> cityName,
            List<String> profileStateName) {
        // 2. 필터 조건에 따라 동적으로 조인 추가
        if (isNotEmpty(subPosition)) {
            QProfilePosition qProfilePosition = QProfilePosition.profilePosition;
            QPosition qPosition = QPosition.position;

            countQuery
                    .leftJoin(qProfilePosition)
                    .on(qProfilePosition.profile.eq(qProfile))
                    .leftJoin(qPosition)
                    .on(qProfilePosition.position.eq(qPosition))
                    .where(qPosition.subPosition.in(subPosition));
        }

        if (isNotEmpty(cityName)) {
            QProfileRegion qProfileRegion = QProfileRegion.profileRegion;
            QRegion qRegion = QRegion.region;

            countQuery
                    .leftJoin(qProfileRegion)
                    .on(qProfileRegion.profile.eq(qProfile))
                    .leftJoin(qRegion)
                    .on(qProfileRegion.region.eq(qRegion))
                    .where(qRegion.cityName.in(cityName));
        }

        if (isNotEmpty(profileStateName)) {
            QProfileCurrentState qProfileCurrentState = QProfileCurrentState.profileCurrentState;
            QProfileState qProfileState = QProfileState.profileState;

            countQuery
                    .leftJoin(qProfileCurrentState)
                    .on(qProfileCurrentState.profile.eq(qProfile))
                    .leftJoin(qProfileState)
                    .on(qProfileCurrentState.profileState.eq(qProfileState))
                    .where(qProfileState.profileStateName.in(profileStateName));
        }
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

    @Override
    public Page<Profile> findAllExcludingIds(final List<Long> excludeIds, final Pageable pageable) {
        QProfile qProfile = QProfile.profile;

        // 1. ID만 먼저 조회하는 서브쿼리
        List<Long> profileIds =
                jpaQueryFactory
                        .select(qProfile.id)
                        .from(qProfile)
                        .where(
                                qProfile.status
                                        .eq(StatusType.USABLE)
                                        .and(qProfile.isProfilePublic.eq(true))
                                        .and(
                                                excludeIds.isEmpty()
                                                        ? null
                                                        : qProfile.id.notIn(excludeIds)))
                        .orderBy(qProfile.createdAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        if (profileIds.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0L);
        }

        // 2. 실제 데이터 조회 - 필요한 연관관계만 fetch join
        List<Profile> content =
                jpaQueryFactory
                        .selectFrom(qProfile)
                        .where(qProfile.id.in(profileIds))
                        .orderBy(qProfile.createdAt.desc())
                        .distinct()
                        .fetch();

        // 3. Count 쿼리 최적화
        return PageableExecutionUtils.getPage(
                content,
                pageable,
                () -> {
                    // 마지막 페이지이거나 한 페이지 이하의 데이터만 있는 경우 카운트 쿼리 생략
                    if (content.size() < pageable.getPageSize()) {
                        return pageable.getOffset() + content.size();
                    }

                    Long count =
                            jpaQueryFactory
                                    .select(qProfile.count())
                                    .from(qProfile)
                                    .where(
                                            qProfile.status
                                                    .eq(StatusType.USABLE)
                                                    .and(qProfile.isProfilePublic.eq(true))
                                                    .and(
                                                            excludeIds.isEmpty()
                                                                    ? null
                                                                    : qProfile.id.notIn(
                                                                            excludeIds)))
                                    .fetchOne();

                    return count != null ? count : 0L;
                });
    }

    // 방법 2: 개별 컬렉션 별도 로딩을 위한 추가 메서드
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

            // 커서 조건
            if (cursorRequest != null
                    && cursorRequest.hasNext()
                    && cursorRequest.getCursor() != null) {
                baseCondition =
                        baseCondition.and(qProfile.member.emailId.lt(cursorRequest.getCursor()));
            }

            // 페이지 크기 안전하게 설정
            int pageSize = (cursorRequest != null) ? Math.max(1, cursorRequest.getSize()) : 10;

            // 1. ID만 먼저 조회
            List<Long> profileIds =
                    jpaQueryFactory
                            .select(qProfile.id)
                            .from(qProfile)
                            .where(baseCondition)
                            .orderBy(qProfile.id.desc())
                            .limit(pageSize + 1)
                            .fetch();

            if (profileIds.isEmpty()) {
                return CursorResponse.of(List.of(), null);
            }

            // 다음 커서 계산
            String nextCursor = null;
            boolean hasNext = profileIds.size() > pageSize;

            if (hasNext) {
                nextCursor = String.valueOf(profileIds.get(profileIds.size() - 1));
                profileIds.remove(profileIds.size() - 1);
            }

            // 2. ID로 팀 엔터티만 조회 (컬렉션 없이)
            List<Profile> profiles =
                    jpaQueryFactory
                            .selectFrom(qProfile)
                            .where(qProfile.id.in(profileIds))
                            .orderBy(qProfile.id.desc())
                            .fetch();

            return CursorResponse.of(profiles, nextCursor);
        } catch (Exception e) {
            log.error("Error in findAllExcludingIdsWithCursor: {}", e.getMessage(), e);
            return CursorResponse.of(List.of(), null);
        }
    }

    /** 필터링 조건으로 팀을 커서 기반으로 조회합니다. */
    public CursorResponse<Profile> findAllByFilteringWithCursor(
            final List<String> subPosition,
            final List<String> cityName,
            final List<String> profileStateName,
            final CursorRequest cursorRequest) {
        QProfile qProfile = QProfile.profile;

        try {
            // 1. 필터링된 팀 ID를 조회
            JPAQuery<Long> profileIdQuery =
                    jpaQueryFactory
                            .select(qProfile.id)
                            .distinct()
                            .from(qProfile)
                            .where(
                                    qProfile.status
                                            .eq(USABLE)
                                            .and(qProfile.isProfilePublic.eq(true)));

            // 커서 조건 추가
            if (cursorRequest != null
                    && cursorRequest.hasNext()
                    && cursorRequest.getCursor() != null) {
                profileIdQuery =
                        profileIdQuery.where(qProfile.member.emailId.lt(cursorRequest.getCursor()));
            }

            // 규모 필터링
            if (isNotEmpty(subPosition)) {
                QProfilePosition qProfilePosition = QProfilePosition.profilePosition;
                QPosition qPosition = QPosition.position;

                profileIdQuery
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

                profileIdQuery
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

                profileIdQuery
                        .leftJoin(qProfileCurrentState)
                        .on(qProfileCurrentState.profile.eq(qProfile))
                        .leftJoin(qProfileState)
                        .on(qProfileCurrentState.profileState.eq(qProfileState))
                        .where(qProfileState.profileStateName.in(profileStateName));
            }

            // ID 내림차순 정렬 및 제한
            int pageSize = (cursorRequest != null) ? Math.max(1, cursorRequest.getSize()) : 10;
            List<Long> profileIds =
                    profileIdQuery
                            .orderBy(qProfile.id.desc())
                            .limit(pageSize + 1) // 다음 페이지 확인을 위해 +1
                            .fetch();

            // 조회할 팀이 없는 경우 빈 응답 반환
            if (profileIds.isEmpty()) {
                return CursorResponse.of(List.of(), null);
            }

            // 다음 커서 계산
            boolean hasNext = profileIds.size() > pageSize;
            String nextCursor = null;

            // 다음 페이지가 있는 경우
            if (hasNext) {
                nextCursor = String.valueOf(profileIds.get(profileIds.size() - 1));
                profileIds.remove(profileIds.size() - 1); // 마지막 요소 제거
            }

            // 2. 실제 데이터 조회 - fetch join을 하나만 적용하거나 제거
            // 방법 1: fetch join 없이 조회 (가장 안전한 방법)
            List<Profile> content =
                    jpaQueryFactory
                            .selectFrom(qProfile)
                            .where(qProfile.id.in(profileIds))
                            .orderBy(qProfile.id.desc())
                            .fetch();

            // 또는 방법 2: EntityGraph를 사용 (TeamRepository에 추가된 메서드 활용)
            // List<Team> content = teamRepository.findAllByIdIn(teamIds);
            // 필요한 경우 ID 정렬
            // content.sort(Comparator.comparing(Team::getId, Comparator.reverseOrder()));

            return CursorResponse.of(content, nextCursor);
        } catch (Exception e) {
            log.error("Error in findAllByFilteringWithCursor: {}", e.getMessage(), e);
            return CursorResponse.of(List.of(), null);
        }
    }

    private boolean isNotEmpty(List<?> list) {
        return list != null && !list.isEmpty();
    }
}
