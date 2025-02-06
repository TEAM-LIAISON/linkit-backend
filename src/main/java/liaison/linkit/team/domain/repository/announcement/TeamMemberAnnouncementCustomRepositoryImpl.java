package liaison.linkit.team.domain.repository.announcement;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import liaison.linkit.common.domain.QPosition;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.global.util.QueryDslUtil;
import liaison.linkit.profile.domain.region.QRegion;
import liaison.linkit.profile.domain.skill.QSkill;
import liaison.linkit.team.domain.announcement.QAnnouncementPosition;
import liaison.linkit.team.domain.announcement.QAnnouncementSkill;
import liaison.linkit.team.domain.announcement.QTeamMemberAnnouncement;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.region.QTeamRegion;
import liaison.linkit.team.domain.scale.QScale;
import liaison.linkit.team.domain.scale.QTeamScale;
import liaison.linkit.team.domain.team.QTeam;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementRequestDTO.UpdateTeamMemberAnnouncementRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TeamMemberAnnouncementCustomRepositoryImpl implements TeamMemberAnnouncementCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext
    private EntityManager entityManager; // EntityManager 주입

    @Override
    public List<TeamMemberAnnouncement> getTeamMemberAnnouncements(final Long teamId) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement = QTeamMemberAnnouncement.teamMemberAnnouncement;

        return jpaQueryFactory
                .selectFrom(qTeamMemberAnnouncement)
                .where(qTeamMemberAnnouncement.team.id.eq(teamId))
                .fetch();
    }

    @Override
    public List<TeamMemberAnnouncement> getAllByTeamIds(final List<Long> teamIds) {
        // 팀 ID 리스트가 비어있으면 바로 빈 리스트 반환
        if (teamIds == null || teamIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Q 클래스 준비
        QTeamMemberAnnouncement qAnnouncement = QTeamMemberAnnouncement.teamMemberAnnouncement;

        // 쿼리 실행
        return jpaQueryFactory
                .selectFrom(qAnnouncement)
                .where(qAnnouncement.team.id.in(teamIds))
                .fetch();
    }

    @Override
    public TeamMemberAnnouncement getTeamMemberAnnouncement(final Long teamMemberAnnouncementId) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement = QTeamMemberAnnouncement.teamMemberAnnouncement;

        return jpaQueryFactory
                .selectFrom(qTeamMemberAnnouncement)
                .where(qTeamMemberAnnouncement.id.eq(teamMemberAnnouncementId))
                .fetchOne();
    }

    @Override
    public TeamMemberAnnouncement updateTeamMemberAnnouncement(
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final UpdateTeamMemberAnnouncementRequest request
    ) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement = QTeamMemberAnnouncement.teamMemberAnnouncement;

        // 1) QueryDSL 부분 업데이트
        long updatedCount = jpaQueryFactory
                .update(qTeamMemberAnnouncement)
                .set(qTeamMemberAnnouncement.announcementTitle, request.getAnnouncementTitle())
                .set(qTeamMemberAnnouncement.announcementEndDate, request.getAnnouncementEndDate())
                .set(qTeamMemberAnnouncement.isPermanentRecruitment, request.getIsPermanentRecruitment())
                .set(qTeamMemberAnnouncement.isRegionFlexible, request.getIsRegionFlexible())
                .set(qTeamMemberAnnouncement.mainTasks, request.getMainTasks())
                .set(qTeamMemberAnnouncement.workMethod, request.getWorkMethod())
                .set(qTeamMemberAnnouncement.idealCandidate, request.getIdealCandidate())
                .set(qTeamMemberAnnouncement.preferredQualifications, request.getPreferredQualifications())
                .set(qTeamMemberAnnouncement.joiningProcess, request.getJoiningProcess())
                .set(qTeamMemberAnnouncement.benefits, request.getBenefits())
                .where(qTeamMemberAnnouncement.id.eq(teamMemberAnnouncement.getId()))
                .execute();

        // 2) flush & clear
        entityManager.flush();
        entityManager.clear();

        // 3) 변경된 행이 있으면, 다시 select 해서 엔티티 리턴
        if (updatedCount > 0) {
            return jpaQueryFactory
                    .selectFrom(qTeamMemberAnnouncement)
                    .where(qTeamMemberAnnouncement.id.eq(teamMemberAnnouncement.getId()))
                    .fetchOne(); // 새로 로드된 엔티티(영속 상태)
        } else {
            return null; // 또는 Optional.empty() 처리 등
        }
    }


    @Override
    public TeamMemberAnnouncement updateTeamMemberAnnouncementPublicState(final TeamMemberAnnouncement teamMemberAnnouncement, final boolean isTeamMemberAnnouncementCurrentPublicState) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement = QTeamMemberAnnouncement.teamMemberAnnouncement;

        // QueryDSL을 사용하여 데이터베이스에서 ProfileLog 엔티티를 업데이트
        long updatedCount = jpaQueryFactory
                .update(qTeamMemberAnnouncement)
                .set(qTeamMemberAnnouncement.isAnnouncementPublic, !isTeamMemberAnnouncementCurrentPublicState)
                .where(qTeamMemberAnnouncement.id.eq(teamMemberAnnouncement.getId()))
                .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) { // 업데이트 성공 확인
            teamMemberAnnouncement.setIsAnnouncementPublic(!isTeamMemberAnnouncementCurrentPublicState); // 메모리 내 객체 업데이트
            return teamMemberAnnouncement;
        } else {
            throw new IllegalStateException("팀원 공고 공개/비공개 업데이트 실패");
        }
    }

    @Override
    public Page<TeamMemberAnnouncement> findAll(
            final List<String> majorPosition,
            final List<String> skillName,
            final List<String> cityName,
            final List<String> scaleName,
            final Pageable pageable
    ) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement = QTeamMemberAnnouncement.teamMemberAnnouncement;

        QTeam qTeam = QTeam.team;

        QAnnouncementPosition qAnnouncementPosition = QAnnouncementPosition.announcementPosition;
        QPosition qPosition = QPosition.position;

        QAnnouncementSkill qAnnouncementSkill = QAnnouncementSkill.announcementSkill;
        QSkill qSkill = QSkill.skill;

        QTeamRegion qTeamRegion = QTeamRegion.teamRegion;
        QRegion qRegion = QRegion.region;

        QTeamScale qTeamScale = QTeamScale.teamScale;
        QScale qScale = QScale.scale;

        log.info("Starting query to fetch Announcements");

        List<TeamMemberAnnouncement> content = jpaQueryFactory
                .selectDistinct(qTeamMemberAnnouncement)
                .from(qTeamMemberAnnouncement)

                .leftJoin(qTeamMemberAnnouncement.team, qTeam)

                .leftJoin(qTeamScale).on(qTeamScale.team.eq(qTeam))
                .leftJoin(qTeamScale.scale, qScale)

                .leftJoin(qTeamRegion).on(qTeamRegion.team.eq(qTeam))
                .leftJoin(qTeamRegion.region, qRegion)

                .leftJoin(qAnnouncementPosition).on(qAnnouncementPosition.teamMemberAnnouncement.eq(qTeamMemberAnnouncement))
                .leftJoin(qAnnouncementPosition.position, qPosition)

                .leftJoin(qAnnouncementSkill).on(qAnnouncementSkill.teamMemberAnnouncement.eq(qTeamMemberAnnouncement))
                .leftJoin(qAnnouncementSkill.skill, qSkill)

                .where(
                        qTeamMemberAnnouncement.status.eq(StatusType.USABLE),
                        qTeamMemberAnnouncement.isAnnouncementPublic.eq(true),
                        hasMajorPositions(majorPosition),
                        hasSkillNames(skillName),
                        hasCityName(cityName),
                        hasScaleNames(scaleName)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(
                        QueryDslUtil.getOrderAnnouncementSpecifier(
                                pageable.getSort(),
                                qTeamMemberAnnouncement,
                                qAnnouncementPosition,
                                qAnnouncementSkill,
                                qTeamRegion,
                                qTeamScale
                        )
                )
                .fetch();

        log.info("Fetched {} announcements from database", content.size());

        // 카운트 쿼리
        Long totalLong = jpaQueryFactory
                .selectDistinct(qTeamMemberAnnouncement.count())
                .from(qTeamMemberAnnouncement)

                .leftJoin(qTeamMemberAnnouncement.team, qTeam)

                .leftJoin(qTeamScale).on(qTeamScale.team.eq(qTeam))
                .leftJoin(qTeamScale.scale, qScale)

                .leftJoin(qTeamRegion).on(qTeamRegion.team.eq(qTeam))
                .leftJoin(qTeamRegion.region, qRegion)

                .leftJoin(qAnnouncementPosition).on(qAnnouncementPosition.teamMemberAnnouncement.eq(qTeamMemberAnnouncement))
                .leftJoin(qAnnouncementPosition.position, qPosition)

                .leftJoin(qAnnouncementSkill).on(qAnnouncementSkill.teamMemberAnnouncement.eq(qTeamMemberAnnouncement))
                .leftJoin(qAnnouncementSkill.skill, qSkill)

                .where(
                        qTeamMemberAnnouncement.status.eq(StatusType.USABLE),
                        qTeamMemberAnnouncement.isAnnouncementPublic.eq(true),
                        hasMajorPositions(majorPosition),
                        hasSkillNames(skillName),
                        hasCityName(cityName),
                        hasScaleNames(scaleName)
                )
                .fetchOne();

        long total = (totalLong == null) ? 0L : totalLong;

        log.info("Fetched {} announcements from database", total);

        return PageableExecutionUtils.getPage(content, pageable, () -> total);
    }

    private BooleanExpression hasMajorPositions(final List<String> majorPosition) {
        if (majorPosition == null || majorPosition.isEmpty()) {
            return null;
        }
        QPosition qPosition = QPosition.position;

        return qPosition.majorPosition.in(majorPosition);
    }

    private BooleanExpression hasSkillNames(List<String> skillName) {
        if (skillName == null || skillName.isEmpty()) {
            return null;
        }
        QSkill qSkill = QSkill.skill;

        return qSkill.skillName.in(skillName);
    }

    private BooleanExpression hasCityName(List<String> cityName) {
        if (cityName == null || cityName.isEmpty()) {
            return null;
        }
        QRegion qRegion = QRegion.region;

        return qRegion.cityName.in(cityName);
    }

    private BooleanExpression hasScaleNames(List<String> scaleName) {
        if (scaleName == null || scaleName.isEmpty()) {
            return null;
        }
        QScale qScale = QScale.scale;

        return qScale.scaleName.in(scaleName);
    }

