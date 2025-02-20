package liaison.linkit.profile.domain.repository.profile;

import com.querydsl.core.types.dsl.BooleanExpression;
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
import liaison.linkit.profile.domain.skill.QProfileSkill;
import liaison.linkit.profile.domain.skill.QSkill;
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

//    @Override
//    public List<Profile> findTopProfiles(final int limit) {
//        QProfile qProfile = QProfile.profile;
//
//        return jpaQueryFactory
//                .selectFrom(qProfile)
//                .where(
//                        qProfile.status.eq(StatusType.USABLE)
//                                .and(qProfile.isProfilePublic.eq(true))
//                )
//                .orderBy(qProfile.createdAt.desc()) // 최신순으로 정렬
//                .limit(limit)
//                .fetch();
//    }

    @Override
    public List<Profile> findTopProfiles(final int limit) {
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
        final List<String> skillName,
        final List<String> cityName,
        final List<String> profileStateName,
        final Pageable pageable
    ) {
        QProfile qProfile = QProfile.profile;

        JPAQuery<Profile> query = buildBaseQuery(qProfile);
        applyFilters(query, qProfile, subPosition, skillName, cityName, profileStateName);
        applyDefaultConditions(query, qProfile);
        applySort(query, qProfile, pageable);

        // Execute main query with pagination
        List<Profile> content = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // Build and execute count query
        JPAQuery<Long> countQuery = buildCountQuery(qProfile);
        applyFilters(countQuery, qProfile, subPosition, skillName, cityName, profileStateName);
        applyDefaultConditions(countQuery, qProfile);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
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


    private BooleanExpression hasSubPositions(final List<String> subPositions) {
        if (subPositions == null || subPositions.isEmpty()) {
            return null;
        }

        QPosition qPosition = QPosition.position;

        return qPosition.subPosition.in(subPositions);
    }

    private BooleanExpression hasSkillNames(final List<String> skillName) {
        if (skillName == null || skillName.isEmpty()) {
            return null;
        }
        QSkill qSkill = QSkill.skill;

        return qSkill.skillName.in(skillName);
    }

    private BooleanExpression hasCityName(final List<String> cityName) {
        if (cityName == null || cityName.isEmpty()) {
            return null;
        }
        QRegion qRegion = QRegion.region;

        return qRegion.cityName.in(cityName);
    }

    private BooleanExpression hasProfileStateNames(final List<String> profileStateName) {
        if (profileStateName == null || profileStateName.isEmpty()) {
            return null;
        }
        QProfileState qProfileState = QProfileState.profileState;

        return qProfileState.profileStateName.in(profileStateName);
    }

    private JPAQuery<Profile> buildBaseQuery(QProfile qProfile) {
        return jpaQueryFactory
            .selectFrom(qProfile)
            .from(qProfile);
    }

    private JPAQuery<Long> buildCountQuery(QProfile qProfile) {
        return jpaQueryFactory
            .selectDistinct(qProfile.count())
            .from(qProfile);
    }

    private void applyFilters(
        JPAQuery<?> query,
        QProfile qProfile,
        List<String> subPosition,
        List<String> skillName,
        List<String> cityName,
        List<String> profileStateName
    ) {
        applyPositionFilter(query, qProfile, subPosition);
        applySkillFilter(query, qProfile, skillName);
        applyRegionFilter(query, qProfile, cityName);
        applyProfileStateFilter(query, qProfile, profileStateName);
    }

    private void applyPositionFilter(
        JPAQuery<?> query,
        QProfile qProfile,
        List<String> subPosition
    ) {
        if (isNotEmpty(subPosition)) {
            QProfilePosition qProfilePosition = QProfilePosition.profilePosition;
            QPosition qPosition = QPosition.position;

            query.innerJoin(qProfilePosition)
                .on(qProfilePosition.profile.eq(qProfile))
                .innerJoin(qProfilePosition.position, qPosition)
                .where(qPosition.subPosition.in(subPosition));
        }
    }

    private void applySkillFilter(
        JPAQuery<?> query,
        QProfile qProfile,
        List<String> skillName
    ) {
        if (isNotEmpty(skillName)) {
            QProfileSkill qProfileSkill = QProfileSkill.profileSkill;
            QSkill qSkill = QSkill.skill;

            query.innerJoin(qProfileSkill)
                .on(qProfileSkill.profile.eq(qProfile))
                .innerJoin(qProfileSkill.skill, qSkill)
                .where(qSkill.skillName.in(skillName));
        }
    }

    private void applyRegionFilter(
        JPAQuery<?> query,
        QProfile qProfile,
        List<String> cityName
    ) {
        if (isNotEmpty(cityName)) {
            QProfileRegion qProfileRegion = QProfileRegion.profileRegion;
            QRegion qRegion = QRegion.region;

            query.innerJoin(qProfileRegion)
                .on(qProfileRegion.profile.eq(qProfile))
                .innerJoin(qProfileRegion.region, qRegion)
                .where(qRegion.cityName.in(cityName));
        }
    }

    private void applyProfileStateFilter(
        JPAQuery<?> query,
        QProfile qProfile,
        List<String> profileStateName
    ) {
        if (isNotEmpty(profileStateName)) {
            QProfileCurrentState qProfileCurrentState = QProfileCurrentState.profileCurrentState;
            QProfileState qProfileState = QProfileState.profileState;

            query.innerJoin(qProfileCurrentState)
                .on(qProfileCurrentState.profile.eq(qProfile))
                .innerJoin(qProfileCurrentState.profileState, qProfileState)
                .where(qProfileState.profileStateName.in(profileStateName));
        }
    }

    private void applyDefaultConditions(JPAQuery<?> query, QProfile qProfile) {
        query.where(
            qProfile.status.eq(StatusType.USABLE)
                .and(qProfile.isProfilePublic.eq(true))
        );
    }

    private void applySort(
        JPAQuery<?> query,
        QProfile qProfile,
        Pageable pageable
    ) {
        query.orderBy(QueryDslUtil.getOrderProfileSpecifier(
            pageable.getSort(),
            qProfile,
            QProfilePosition.profilePosition,
            QProfileRegion.profileRegion,
            QProfileCurrentState.profileCurrentState,
            QProfileSkill.profileSkill
        ));
    }

    private boolean isNotEmpty(List<?> list) {
        return list != null && !list.isEmpty();
    }
}
