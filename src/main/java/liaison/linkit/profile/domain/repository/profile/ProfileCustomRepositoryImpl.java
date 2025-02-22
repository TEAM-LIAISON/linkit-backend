package liaison.linkit.profile.domain.repository.profile;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
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

        Profile result = jpaQueryFactory
            .selectFrom(profile)
            .where(profile.member.id.eq(memberId))
            .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Profile> findByEmailId(final String emailId) {
        QProfile profile = QProfile.profile;

        Profile result = jpaQueryFactory
            .selectFrom(profile)
            .where(profile.member.emailId.eq(emailId))
            .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public boolean existsByMemberId(Long memberId) {
        QProfile profile = QProfile.profile;

        Integer count = jpaQueryFactory
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
            .where(
                qProfile.status.eq(StatusType.USABLE)
                    .and(qProfile.isProfilePublic.eq(true))
            )
            .orderBy(
                new CaseBuilder()
                    .when(qProfile.id.eq(42L)).then(0)
                    .when(qProfile.id.eq(58L)).then(1)
                    .when(qProfile.id.eq(57L)).then(2)
                    .when(qProfile.id.eq(55L)).then(3)
                    .when(qProfile.id.eq(73L)).then(4)
                    .when(qProfile.id.eq(63L)).then(5)
                    .otherwise(6)
                    .asc()
            )
            .limit(limit)
            .fetch();
    }

    @Override
    public Page<Profile> findAll(
        final List<String> subPosition,
        final List<String> cityName,
        final List<String> profileStateName,
        final Pageable pageable
    ) {
        log.info("subPosition: {}", subPosition);
        log.info("cityName: {}", cityName);
        log.info("profileStateName: {}", profileStateName);

        QProfile qProfile = QProfile.profile;

        JPAQuery<Long> profileIdQuery = jpaQueryFactory
            .select(qProfile.id)
            .distinct()
            .from(qProfile)
            .where(qProfile.status.eq(StatusType.USABLE)
                .and(qProfile.isProfilePublic.eq(true)));

        // 2. 필터 조건에 따라 동적으로 조인 추가
        if (isNotEmpty(subPosition)) {
            QProfilePosition qProfilePosition = QProfilePosition.profilePosition;
            QPosition qPosition = QPosition.position;

            profileIdQuery
                .leftJoin(qProfilePosition).on(qProfilePosition.profile.eq(qProfile))
                .leftJoin(qPosition).on(qProfilePosition.position.eq(qPosition))
                .where(qPosition.subPosition.in(subPosition));
        }

        if (isNotEmpty(cityName)) {
            QProfileRegion qProfileRegion = QProfileRegion.profileRegion;
            QRegion qRegion = QRegion.region;

            profileIdQuery
                .leftJoin(qProfileRegion).on(qProfileRegion.profile.eq(qProfile))
                .leftJoin(qRegion).on(qProfileRegion.region.eq(qRegion))
                .where(qRegion.cityName.in(cityName));
        }

        if (isNotEmpty(profileStateName)) {
            QProfileCurrentState qProfileCurrentState = QProfileCurrentState.profileCurrentState;
            QProfileState qProfileState = QProfileState.profileState;

            profileIdQuery
                .leftJoin(qProfileCurrentState).on(qProfileCurrentState.profile.eq(qProfile))
                .leftJoin(qProfileState).on(qProfileCurrentState.profileState.eq(qProfileState))
                .where(qProfileState.profileStateName.in(profileStateName));
        }

        // 3. 페이징 처리된 ID 목록 조회
        List<Long> profileIds = profileIdQuery
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 2. 실제 데이터 조회 - OneToOne 관계는 fetch join 사용
        List<Profile> content = jpaQueryFactory
            .selectFrom(qProfile)
            .leftJoin(qProfile.member).fetchJoin()
            .leftJoin(qProfile.profileRegion).fetchJoin()  // OneToOne은 fetch join 안전
            .leftJoin(qProfile.profilePositions)  // OneToMany는 fetch join 제한적 사용
            .leftJoin(qProfile.profileCurrentStates)
            .where(qProfile.id.in(profileIds))
            .orderBy(QueryDslUtil.getOrderProfileSpecifier(
                pageable.getSort(),
                qProfile,
                QProfilePosition.profilePosition,
                QProfileRegion.profileRegion,
                QProfileCurrentState.profileCurrentState
            ))
            .distinct()
            .fetch();

        // 3. Count 쿼리
        JPAQuery<Long> countQuery = jpaQueryFactory
            .select(qProfile.countDistinct())
            .from(qProfile)
            .where(qProfile.status.eq(StatusType.USABLE)
                .and(qProfile.isProfilePublic.eq(true)));

        applyFiltersToCountQuery(countQuery, qProfile, subPosition, cityName, profileStateName);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private void applyFiltersToCountQuery(
        JPAQuery<Long> countQuery,
        QProfile qProfile,
        List<String> subPosition,
        List<String> cityName,
        List<String> profileStateName
    ) {
        // 2. 필터 조건에 따라 동적으로 조인 추가
        if (isNotEmpty(subPosition)) {
            QProfilePosition qProfilePosition = QProfilePosition.profilePosition;
            QPosition qPosition = QPosition.position;

            countQuery
                .leftJoin(qProfilePosition).on(qProfilePosition.profile.eq(qProfile))
                .leftJoin(qPosition).on(qProfilePosition.position.eq(qPosition))
                .where(qPosition.subPosition.in(subPosition));
        }

        if (isNotEmpty(cityName)) {
            QProfileRegion qProfileRegion = QProfileRegion.profileRegion;
            QRegion qRegion = QRegion.region;

            countQuery
                .leftJoin(qProfileRegion).on(qProfileRegion.profile.eq(qProfile))
                .leftJoin(qRegion).on(qProfileRegion.region.eq(qRegion))
                .where(qRegion.cityName.in(cityName));
        }

        if (isNotEmpty(profileStateName)) {
            QProfileCurrentState qProfileCurrentState = QProfileCurrentState.profileCurrentState;
            QProfileState qProfileState = QProfileState.profileState;

            countQuery
                .leftJoin(qProfileCurrentState).on(qProfileCurrentState.profile.eq(qProfile))
                .leftJoin(qProfileState).on(qProfileCurrentState.profileState.eq(qProfileState))
                .where(qProfileState.profileStateName.in(profileStateName));
        }
    }

    @Override
    public Page<Profile> findTopCompletionProfiles(final Pageable pageable) {
        QProfile qProfile = QProfile.profile;

        List<Profile> content = jpaQueryFactory
            .selectFrom(qProfile)
            .where(
                qProfile.status.eq(StatusType.USABLE)
                    .and(qProfile.isProfilePublic.eq(true))
            )
            .orderBy(
                new CaseBuilder()
                    .when(qProfile.id.eq(42L)).then(0)
                    .when(qProfile.id.eq(58L)).then(1)
                    .when(qProfile.id.eq(57L)).then(2)
                    .when(qProfile.id.eq(55L)).then(3)
                    .when(qProfile.id.eq(73L)).then(4)
                    .when(qProfile.id.eq(63L)).then(5)
                    .when(qProfile.id.eq(33L)).then(6)
                    .when(qProfile.id.eq(26L)).then(7)
                    .otherwise(8)
                    .asc()
            )
            .limit(6)
            .fetch();

        // Pageable 정보와 함께 Page 객체로 반환 (항상 최대 6개의 레코드)
        return PageableExecutionUtils.getPage(content, pageable, content::size);
    }

    @Override
    public Page<Profile> findAllExcludingIds(
        final List<Long> excludeIds,
        final Pageable pageable
    ) {
        QProfile qProfile = QProfile.profile;

        List<Profile> content = jpaQueryFactory
            .selectFrom(qProfile)
            .where(
                qProfile.status.eq(StatusType.USABLE)
                    .and(qProfile.isProfilePublic.eq(true))
                    .and(qProfile.id.notIn(excludeIds))
            )
            .orderBy(qProfile.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Long total = jpaQueryFactory
            .selectDistinct(qProfile.count())
            .from(qProfile)
            .where(
                qProfile.status.eq(StatusType.USABLE)
                    .and(qProfile.isProfilePublic.eq(true))
                    .and(qProfile.id.notIn(excludeIds))
            )
            .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> total);
    }

    private boolean isNotEmpty(List<?> list) {
        return list != null && !list.isEmpty();
    }
}
