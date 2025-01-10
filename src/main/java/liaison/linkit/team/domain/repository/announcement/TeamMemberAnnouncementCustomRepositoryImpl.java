package liaison.linkit.team.domain.repository.announcement;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
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
    public TeamMemberAnnouncement updateTeamMemberAnnouncement(final TeamMemberAnnouncement teamMemberAnnouncement, final UpdateTeamMemberAnnouncementRequest request) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement = QTeamMemberAnnouncement.teamMemberAnnouncement;

        // 팀원 공고 업데이트
        long updatedCount = jpaQueryFactory
                .update(qTeamMemberAnnouncement)
                .set(qTeamMemberAnnouncement.announcementTitle, request.getAnnouncementTitle())
                .set(qTeamMemberAnnouncement.announcementStartDate, request.getAnnouncementStartDate())
                .set(qTeamMemberAnnouncement.announcementEndDate, request.getAnnouncementEndDate())
                .set(qTeamMemberAnnouncement.isRegionFlexible, request.getIsRegionFlexible())
                .set(qTeamMemberAnnouncement.mainTasks, request.getMainTasks())
                .set(qTeamMemberAnnouncement.workMethod, request.getMainTasks())
                .set(qTeamMemberAnnouncement.idealCandidate, request.getPreferredQualifications())
                .set(qTeamMemberAnnouncement.preferredQualifications, request.getPreferredQualifications())
                .set(qTeamMemberAnnouncement.joiningProcess, request.getJoiningProcess())
                .set(qTeamMemberAnnouncement.benefits, request.getBenefits())
                .where(qTeamMemberAnnouncement.id.eq(teamMemberAnnouncement.getId()))
                .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) {
            return jpaQueryFactory
                    .selectFrom(qTeamMemberAnnouncement)
                    .where(qTeamMemberAnnouncement.id.eq(teamMemberAnnouncement.getId()))
                    .fetchOne();
        } else {
            return null;
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
}
