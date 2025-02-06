package liaison.linkit.profile.domain.repository.profile;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import liaison.linkit.common.domain.QPosition;
import liaison.linkit.common.domain.QProfileState;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.global.util.QueryDslUtil;
import liaison.linkit.member.domain.QMemberBasicInform;
import liaison.linkit.profile.domain.position.QProfilePosition;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.profile.QProfile;
import liaison.linkit.profile.domain.region.QProfileRegion;
import liaison.linkit.profile.domain.region.QRegion;
import liaison.linkit.profile.domain.skill.QProfileSkill;
import liaison.linkit.profile.domain.skill.QSkill;
import liaison.linkit.profile.domain.state.QProfileCurrentState;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.MiniProfileDetailResponse;
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


    // 수정 및 보완 필요
    @Override
    public MiniProfileDetailResponse findMiniProfileDetail(final Long memberId) {
        QProfile qProfile = QProfile.profile;
        QProfilePosition qProfilePosition = QProfilePosition.profilePosition;
        QPosition qPosition = QPosition.position;
        QProfileCurrentState qProfileCurrentState = QProfileCurrentState.profileCurrentState;
        QMemberBasicInform qMemberBasicInform = QMemberBasicInform.memberBasicInform; // 멤버 기본 정보에 대한 Querydsl 메타 모델
        QRegion qRegion = QRegion.region;

        List<MiniProfileResponseDTO.ProfilePositionItem> profilePositionItems =
                jpaQueryFactory
                        .select(
                                Projections.constructor(
                                        MiniProfileResponseDTO.ProfilePositionItem.class,
                                        qProfilePosition.position.majorPosition,
                                        qProfilePosition.position.subPosition
                                ))
                        .from(qProfilePosition)
                        .leftJoin(qPosition)
                        .on(qProfilePosition.position.id.eq(qPosition.id))
                        .where(qProfilePosition.profile.member.id.eq(memberId))
                        .fetch();

        List<MiniProfileResponseDTO.ProfileCurrentStateItem> profileCurrentStateItems =
                jpaQueryFactory
                        .select(
                                Projections.constructor(
                                        MiniProfileResponseDTO.ProfileCurrentStateItem.class,
                                        qProfileCurrentState.profileState.profileStateName
                                ))
                        .from(qProfileCurrentState)
                        .leftJoin(qProfileCurrentState)
                        .on(qProfileCurrentState.profileState.id.eq(qProfileCurrentState.id))
                        .where(qProfileCurrentState.profile.member.id.eq(memberId))
                        .fetch();

        return null;
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
                                .when(qProfile.id.eq(14L)).then(0)
                                .when(qProfile.id.eq(6L)).then(1)
                                .when(qProfile.id.eq(9L)).then(2)
                                .when(qProfile.id.eq(29L)).then(3)
                                .when(qProfile.id.eq(26L)).then(4)
                                .when(qProfile.id.eq(27L)).then(5)
                                .otherwise(6)
                                .asc()
                )
                .fetch();
    }

    @Override
    public Page<Profile> findAll(
            final List<String> majorPosition,
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
            log.info("Major Positions: {}", majorPosition);
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
                            hasMajorPositions(majorPosition),
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
                            hasMajorPositions(majorPosition),
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

    private BooleanExpression hasMajorPositions(final List<String> majorPositions) {
        if (majorPositions == null || majorPositions.isEmpty()) {
            return null;
        }

        QPosition qPosition = QPosition.position;

        return qPosition.majorPosition.in(majorPositions);
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
