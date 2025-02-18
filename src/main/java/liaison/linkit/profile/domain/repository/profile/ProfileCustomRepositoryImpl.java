package liaison.linkit.profile.domain.repository.profile;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
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
        QProfilePosition qProfilePosition = QProfilePosition.profilePosition;
        QPosition qPosition = QPosition.position;
        QProfileRegion qProfileRegion = QProfileRegion.profileRegion;
        QRegion qRegion = QRegion.region;
        QProfileCurrentState qProfileCurrentState = QProfileCurrentState.profileCurrentState;
        QProfileState qProfileState = QProfileState.profileState;
        QProfileSkill qProfileSkill = QProfileSkill.profileSkill;
        QSkill qSkill = QSkill.skill;

        try {
            // 입력 파라미터 로그
            log.info("Executing findAll with parameters:");
            log.info("Sub Positions: {}", subPosition);
            log.info("Skill Names: {}", skillName);
            log.info("City Names: {}", cityName);
            log.info("Profile State Names: {}", profileStateName);
            log.info("Pageable: {}", pageable);

            // 데이터 조회 시작 로그
            log.info("Starting query to fetch Profiles");

            // 데이터 조회 쿼리
            List<Profile> content = jpaQueryFactory
                .selectDistinct(qProfile)
                .from(qProfile)

                // Profile과 ProfilePosition 조인
                .leftJoin(qProfilePosition).on(qProfilePosition.profile.eq(qProfile))
                .leftJoin(qProfilePosition.position, qPosition)
                // Profile과 ProfileRegion 조인
                .leftJoin(qProfileRegion).on(qProfileRegion.profile.eq(qProfile))
                .leftJoin(qProfileRegion.region, qRegion)
                // Profile과 ProfileCurrentState 조인
                .leftJoin(qProfileCurrentState).on(qProfileCurrentState.profile.eq(qProfile))
                .leftJoin(qProfileCurrentState.profileState, qProfileState)
                // Profile과 ProfileSkill 조인
                .leftJoin(qProfileSkill).on(qProfileSkill.profile.eq(qProfile))
                .leftJoin(qProfileSkill.skill, qSkill)

                // 조건
                .where(
                    qProfile.isProfilePublic.eq(true),
                    qProfile.status.eq(StatusType.USABLE),
                    hasSubPositions(subPosition),
                    hasSkillNames(skillName),
                    hasCityName(cityName),
                    hasProfileStateNames(profileStateName)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(QueryDslUtil.getOrderProfileSpecifier(
                    pageable.getSort(),
                    qProfile,
                    qProfilePosition,
                    qProfileRegion,
                    qProfileCurrentState,
                    qProfileSkill
                ))
                .fetch();

            // 조회된 데이터 수 로그
            log.info("Fetched {} profiles from database", content.size());

            // 카운트 쿼리 시작 로그
            log.info("Starting count query");

            // 카운트 쿼리
            Long totalLong = jpaQueryFactory
                .selectDistinct(qProfile.count())
                .from(qProfile)

                .leftJoin(qProfilePosition).on(qProfilePosition.profile.eq(qProfile))
                .leftJoin(qProfilePosition.position, qPosition)
                .leftJoin(qProfileRegion).on(qProfileRegion.profile.eq(qProfile))
                .leftJoin(qProfileRegion.region, qRegion)
                .leftJoin(qProfileCurrentState).on(qProfileCurrentState.profile.eq(qProfile))
                .leftJoin(qProfileCurrentState.profileState, qProfileState)
                .leftJoin(qProfileSkill).on(qProfileSkill.profile.eq(qProfile))
                .leftJoin(qProfileSkill.skill, qSkill)
                .where(
                    qProfile.isProfilePublic.eq(true),
                    qProfile.status.eq(StatusType.USABLE),
                    hasSubPositions(subPosition),
                    hasSkillNames(skillName),
                    hasCityName(cityName),
                    hasProfileStateNames(profileStateName)
                )
                .fetchOne();

            long total = (totalLong == null) ? 0L : totalLong;

            return PageableExecutionUtils.getPage(content, pageable, () -> total);
        } catch (Exception e) {
            log.error("Error executing findAll method", e);
            throw e;
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
}