//    @Override
//    public List<TeamMemberAnnouncement> findTopTeamMemberAnnouncements(final int limit) {
//        QTeamMemberAnnouncement qTeamMemberAnnouncement = QTeamMemberAnnouncement.teamMemberAnnouncement;
//
//        return jpaQueryFactory
//                .selectFrom(qTeamMemberAnnouncement)
//                .where(
//                        qTeamMemberAnnouncement.isAnnouncementPublic.eq(true),
//                        qTeamMemberAnnouncement.status.eq(StatusType.USABLE)
//                )
//                .orderBy(qTeamMemberAnnouncement.createdAt.desc()) // 최신순으로 정렬
//                .limit(limit)
//                .fetch();
//    }

    @Override
    public List<TeamMemberAnnouncement> findTopTeamMemberAnnouncements(final int limit) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement = QTeamMemberAnnouncement.teamMemberAnnouncement;

        return jpaQueryFactory
                .selectFrom(qTeamMemberAnnouncement)
                .where(
                        qTeamMemberAnnouncement.isAnnouncementPublic.eq(true),
                        qTeamMemberAnnouncement.status.eq(StatusType.USABLE)
                )
                .orderBy(
                        // CASE WHEN 구문으로 지정한 순서대로 정렬
                        new CaseBuilder()
                                .when(qTeamMemberAnnouncement.id.eq(6L)).then(0)
                                .when(qTeamMemberAnnouncement.id.eq(4L)).then(1)
                                .when(qTeamMemberAnnouncement.id.eq(3L)).then(2)
                                .when(qTeamMemberAnnouncement.id.eq(10L)).then(3)
                                .when(qTeamMemberAnnouncement.id.eq(16L)).then(4)
                                .when(qTeamMemberAnnouncement.id.eq(8L)).then(5)
                                .when(qTeamMemberAnnouncement.id.eq(9L)).then(6)
                                .when(qTeamMemberAnnouncement.id.eq(22L)).then(7)
                                .when(qTeamMemberAnnouncement.id.eq(21L)).then(8)
                                .otherwise(9)
                                .asc()
                )
                .limit(limit)
                .fetch();
    }


    @Override
    public Set<TeamMemberAnnouncement> getAllDeletableTeamMemberAnnouncementsByTeamIds(final List<Long> teamIds) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement = QTeamMemberAnnouncement.teamMemberAnnouncement;

        if (teamIds == null || teamIds.isEmpty()) {
            log.info("No team IDs provided for fetching deletable announcements.");
            return Collections.emptySet();
        }

        return new HashSet<>(
                jpaQueryFactory
                        .selectFrom(qTeamMemberAnnouncement)
                        .where(qTeamMemberAnnouncement.team.id.in(teamIds))
                        .fetch()
        );
    }

    @Override
    public Set<TeamMemberAnnouncement> getAllDeletableTeamMemberAnnouncementsByTeamId(final Long teamId) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement = QTeamMemberAnnouncement.teamMemberAnnouncement;

        if (teamId == null) {
            log.info("No team ID provided for fetching deletable announcements.");
            return Collections.emptySet();
        }

        return new HashSet<>(
                jpaQueryFactory
                        .selectFrom(qTeamMemberAnnouncement)
                        .where(
                                qTeamMemberAnnouncement.team.id.eq(teamId) // 특정 팀 ID와 일치
                        )
                        .fetch()
        );
    }

    @Override
    public void deleteAllByIds(final List<Long> announcementIds) {
        QTeamMemberAnnouncement qAnnouncement = QTeamMemberAnnouncement.teamMemberAnnouncement;

        try {
            long updatedCount = jpaQueryFactory
                    .update(qAnnouncement)
                    .set(qAnnouncement.status, StatusType.DELETED)
                    .where(qAnnouncement.id.in(announcementIds))
                    .execute();

            log.info("Deleted {} announcements with IDs: {}", updatedCount, announcementIds);

            entityManager.flush();
            entityManager.clear();

        } catch (Exception e) {
            log.error("Error occurred while deleting announcements with IDs: {}", announcementIds, e);
        }
    }
}
