package liaison.linkit.team.domain.repository.announcement;

import static liaison.linkit.global.type.StatusType.USABLE;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.common.domain.QPosition;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.global.util.QueryDslUtil;
import liaison.linkit.profile.domain.region.QRegion;
import liaison.linkit.profile.domain.skill.QSkill;
import liaison.linkit.search.presentation.dto.announcement.FlatAnnouncementDTO;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.search.sortType.AnnouncementSortType;
import liaison.linkit.team.domain.announcement.QAnnouncementPosition;
import liaison.linkit.team.domain.announcement.QAnnouncementProjectType;
import liaison.linkit.team.domain.announcement.QAnnouncementSkill;
import liaison.linkit.team.domain.announcement.QAnnouncementWorkType;
import liaison.linkit.team.domain.announcement.QTeamMemberAnnouncement;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.projectType.QProjectType;
import liaison.linkit.team.domain.region.QTeamRegion;
import liaison.linkit.team.domain.scale.QScale;
import liaison.linkit.team.domain.scale.QTeamScale;
import liaison.linkit.team.domain.team.QTeam;
import liaison.linkit.team.domain.workType.QWorkType;
import liaison.linkit.team.presentation.announcement.dto.AnnouncementDynamicResponse;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementRequestDTO.UpdateTeamMemberAnnouncementRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TeamMemberAnnouncementCustomRepositoryImpl
        implements TeamMemberAnnouncementCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext private EntityManager entityManager; // EntityManager 주입

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
        QTeamMemberAnnouncement qTeamMemberAnnouncement =
                QTeamMemberAnnouncement.teamMemberAnnouncement;

        return jpaQueryFactory
                .selectFrom(qTeamMemberAnnouncement)
                .where(qTeamMemberAnnouncement.id.eq(teamMemberAnnouncementId))
                .fetchOne();
    }

    @Override
    public TeamMemberAnnouncement updateTeamMemberAnnouncement(
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final UpdateTeamMemberAnnouncementRequest request) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement =
                QTeamMemberAnnouncement.teamMemberAnnouncement;

        // isPermanentRecruitment가 true이면 announcementEndDate를 null로, 아니면 요청 값 사용
        String announcementEndDateValue =
                request.getIsPermanentRecruitment() ? null : request.getAnnouncementEndDate();

        long updatedCount =
                jpaQueryFactory
                        .update(qTeamMemberAnnouncement)
                        .set(
                                qTeamMemberAnnouncement.announcementTitle,
                                request.getAnnouncementTitle())
                        .set(qTeamMemberAnnouncement.announcementEndDate, announcementEndDateValue)
                        .set(
                                qTeamMemberAnnouncement.isPermanentRecruitment,
                                request.getIsPermanentRecruitment())
                        .set(
                                qTeamMemberAnnouncement.isAnnouncementInProgress,
                                request.getIsAnnouncementInProgress())
                        .set(
                                qTeamMemberAnnouncement.isRegionFlexible,
                                request.getIsRegionFlexible())
                        .set(
                                qTeamMemberAnnouncement.projectIntroduction,
                                request.getProjectIntroduction())
                        .set(qTeamMemberAnnouncement.mainTasks, request.getMainTasks())
                        .set(qTeamMemberAnnouncement.workMethod, request.getWorkMethod())
                        .set(qTeamMemberAnnouncement.idealCandidate, request.getIdealCandidate())
                        .set(
                                qTeamMemberAnnouncement.preferredQualifications,
                                request.getPreferredQualifications())
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
    public TeamMemberAnnouncement updateTeamMemberAnnouncementPublicState(
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final boolean isTeamMemberAnnouncementCurrentPublicState) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement =
                QTeamMemberAnnouncement.teamMemberAnnouncement;

        // QueryDSL을 사용하여 데이터베이스에서 ProfileLog 엔티티를 업데이트
        long updatedCount =
                jpaQueryFactory
                        .update(qTeamMemberAnnouncement)
                        .set(
                                qTeamMemberAnnouncement.isAnnouncementPublic,
                                !isTeamMemberAnnouncementCurrentPublicState)
                        .where(qTeamMemberAnnouncement.id.eq(teamMemberAnnouncement.getId()))
                        .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) { // 업데이트 성공 확인
            teamMemberAnnouncement.setIsAnnouncementPublic(
                    !isTeamMemberAnnouncementCurrentPublicState); // 메모리 내 객체 업데이트
            return teamMemberAnnouncement;
        } else {
            throw new IllegalStateException("팀원 공고 공개/비공개 업데이트 실패");
        }
    }

    @Override
    public Page<TeamMemberAnnouncement> findAll(
            final List<String> subPosition,
            final List<String> cityName,
            final List<String> scaleName,
            final Pageable pageable) {

        QTeamMemberAnnouncement qTeamMemberAnnouncement =
                QTeamMemberAnnouncement.teamMemberAnnouncement;
        QTeam qTeam = QTeam.team;

        JPAQuery<Long> announcementIdQuery =
                jpaQueryFactory
                        .select(qTeamMemberAnnouncement.id)
                        .distinct()
                        .from(qTeamMemberAnnouncement)
                        .leftJoin(qTeamMemberAnnouncement.team, qTeam)
                        .where(
                                qTeamMemberAnnouncement
                                        .status
                                        .eq(StatusType.USABLE)
                                        .and(
                                                qTeamMemberAnnouncement.isAnnouncementPublic.eq(
                                                        true)));

        if (isNotEmpty(subPosition)) {
            QAnnouncementPosition qAnnouncementPosition =
                    QAnnouncementPosition.announcementPosition;
            QPosition qPosition = QPosition.position;

            announcementIdQuery
                    .leftJoin(qAnnouncementPosition)
                    .on(qAnnouncementPosition.teamMemberAnnouncement.eq(qTeamMemberAnnouncement))
                    .leftJoin(qPosition)
                    .on(qAnnouncementPosition.position.eq(qPosition))
                    .where(qPosition.subPosition.in(subPosition));
        }

        // cityName 필터 처리: 팀의 지역 정보를 통해 필터링
        if (isNotEmpty(cityName)) {
            QTeamRegion qTeamRegion = QTeamRegion.teamRegion;
            QRegion qRegion = QRegion.region;

            // 이미 qTeamMemberAnnouncement.team과 qTeam은 조인된 상태이므로,
            // qTeam에서 teamRegions를 조인하고, 그 안의 region을 조인하여 조건 적용
            announcementIdQuery
                    .leftJoin(qTeam.teamRegions, qTeamRegion)
                    .leftJoin(qTeamRegion.region, qRegion)
                    .where(qRegion.cityName.in(cityName));
        }

        if (isNotEmpty(scaleName)) {
            QTeamScale qTeamScale = QTeamScale.teamScale;
            QScale qScale = QScale.scale;

            announcementIdQuery
                    .leftJoin(qTeamScale)
                    .on(qTeamScale.team.eq(qTeam))
                    .leftJoin(qScale)
                    .on(qTeamScale.scale.eq(qScale))
                    .where(qScale.scaleName.in(scaleName));
        }

        List<Long> announcementIds =
                announcementIdQuery
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        // 2. 실제 데이터 조회 (fetch join으로 연관관계 로딩)
        List<TeamMemberAnnouncement> content =
                jpaQueryFactory
                        .selectFrom(qTeamMemberAnnouncement)
                        .leftJoin(qTeamMemberAnnouncement.team, qTeam)
                        .leftJoin(qTeamMemberAnnouncement.announcementPosition)
                        .fetchJoin()
                        .leftJoin(qTeam.teamScales)
                        .leftJoin(qTeam.teamRegions)
                        .where(qTeamMemberAnnouncement.id.in(announcementIds))
                        .orderBy(
                                QueryDslUtil.getOrderAnnouncementSpecifier(
                                        pageable.getSort(),
                                        qTeamMemberAnnouncement,
                                        QAnnouncementPosition.announcementPosition,
                                        QTeamRegion.teamRegion,
                                        QTeamScale.teamScale))
                        .distinct()
                        .fetch();

        // 3. Count 쿼리
        JPAQuery<Long> countQuery =
                jpaQueryFactory
                        .select(qTeamMemberAnnouncement.countDistinct())
                        .from(qTeamMemberAnnouncement)
                        .leftJoin(qTeamMemberAnnouncement.team, qTeam)
                        .where(
                                qTeamMemberAnnouncement
                                        .status
                                        .eq(StatusType.USABLE)
                                        .and(
                                                qTeamMemberAnnouncement.isAnnouncementPublic.eq(
                                                        true)));

        applyFiltersToCountQuery(
                countQuery, qTeamMemberAnnouncement, qTeam, subPosition, cityName, scaleName);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private void applyFiltersToCountQuery(
            JPAQuery<Long> countQuery,
            QTeamMemberAnnouncement qTeamMemberAnnouncement,
            QTeam qTeam,
            List<String> subPosition,
            List<String> cityName,
            List<String> scaleName) {
        if (isNotEmpty(subPosition)) {
            QAnnouncementPosition qAnnouncementPosition =
                    QAnnouncementPosition.announcementPosition;
            QPosition qPosition = QPosition.position;

            countQuery
                    .leftJoin(qAnnouncementPosition)
                    .on(qAnnouncementPosition.teamMemberAnnouncement.eq(qTeamMemberAnnouncement))
                    .leftJoin(qPosition)
                    .on(qAnnouncementPosition.position.eq(qPosition))
                    .where(qPosition.subPosition.in(subPosition));
        }

        if (isNotEmpty(cityName)) {
            QTeamRegion qTeamRegion = QTeamRegion.teamRegion;
            QRegion qRegion = QRegion.region;

            countQuery
                    .leftJoin(qTeamRegion)
                    .on(qTeamRegion.team.eq(qTeam))
                    .leftJoin(qRegion)
                    .on(qTeamRegion.region.eq(qRegion))
                    .where(qRegion.cityName.in(cityName));
        }

        if (isNotEmpty(scaleName)) {
            QTeamScale qTeamScale = QTeamScale.teamScale;
            QScale qScale = QScale.scale;

            countQuery
                    .leftJoin(qTeamScale)
                    .on(qTeamScale.team.eq(qTeam))
                    .leftJoin(qScale)
                    .on(qTeamScale.scale.eq(qScale))
                    .where(qScale.scaleName.in(scaleName));
        }
    }

    @Override
    public Page<TeamMemberAnnouncement> findHotAnnouncements(final Pageable pageable) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement =
                QTeamMemberAnnouncement.teamMemberAnnouncement;

        // LazyInitializationException 이슈 해결
        List<TeamMemberAnnouncement> content =
                jpaQueryFactory
                        .selectFrom(qTeamMemberAnnouncement)
                        // 연관관계를 fetch join으로 미리 가져오는 예 (OneToMany라면 leftJoin + fetchJoin)
                        .leftJoin(qTeamMemberAnnouncement.team, QTeam.team)
                        .fetchJoin()
                        // 필요에 따라 다른 연관관계도 fetchJoin
                        .where(
                                qTeamMemberAnnouncement
                                        .status
                                        .eq(StatusType.USABLE)
                                        .and(qTeamMemberAnnouncement.isAnnouncementPublic.eq(true)))
                        .orderBy(
                                // CASE WHEN 구문으로 지정한 순서대로 정렬
                                new CaseBuilder()
                                        .when(qTeamMemberAnnouncement.id.eq(69L))
                                        .then(0)
                                        .when(qTeamMemberAnnouncement.id.eq(67L))
                                        .then(1)
                                        .when(qTeamMemberAnnouncement.id.eq(47L))
                                        .then(2)
                                        .when(qTeamMemberAnnouncement.id.eq(40L))
                                        .then(3)
                                        .when(qTeamMemberAnnouncement.id.eq(38L))
                                        .then(4)
                                        .when(qTeamMemberAnnouncement.id.eq(42L))
                                        .then(5)
                                        .when(qTeamMemberAnnouncement.id.eq(36L))
                                        .then(6)
                                        .when(qTeamMemberAnnouncement.id.eq(32L))
                                        .then(7)
                                        .when(qTeamMemberAnnouncement.id.eq(50L))
                                        .then(8)
                                        .otherwise(9)
                                        .asc())
                        .limit(6)
                        .fetch();

        // Pageable 정보와 함께 Page 객체로 반환 (항상 최대 6개의 레코드)
        return PageableExecutionUtils.getPage(content, pageable, content::size);
    }

    @Override
    public Page<TeamMemberAnnouncement> findExcludedAnnouncements(
            final List<Long> excludeAnnouncementIds, final Pageable pageable) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement =
                QTeamMemberAnnouncement.teamMemberAnnouncement;

        List<Long> announcementIds =
                jpaQueryFactory
                        .select(qTeamMemberAnnouncement.id)
                        .from(qTeamMemberAnnouncement)
                        .where(
                                qTeamMemberAnnouncement
                                        .status
                                        .eq(StatusType.USABLE)
                                        .and(qTeamMemberAnnouncement.isAnnouncementPublic.eq(true))
                                        .and(
                                                excludeAnnouncementIds.isEmpty()
                                                        ? null
                                                        : qTeamMemberAnnouncement.id.notIn(
                                                                excludeAnnouncementIds)))
                        .orderBy(qTeamMemberAnnouncement.createdAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        if (announcementIds.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0L);
        }

        List<TeamMemberAnnouncement> content =
                jpaQueryFactory
                        .selectFrom(qTeamMemberAnnouncement)
                        .where(qTeamMemberAnnouncement.id.in(announcementIds))
                        .orderBy(qTeamMemberAnnouncement.createdAt.desc())
                        .distinct()
                        .fetch();

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
                                    .select(qTeamMemberAnnouncement.count())
                                    .from(qTeamMemberAnnouncement)
                                    .where(
                                            qTeamMemberAnnouncement
                                                    .status
                                                    .eq(StatusType.USABLE)
                                                    .and(
                                                            qTeamMemberAnnouncement
                                                                    .isAnnouncementPublic.eq(true))
                                                    .and(
                                                            excludeAnnouncementIds.isEmpty()
                                                                    ? null
                                                                    : qTeamMemberAnnouncement.id
                                                                            .notIn(
                                                                                    excludeAnnouncementIds)))
                                    .fetchOne();

                    return count != null ? count : 0L;
                });
    }

    @Override
    public CursorResponse<TeamMemberAnnouncement> findAllExcludingIdsWithCursor(
            final List<Long> excludeAnnouncementIds, final CursorRequest cursorRequest) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement =
                QTeamMemberAnnouncement.teamMemberAnnouncement;

        try {
            // 기본 쿼리 조건
            BooleanExpression baseCondition =
                    qTeamMemberAnnouncement
                            .status
                            .eq(StatusType.USABLE)
                            .and(qTeamMemberAnnouncement.isAnnouncementPublic.eq(true));

            // ID 제외 조건
            if (excludeAnnouncementIds != null && !excludeAnnouncementIds.isEmpty()) {
                baseCondition =
                        baseCondition.and(qTeamMemberAnnouncement.id.notIn(excludeAnnouncementIds));
            }

            // 커서 조건
            if (cursorRequest != null
                    && cursorRequest.hasNext()
                    && cursorRequest.cursor() != null) {
                baseCondition =
                        baseCondition.and(
                                qTeamMemberAnnouncement.id.lt(
                                        Long.valueOf(cursorRequest.cursor())));
            }

            // 페이지 크기 안전하게 설정
            int requestedSize = (cursorRequest != null) ? Math.max(1, cursorRequest.size()) : 10;
            int pageSize = (requestedSize % 6 == 0) ? requestedSize : (requestedSize / 6 + 1) * 6;

            // 1. ID만 먼저 조회
            List<Long> announcementIds =
                    jpaQueryFactory
                            .select(qTeamMemberAnnouncement.id)
                            .from(qTeamMemberAnnouncement)
                            .where(baseCondition)
                            .orderBy(qTeamMemberAnnouncement.createdAt.desc()) // 생성일 기준 내림차순 정렬
                            .limit(pageSize + 1) // 다음 페이지 확인을 위해 +1
                            .fetch();

            if (announcementIds.isEmpty()) {
                return CursorResponse.of(List.of(), null);
            }

            // 다음 커서 계산
            String nextCursor = null;
            boolean hasNext = announcementIds.size() > pageSize;

            if (hasNext) {
                nextCursor = String.valueOf(announcementIds.get(announcementIds.size() - 1));
                announcementIds.remove(announcementIds.size() - 1); // 마지막 요소 제거
            }

            // 2. ID로 공고 엔터티 조회
            List<TeamMemberAnnouncement> announcements =
                    jpaQueryFactory
                            .selectFrom(qTeamMemberAnnouncement)
                            // 필요한 연관관계를 모두 fetch join
                            .leftJoin(qTeamMemberAnnouncement.team, QTeam.team)
                            .fetchJoin()
                            .leftJoin(qTeamMemberAnnouncement.announcementPosition)
                            .fetchJoin()
                            .where(qTeamMemberAnnouncement.id.in(announcementIds))
                            .orderBy(qTeamMemberAnnouncement.createdAt.desc())
                            .distinct()
                            .fetch();

            return CursorResponse.of(announcements, nextCursor);
        } catch (Exception e) {
            log.error("Error in findAllExcludingIdsWithCursor: {}", e.getMessage(), e);
            return CursorResponse.of(List.of(), null);
        }
    }

    @Override
    public CursorResponse<TeamMemberAnnouncement> findAllByFilteringWithCursor(
            final List<String> subPosition,
            final List<String> cityName,
            final List<String> projectTypeName,
            final List<String> workTypeName,
            final AnnouncementSortType sortType,
            final CursorRequest cursorRequest) {

        QTeamMemberAnnouncement qTeamMemberAnnouncement =
                QTeamMemberAnnouncement.teamMemberAnnouncement;
        QTeam qTeam = QTeam.team;

        try {
            // 1. 필터링된 공고 ID 조회
            JPAQuery<Long> announcementIdQuery =
                    jpaQueryFactory
                            .select(qTeamMemberAnnouncement.id)
                            .distinct()
                            .from(qTeamMemberAnnouncement)
                            .leftJoin(qTeamMemberAnnouncement.team, qTeam)
                            .where(
                                    qTeamMemberAnnouncement
                                            .status
                                            .eq(StatusType.USABLE)
                                            .and(
                                                    qTeamMemberAnnouncement.isAnnouncementPublic.eq(
                                                            true)));

            // 커서 조건 추가
            if (cursorRequest != null
                    && cursorRequest.hasNext()
                    && cursorRequest.cursor() != null) {
                announcementIdQuery =
                        announcementIdQuery.where(
                                qTeamMemberAnnouncement.id.lt(
                                        Long.valueOf(cursorRequest.cursor())));
            }

            // subPosition 필터링
            if (isNotEmpty(subPosition)) {
                QAnnouncementPosition qAnnouncementPosition =
                        QAnnouncementPosition.announcementPosition;
                QPosition qPosition = QPosition.position;

                announcementIdQuery
                        .leftJoin(qAnnouncementPosition)
                        .on(
                                qAnnouncementPosition.teamMemberAnnouncement.eq(
                                        qTeamMemberAnnouncement))
                        .leftJoin(qPosition)
                        .on(qAnnouncementPosition.position.eq(qPosition))
                        .where(qPosition.subPosition.in(subPosition));
            }

            // cityName 필터링
            if (isNotEmpty(cityName)) {
                QTeamRegion qTeamRegion = QTeamRegion.teamRegion;
                QRegion qRegion = QRegion.region;

                announcementIdQuery
                        .leftJoin(qTeam.teamRegions, qTeamRegion)
                        .leftJoin(qTeamRegion.region, qRegion)
                        .where(qRegion.cityName.in(cityName));
            }

            if (isNotEmpty(projectTypeName)) {
                QAnnouncementProjectType qAnnouncementProjectType =
                        QAnnouncementProjectType.announcementProjectType;
                QProjectType qProjectType = QProjectType.projectType;

                announcementIdQuery
                        .leftJoin(qAnnouncementProjectType)
                        .on(
                                qAnnouncementProjectType.teamMemberAnnouncement.eq(
                                        qTeamMemberAnnouncement))
                        .leftJoin(qProjectType)
                        .on(qAnnouncementProjectType.projectType.eq(qProjectType))
                        .where(qProjectType.projectTypeName.in(projectTypeName));
            }

            if (isNotEmpty(workTypeName)) {
                QAnnouncementWorkType qAnnouncementWorkType =
                        QAnnouncementWorkType.announcementWorkType;
                QWorkType qWorkType = QWorkType.workType;

                announcementIdQuery
                        .leftJoin(qAnnouncementWorkType)
                        .on(
                                qAnnouncementWorkType.teamMemberAnnouncement.eq(
                                        qTeamMemberAnnouncement))
                        .leftJoin(qWorkType)
                        .on(qAnnouncementWorkType.workType.eq(qWorkType))
                        .where(qWorkType.workTypeName.in(workTypeName));
            }

            // ID 내림차순 정렬 및 제한
            int requestedSize = (cursorRequest != null) ? Math.max(1, cursorRequest.size()) : 10;
            int pageSize = (requestedSize % 6 == 0) ? requestedSize : (requestedSize / 6 + 1) * 6;

            // 정렬 조건 적용
            List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

            // sortType이 null인 경우 LATEST로 처리
            AnnouncementSortType effectiveSortType =
                    (sortType != null) ? sortType : AnnouncementSortType.LATEST;

            switch (effectiveSortType) {
                case LATEST:
                    orderSpecifiers.add(qTeamMemberAnnouncement.createdAt.desc());
                    break;
                case POPULAR:
                    orderSpecifiers.add(qTeamMemberAnnouncement.viewCount.desc());
                    break;
                case DEADLINE:
                    // Define common conditions first for better readability
                    String todayYearMonth =
                            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

                    BooleanExpression isInProgress =
                            qTeamMemberAnnouncement
                                    .isAnnouncementInProgress
                                    .eq(true)
                                    .and(
                                            qTeamMemberAnnouncement.announcementEndDate.gt(
                                                    todayYearMonth));

                    BooleanExpression isClosed =
                            qTeamMemberAnnouncement.isAnnouncementInProgress.eq(false);

                    // 1. 그룹 우선순위 정렬
                    // 0: 상시모집 → 1: 모집중 → 2: 모집마감 → 3: 기타
                    orderSpecifiers.add(
                            new CaseBuilder()
                                    .when(qTeamMemberAnnouncement.isPermanentRecruitment.eq(true))
                                    .then(0)
                                    .when(isInProgress)
                                    .then(1)
                                    .when(isClosed)
                                    .then(2)
                                    .otherwise(3)
                                    .asc());

                    // 2. 상시 모집: 생성일 오름차순
                    orderSpecifiers.add(
                            new CaseBuilder()
                                    .when(qTeamMemberAnnouncement.isPermanentRecruitment.eq(true))
                                    .then(qTeamMemberAnnouncement.createdAt)
                                    .otherwise((LocalDateTime) null)
                                    .asc()
                                    .nullsLast());

                    // 3. 모집 중: 마감일 오름차순
                    orderSpecifiers.add(
                            new CaseBuilder()
                                    .when(isInProgress)
                                    .then(qTeamMemberAnnouncement.announcementEndDate)
                                    .otherwise((String) null)
                                    .asc()
                                    .nullsLast());

                    // 4. 모집 마감: 생성일 오름차순
                    orderSpecifiers.add(
                            new CaseBuilder()
                                    .when(isClosed)
                                    .then(qTeamMemberAnnouncement.createdAt)
                                    .otherwise((LocalDateTime) null)
                                    .asc()
                                    .nullsLast());
                    break;
                default:
                    orderSpecifiers.add(qTeamMemberAnnouncement.createdAt.desc());
            }

            // 정렬 조건을 ID 쿼리에도 적용
            OrderSpecifier<?>[] orderArray = orderSpecifiers.toArray(new OrderSpecifier[0]);

            List<Long> announcementIds =
                    announcementIdQuery
                            .orderBy(orderArray)
                            .limit(pageSize + 1) // 다음 페이지 확인을 위해 +1
                            .fetch();

            // 조회할 공고가 없는 경우 빈 응답 반환
            if (announcementIds.isEmpty()) {
                return CursorResponse.of(List.of(), null);
            }

            // 다음 커서 계산
            boolean hasNext = announcementIds.size() > pageSize;
            String nextCursor = null;

            // 다음 페이지가 있는 경우
            if (hasNext) {
                nextCursor = String.valueOf(announcementIds.get(announcementIds.size() - 1));
                announcementIds.remove(announcementIds.size() - 1); // 마지막 요소 제거
            }

            // 2. 실제 데이터 조회
            List<TeamMemberAnnouncement> announcements =
                    jpaQueryFactory
                            .selectFrom(qTeamMemberAnnouncement)
                            .leftJoin(qTeamMemberAnnouncement.team, QTeam.team)
                            .fetchJoin()
                            .leftJoin(qTeamMemberAnnouncement.announcementPosition)
                            .fetchJoin()
                            .leftJoin(qTeamMemberAnnouncement.announcementProjectType)
                            .fetchJoin()
                            .leftJoin(qTeamMemberAnnouncement.announcementWorkType)
                            .fetchJoin()
                            .where(qTeamMemberAnnouncement.id.in(announcementIds))
                            .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                            .distinct()
                            .fetch();

            return CursorResponse.of(announcements, nextCursor);
        } catch (Exception e) {
            log.error("Error in findAllByFilteringWithCursor: {}", e.getMessage(), e);
            return CursorResponse.of(List.of(), null);
        }
    }

    @Override
    public List<TeamMemberAnnouncement> findHomeTopTeamMemberAnnouncements(final int limit) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement =
                QTeamMemberAnnouncement.teamMemberAnnouncement;

        return jpaQueryFactory
                .selectFrom(qTeamMemberAnnouncement)
                .where(
                        qTeamMemberAnnouncement.isAnnouncementPublic.eq(true),
                        qTeamMemberAnnouncement.status.eq(StatusType.USABLE))
                .orderBy(
                        // CASE WHEN 구문으로 지정한 순서대로 정렬
                        new CaseBuilder()
                                .when(qTeamMemberAnnouncement.id.eq(69L))
                                .then(0)
                                .when(qTeamMemberAnnouncement.id.eq(67L))
                                .then(1)
                                .when(qTeamMemberAnnouncement.id.eq(47L))
                                .then(2)
                                .when(qTeamMemberAnnouncement.id.eq(40L))
                                .then(3)
                                .when(qTeamMemberAnnouncement.id.eq(38L))
                                .then(4)
                                .when(qTeamMemberAnnouncement.id.eq(42L))
                                .then(5)
                                .when(qTeamMemberAnnouncement.id.eq(36L))
                                .then(6)
                                .when(qTeamMemberAnnouncement.id.eq(32L))
                                .then(7)
                                .when(qTeamMemberAnnouncement.id.eq(22L))
                                .then(8)
                                .otherwise(9)
                                .asc())
                .limit(limit)
                .fetch();
    }

    @Override
    public Set<TeamMemberAnnouncement> getAllDeletableTeamMemberAnnouncementsByTeamIds(
            final List<Long> teamIds) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement =
                QTeamMemberAnnouncement.teamMemberAnnouncement;

        if (teamIds == null || teamIds.isEmpty()) {
            return Collections.emptySet();
        }

        return new HashSet<>(
                jpaQueryFactory
                        .selectFrom(qTeamMemberAnnouncement)
                        .where(qTeamMemberAnnouncement.team.id.in(teamIds))
                        .fetch());
    }

    @Override
    public Set<TeamMemberAnnouncement> getAllDeletableTeamMemberAnnouncementsByTeamId(
            final Long teamId) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement =
                QTeamMemberAnnouncement.teamMemberAnnouncement;

        if (teamId == null) {
            return Collections.emptySet();
        }

        return new HashSet<>(
                jpaQueryFactory
                        .selectFrom(qTeamMemberAnnouncement)
                        .where(
                                qTeamMemberAnnouncement.team.id.eq(teamId) // 특정 팀 ID와
                                // 일치
                                )
                        .fetch());
    }

    @Override
    public void deleteAllByIds(final List<Long> announcementIds) {
        QTeamMemberAnnouncement qAnnouncement = QTeamMemberAnnouncement.teamMemberAnnouncement;

        try {
            long updatedCount =
                    jpaQueryFactory
                            .update(qAnnouncement)
                            .set(qAnnouncement.status, StatusType.DELETED)
                            .where(qAnnouncement.id.in(announcementIds))
                            .execute();

            entityManager.flush();
            entityManager.clear();

        } catch (Exception e) {
            log.error(
                    "Error occurred while deleting announcements with IDs: {}", announcementIds, e);
        }
    }

    @Override
    public List<TeamMemberAnnouncement> findAllAnnouncementsByTeamId(final Long teamId) {
        QTeamMemberAnnouncement q = QTeamMemberAnnouncement.teamMemberAnnouncement;
        String todayYearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        BooleanExpression isInProgress =
                q.isAnnouncementInProgress.eq(true).and(q.announcementEndDate.gt(todayYearMonth));

        BooleanExpression isClosed = q.isAnnouncementInProgress.eq(false);

        return jpaQueryFactory
                .selectFrom(q)
                .where(q.team.id.eq(teamId))
                .orderBy(
                        // 1. 그룹 우선순위
                        new CaseBuilder()
                                .when(q.isPermanentRecruitment.eq(true))
                                .then(0) // 상시
                                .when(isInProgress)
                                .then(1) // 모집 중
                                .when(isClosed)
                                .then(2) // 마감
                                .otherwise(3)
                                .asc(),

                        // 2. 상시 모집 정렬
                        new CaseBuilder()
                                .when(q.isPermanentRecruitment.eq(true))
                                .then(q.createdAt)
                                .otherwise((LocalDateTime) null)
                                .asc()
                                .nullsLast(),

                        // 3. 모집 중 정렬: 마감일 오름차순
                        new CaseBuilder()
                                .when(isInProgress)
                                .then(q.announcementEndDate)
                                .otherwise((String) null)
                                .asc()
                                .nullsLast(),

                        // 4. 마감 정렬: 등록일 오름차순
                        new CaseBuilder()
                                .when(isClosed)
                                .then(q.createdAt)
                                .otherwise((LocalDateTime) null)
                                .asc()
                                .nullsLast())
                .fetch();
    }

    @Override
    public List<TeamMemberAnnouncement> findPublicAnnouncementsByTeamId(final Long teamId) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement =
                QTeamMemberAnnouncement.teamMemberAnnouncement;
        String todayYearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        BooleanExpression isInProgress =
                qTeamMemberAnnouncement
                        .isAnnouncementInProgress
                        .eq(true)
                        .and(qTeamMemberAnnouncement.announcementEndDate.gt(todayYearMonth));

        BooleanExpression isClosed = qTeamMemberAnnouncement.isAnnouncementInProgress.eq(false);

        return jpaQueryFactory
                .selectFrom(qTeamMemberAnnouncement)
                .where(
                        qTeamMemberAnnouncement
                                .team
                                .id
                                .eq(teamId)
                                .and(qTeamMemberAnnouncement.isAnnouncementPublic.eq(true)))
                .orderBy(
                        // 1. 그룹 우선순위
                        new CaseBuilder()
                                .when(qTeamMemberAnnouncement.isPermanentRecruitment.eq(true))
                                .then(0) // 상시
                                .when(isInProgress)
                                .then(1) // 모집 중
                                .when(isClosed)
                                .then(2) // 마감
                                .otherwise(3)
                                .asc(),

                        // 2. 상시 모집 정렬
                        new CaseBuilder()
                                .when(qTeamMemberAnnouncement.isPermanentRecruitment.eq(true))
                                .then(qTeamMemberAnnouncement.createdAt)
                                .otherwise((LocalDateTime) null)
                                .asc()
                                .nullsLast(),

                        // 3. 모집 중 정렬: 마감일 오름차순
                        new CaseBuilder()
                                .when(isInProgress)
                                .then(qTeamMemberAnnouncement.announcementEndDate)
                                .otherwise((String) null)
                                .asc()
                                .nullsLast(),

                        // 4. 마감 정렬: 등록일 오름차순
                        new CaseBuilder()
                                .when(isClosed)
                                .then(qTeamMemberAnnouncement.createdAt)
                                .otherwise((LocalDateTime) null)
                                .asc()
                                .nullsLast())
                .fetch();
    }

    private boolean isNotEmpty(List<?> list) {
        return list != null && !list.isEmpty();
    }

    @Override
    public List<TeamMemberAnnouncement> findRecentPublicAnnouncementsNotAdvertised(
            final LocalDateTime since) {
        QTeamMemberAnnouncement q = QTeamMemberAnnouncement.teamMemberAnnouncement;

        // First check just by date
        List<TeamMemberAnnouncement> dateOnly =
                jpaQueryFactory.selectFrom(q).where(q.createdAt.goe(since)).fetch();
        log.info("Records by date only: {}", dateOnly.size());

        // Then add each condition one by one
        List<TeamMemberAnnouncement> withPublic =
                jpaQueryFactory
                        .selectFrom(q)
                        .where(q.createdAt.goe(since), q.isAnnouncementPublic.eq(true))
                        .fetch();
        log.info("With isPublic: {}", withPublic.size());

        // Complete query with all conditions
        return jpaQueryFactory
                .selectFrom(q)
                .where(
                        q.createdAt.goe(since),
                        q.isAnnouncementPublic.eq(true),
                        q.isAnnouncementInProgress.eq(true),
                        q.isAdvertisingMailSent.eq(false))
                .orderBy(q.createdAt.desc())
                .fetch();
    }

    @Override
    public TeamMemberAnnouncement updateTeamMemberAnnouncementClosedState(
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final boolean isTeamMemberAnnouncementInProgress) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement =
                QTeamMemberAnnouncement.teamMemberAnnouncement;

        // QueryDSL을 사용하여 데이터베이스에서 ProfileLog 엔티티를 업데이트
        long updatedCount =
                jpaQueryFactory
                        .update(qTeamMemberAnnouncement)
                        .set(
                                qTeamMemberAnnouncement.isAnnouncementInProgress,
                                isTeamMemberAnnouncementInProgress)
                        .where(qTeamMemberAnnouncement.id.eq(teamMemberAnnouncement.getId()))
                        .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) { // 업데이트 성공 확인
            teamMemberAnnouncement.setIsAnnouncementPublic(
                    isTeamMemberAnnouncementInProgress); // 메모리 내 객체 업데이트
            return teamMemberAnnouncement;
        } else {
            throw new IllegalStateException("팀원 공고 공개/비공개 업데이트 실패");
        }
    }

    @Override
    public void incrementViewCount(final Long announcementId) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement =
                QTeamMemberAnnouncement.teamMemberAnnouncement;

        jpaQueryFactory
                .update(qTeamMemberAnnouncement)
                .set(qTeamMemberAnnouncement.viewCount, qTeamMemberAnnouncement.viewCount.add(1))
                .where(qTeamMemberAnnouncement.id.eq(announcementId))
                .execute();

        // 영속성 컨텍스트 초기화
        entityManager.flush();
        entityManager.clear();
    }

    @Override
    public List<TeamMemberAnnouncement> findAllByIsNotPermanentRecruitment() {
        QTeamMemberAnnouncement qTeamMemberAnnouncement =
                QTeamMemberAnnouncement.teamMemberAnnouncement;

        return jpaQueryFactory
                .selectFrom(qTeamMemberAnnouncement)
                .where(
                        qTeamMemberAnnouncement.status.eq(StatusType.USABLE),
                        qTeamMemberAnnouncement.isAnnouncementInProgress.isTrue(),
                        qTeamMemberAnnouncement.isPermanentRecruitment.isFalse())
                .orderBy(qTeamMemberAnnouncement.id.asc())
                .fetch();
    }

    @Override
    public List<AnnouncementDynamicResponse> findAllDynamicVariablesWithTeamMemberAnnouncement() {
        QTeamMemberAnnouncement qTeamMemberAnnouncement =
                QTeamMemberAnnouncement.teamMemberAnnouncement;
        QTeam qTeam = QTeam.team;

        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                AnnouncementDynamicResponse.class,
                                qTeam.teamName,
                                qTeam.teamCode,
                                qTeamMemberAnnouncement.id,
                                qTeam.createdAt))
                .from(qTeamMemberAnnouncement)
                .leftJoin(qTeamMemberAnnouncement.team, qTeam)
                .where(
                        qTeamMemberAnnouncement
                                .status
                                .eq(USABLE)
                                .and(qTeamMemberAnnouncement.isAnnouncementPublic.eq(true)))
                .orderBy(qTeamMemberAnnouncement.id.desc())
                .fetch();
    }

    public List<FlatAnnouncementDTO> findHomeTopAnnouncements(final int limit) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement =
                QTeamMemberAnnouncement.teamMemberAnnouncement;
        QTeam qTeam = QTeam.team;

        QTeamScale qTeamScale = QTeamScale.teamScale;
        QScale qScale = QScale.scale;

        QTeamRegion qTeamRegion = QTeamRegion.teamRegion;
        QRegion qRegion = QRegion.region;

        QAnnouncementPosition qAnnouncementPosition = QAnnouncementPosition.announcementPosition;
        QPosition qPosition = QPosition.position;

        QAnnouncementSkill qAnnouncementSkill = QAnnouncementSkill.announcementSkill;
        QSkill qSkill = QSkill.skill;

        List<Long> targetAnnouncementIds =
                jpaQueryFactory
                        .select(qTeamMemberAnnouncement.id)
                        .from(qTeamMemberAnnouncement)
                        .where(
                                qTeamMemberAnnouncement
                                        .status
                                        .eq(USABLE)
                                        .and(qTeamMemberAnnouncement.isAnnouncementPublic.eq(true))
                                        .and(
                                                qTeamMemberAnnouncement.id.in(
                                                        86L, 85L, 84L, 79L, 80L, 82L, 72L, 70L,
                                                        76L)))
                        .orderBy(
                                // CASE WHEN 구문으로 지정한 순서대로 정렬
                                new CaseBuilder()
                                        .when(qTeamMemberAnnouncement.id.eq(86L))
                                        .then(0)
                                        .when(qTeamMemberAnnouncement.id.eq(85L))
                                        .then(1)
                                        .when(qTeamMemberAnnouncement.id.eq(84L))
                                        .then(2)
                                        .when(qTeamMemberAnnouncement.id.eq(79L))
                                        .then(3)
                                        .when(qTeamMemberAnnouncement.id.eq(80L))
                                        .then(4)
                                        .when(qTeamMemberAnnouncement.id.eq(82L))
                                        .then(5)
                                        .when(qTeamMemberAnnouncement.id.eq(72L))
                                        .then(6)
                                        .when(qTeamMemberAnnouncement.id.eq(70L))
                                        .then(7)
                                        .when(qTeamMemberAnnouncement.id.eq(76L))
                                        .then(8)
                                        .otherwise(9)
                                        .asc())
                        .limit(limit)
                        .fetch();

        return jpaQueryFactory
                .select(
                        Projections.fields(
                                FlatAnnouncementDTO.class,
                                qTeamMemberAnnouncement.id.as("teamMemberAnnouncementId"),
                                qTeam.teamLogoImagePath.as("teamLogoImagePath"),
                                qTeam.teamName.as("teamName"),
                                qTeam.teamCode.as("teamCode"),
                                qScale.scaleName.as("teamScaleName"),
                                qRegion.cityName.as("cityName"),
                                qRegion.divisionName.as("divisionName"),
                                qTeamMemberAnnouncement.isAnnouncementInProgress.as(
                                        "isAnnouncementInProgress"),
                                qTeamMemberAnnouncement.announcementEndDate.as(
                                        "announcementEndDate"),
                                qTeamMemberAnnouncement.isPermanentRecruitment.as(
                                        "isPermanentRecruitment"),
                                qTeamMemberAnnouncement.announcementTitle.as("announcementTitle"),
                                qTeamMemberAnnouncement.viewCount.as("viewCount"),
                                qPosition.majorPosition.as("majorPosition"),
                                qPosition.subPosition.as("subPosition"),
                                qSkill.skillName.as("announcementSkillName"),
                                qTeamMemberAnnouncement.createdAt.as("createdAt")))
                .from(qTeamMemberAnnouncement)
                .leftJoin(qTeamMemberAnnouncement.team, qTeam)
                .leftJoin(qTeam.teamScales, qTeamScale)
                .leftJoin(qTeamScale.scale, qScale)
                .leftJoin(qTeam.teamRegions, qTeamRegion)
                .leftJoin(qTeamRegion.region, qRegion)
                .leftJoin(qTeamMemberAnnouncement.announcementPosition, qAnnouncementPosition)
                .leftJoin(qAnnouncementPosition.position, qPosition)
                .leftJoin(qTeamMemberAnnouncement.announcementSkills, qAnnouncementSkill)
                .leftJoin(qAnnouncementSkill.skill, qSkill)
                .where(qTeamMemberAnnouncement.id.in(targetAnnouncementIds))
                .orderBy(
                        // CASE WHEN 구문으로 지정한 순서대로 정렬
                        new CaseBuilder()
                                .when(qTeamMemberAnnouncement.id.eq(86L))
                                .then(0)
                                .when(qTeamMemberAnnouncement.id.eq(85L))
                                .then(1)
                                .when(qTeamMemberAnnouncement.id.eq(84L))
                                .then(2)
                                .when(qTeamMemberAnnouncement.id.eq(79L))
                                .then(3)
                                .when(qTeamMemberAnnouncement.id.eq(80L))
                                .then(4)
                                .when(qTeamMemberAnnouncement.id.eq(82L))
                                .then(5)
                                .when(qTeamMemberAnnouncement.id.eq(72L))
                                .then(6)
                                .when(qTeamMemberAnnouncement.id.eq(70L))
                                .then(7)
                                .when(qTeamMemberAnnouncement.id.eq(76L))
                                .then(8)
                                .otherwise(9)
                                .asc())
                .fetch();
    }

    public List<FlatAnnouncementDTO> findTopHotAnnouncements(final int limit) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement =
                QTeamMemberAnnouncement.teamMemberAnnouncement;
        QTeam qTeam = QTeam.team;

        QTeamScale qTeamScale = QTeamScale.teamScale;
        QScale qScale = QScale.scale;

        QTeamRegion qTeamRegion = QTeamRegion.teamRegion;
        QRegion qRegion = QRegion.region;

        QAnnouncementPosition qAnnouncementPosition = QAnnouncementPosition.announcementPosition;
        QPosition qPosition = QPosition.position;

        QAnnouncementSkill qAnnouncementSkill = QAnnouncementSkill.announcementSkill;
        QSkill qSkill = QSkill.skill;

        QAnnouncementProjectType qAnnouncementProjectType =
                QAnnouncementProjectType.announcementProjectType;
        QProjectType qProjectType = QProjectType.projectType;

        QAnnouncementWorkType qAnnouncementWorkType = QAnnouncementWorkType.announcementWorkType;
        QWorkType qWorkType = QWorkType.workType;

        List<Long> targetAnnouncementIds =
                jpaQueryFactory
                        .select(qTeamMemberAnnouncement.id)
                        .from(qTeamMemberAnnouncement)
                        .where(
                                qTeamMemberAnnouncement
                                        .status
                                        .eq(USABLE)
                                        .and(qTeamMemberAnnouncement.isAnnouncementPublic.eq(true))
                                        .and(
                                                qTeamMemberAnnouncement.id.in(
                                                        86L, 85L, 84L, 79L, 80L, 82L, 72L, 70L,
                                                        76L)))
                        .orderBy(
                                // CASE WHEN 구문으로 지정한 순서대로 정렬
                                new CaseBuilder()
                                        .when(qTeamMemberAnnouncement.id.eq(86L))
                                        .then(0)
                                        .when(qTeamMemberAnnouncement.id.eq(85L))
                                        .then(1)
                                        .when(qTeamMemberAnnouncement.id.eq(84L))
                                        .then(2)
                                        .when(qTeamMemberAnnouncement.id.eq(79L))
                                        .then(3)
                                        .when(qTeamMemberAnnouncement.id.eq(80L))
                                        .then(4)
                                        .when(qTeamMemberAnnouncement.id.eq(82L))
                                        .then(5)
                                        .when(qTeamMemberAnnouncement.id.eq(72L))
                                        .then(6)
                                        .when(qTeamMemberAnnouncement.id.eq(70L))
                                        .then(7)
                                        .when(qTeamMemberAnnouncement.id.eq(76L))
                                        .then(8)
                                        .otherwise(9)
                                        .asc())
                        .limit(limit)
                        .fetch();

        return jpaQueryFactory
                .select(
                        Projections.fields(
                                FlatAnnouncementDTO.class,
                                qTeamMemberAnnouncement.id.as("teamMemberAnnouncementId"),
                                qTeam.teamLogoImagePath.as("teamLogoImagePath"),
                                qTeam.teamName.as("teamName"),
                                qTeam.teamCode.as("teamCode"),
                                qScale.scaleName.as("teamScaleName"),
                                qRegion.cityName.as("cityName"),
                                qRegion.divisionName.as("divisionName"),
                                qTeamMemberAnnouncement.isAnnouncementInProgress.as(
                                        "isAnnouncementInProgress"),
                                qTeamMemberAnnouncement.announcementEndDate.as(
                                        "announcementEndDate"),
                                qTeamMemberAnnouncement.isPermanentRecruitment.as(
                                        "isPermanentRecruitment"),
                                qTeamMemberAnnouncement.announcementTitle.as("announcementTitle"),
                                qTeamMemberAnnouncement.viewCount.as("viewCount"),
                                qPosition.majorPosition.as("majorPosition"),
                                qPosition.subPosition.as("subPosition"),
                                qSkill.skillName.as("announcementSkillName"),
                                qTeamMemberAnnouncement.createdAt.as("createdAt"),
                                qProjectType.projectTypeName.as("projectTypeName"),
                                qWorkType.workTypeName.as("workTypeName")))
                .from(qTeamMemberAnnouncement)
                .leftJoin(qTeamMemberAnnouncement.team, qTeam)
                .leftJoin(qTeam.teamScales, qTeamScale)
                .leftJoin(qTeamScale.scale, qScale)
                .leftJoin(qTeam.teamRegions, qTeamRegion)
                .leftJoin(qTeamRegion.region, qRegion)
                .leftJoin(qTeamMemberAnnouncement.announcementPosition, qAnnouncementPosition)
                .leftJoin(qAnnouncementPosition.position, qPosition)
                .leftJoin(qTeamMemberAnnouncement.announcementSkills, qAnnouncementSkill)
                .leftJoin(qAnnouncementSkill.skill, qSkill)
                .leftJoin(qTeamMemberAnnouncement.announcementProjectType, qAnnouncementProjectType)
                .leftJoin(qAnnouncementProjectType.projectType, qProjectType)
                .leftJoin(qTeamMemberAnnouncement.announcementWorkType, qAnnouncementWorkType)
                .leftJoin(qAnnouncementWorkType.workType, qWorkType)
                .where(qTeamMemberAnnouncement.id.in(targetAnnouncementIds))
                .orderBy(
                        // CASE WHEN 구문으로 지정한 순서대로 정렬
                        new CaseBuilder()
                                .when(qTeamMemberAnnouncement.id.eq(86L))
                                .then(0)
                                .when(qTeamMemberAnnouncement.id.eq(85L))
                                .then(1)
                                .when(qTeamMemberAnnouncement.id.eq(84L))
                                .then(2)
                                .when(qTeamMemberAnnouncement.id.eq(79L))
                                .then(3)
                                .when(qTeamMemberAnnouncement.id.eq(80L))
                                .then(4)
                                .when(qTeamMemberAnnouncement.id.eq(82L))
                                .then(5)
                                .when(qTeamMemberAnnouncement.id.eq(72L))
                                .then(6)
                                .when(qTeamMemberAnnouncement.id.eq(70L))
                                .then(7)
                                .when(qTeamMemberAnnouncement.id.eq(76L))
                                .then(8)
                                .otherwise(9)
                                .asc())
                .fetch();
    }

    public List<FlatAnnouncementDTO> findFlatAnnouncementsWithoutCursor(
            List<Long> excludeAnnouncementIds, int size) {
        QTeamMemberAnnouncement qAnnouncement = QTeamMemberAnnouncement.teamMemberAnnouncement;
        QTeam qTeam = QTeam.team;
        QTeamScale qTeamScale = QTeamScale.teamScale;
        QScale qScale = QScale.scale;
        QTeamRegion qTeamRegion = QTeamRegion.teamRegion;
        QRegion qRegion = QRegion.region;
        QAnnouncementPosition qAnnouncementPosition = QAnnouncementPosition.announcementPosition;
        QPosition qPosition = QPosition.position;
        QAnnouncementSkill qAnnouncementSkill = QAnnouncementSkill.announcementSkill;
        QSkill qSkill = QSkill.skill;
        QAnnouncementProjectType qAnnouncementProjectType =
                QAnnouncementProjectType.announcementProjectType;
        QProjectType qProjectType = QProjectType.projectType;
        QAnnouncementWorkType qAnnouncementWorkType = QAnnouncementWorkType.announcementWorkType;
        QWorkType qWorkType = QWorkType.workType;

        return jpaQueryFactory
                .select(
                        Projections.fields(
                                FlatAnnouncementDTO.class,
                                qAnnouncement.id.as("teamMemberAnnouncementId"),
                                qTeam.teamLogoImagePath.as("teamLogoImagePath"),
                                qTeam.teamName.as("teamName"),
                                qTeam.teamCode.as("teamCode"),
                                qScale.scaleName.as("teamScaleName"),
                                qRegion.cityName.as("cityName"),
                                qRegion.divisionName.as("divisionName"),
                                qAnnouncement.isAnnouncementInProgress.as(
                                        "isAnnouncementInProgress"),
                                qAnnouncement.announcementEndDate.as("announcementEndDate"),
                                qAnnouncement.isPermanentRecruitment.as("isPermanentRecruitment"),
                                qAnnouncement.announcementTitle.as("announcementTitle"),
                                qAnnouncement.viewCount.as("viewCount"),
                                qPosition.majorPosition.as("majorPosition"),
                                qPosition.subPosition.as("subPosition"),
                                qSkill.skillName.as("announcementSkillName"),
                                qAnnouncement.createdAt.as("createdAt"),
                                qProjectType.projectTypeName.as("projectTypeName"),
                                qWorkType.workTypeName.as("workTypeName")))
                .from(qAnnouncement)
                .leftJoin(qAnnouncement.team, qTeam)
                .leftJoin(qTeam.teamScales, qTeamScale)
                .leftJoin(qTeamScale.scale, qScale)
                .leftJoin(qTeam.teamRegions, qTeamRegion)
                .leftJoin(qTeamRegion.region, qRegion)
                .leftJoin(qAnnouncement.announcementPosition, qAnnouncementPosition)
                .leftJoin(qAnnouncementPosition.position, qPosition)
                .leftJoin(qAnnouncement.announcementSkills, qAnnouncementSkill)
                .leftJoin(qAnnouncementSkill.skill, qSkill)
                .leftJoin(qAnnouncement.announcementProjectType, qAnnouncementProjectType)
                .leftJoin(qAnnouncementProjectType.projectType, qProjectType)
                .leftJoin(qAnnouncement.announcementWorkType, qAnnouncementWorkType)
                .leftJoin(qAnnouncementWorkType.workType, qWorkType)
                .where(
                        qAnnouncement
                                .status
                                .eq(StatusType.USABLE)
                                .and(qAnnouncement.isAnnouncementPublic.eq(true))
                                .and(qAnnouncement.id.notIn(excludeAnnouncementIds)))
                .orderBy(qAnnouncement.id.desc())
                .limit(size * 5)
                .fetch();
    }

    public List<FlatAnnouncementDTO> findAllAnnouncementsWithoutFilter(
            List<Long> excludeProfileIds, CursorRequest cursorRequest) {
        QTeamMemberAnnouncement qAnnouncement = QTeamMemberAnnouncement.teamMemberAnnouncement;
        QTeam qTeam = QTeam.team;
        QTeamScale qTeamScale = QTeamScale.teamScale;
        QScale qScale = QScale.scale;
        QTeamRegion qTeamRegion = QTeamRegion.teamRegion;
        QRegion qRegion = QRegion.region;
        QAnnouncementPosition qAnnouncementPosition = QAnnouncementPosition.announcementPosition;
        QPosition qPosition = QPosition.position;
        QAnnouncementSkill qAnnouncementSkill = QAnnouncementSkill.announcementSkill;
        QSkill qSkill = QSkill.skill;
        QAnnouncementProjectType qAnnouncementProjectType =
                QAnnouncementProjectType.announcementProjectType;
        QProjectType qProjectType = QProjectType.projectType;
        QAnnouncementWorkType qAnnouncementWorkType = QAnnouncementWorkType.announcementWorkType;
        QWorkType qWorkType = QWorkType.workType;

        try {
            // 1. 커서 파싱
            Long cursorId = null;
            if (cursorRequest != null
                    && cursorRequest.hasNext()
                    && cursorRequest.cursor() != null) {
                try {
                    cursorId = Long.parseLong(cursorRequest.cursor());
                } catch (NumberFormatException e) {
                    log.warn("Invalid cursor: {}", cursorRequest.cursor());
                }
            }

            // 2. 기본 조건
            BooleanExpression condition =
                    qAnnouncement
                            .status
                            .eq(USABLE)
                            .and(qAnnouncement.isAnnouncementPublic.isTrue());

            // 커서 조건 추가
            if (cursorId != null) {
                condition = condition.and(qAnnouncement.id.lt(cursorId));
            }
            // 4. 페이지 사이즈 계산
            int requestedSize = (cursorRequest != null) ? Math.max(1, cursorRequest.size()) : 10;
            int pageSize = (requestedSize % 6 == 0) ? requestedSize : ((requestedSize / 6) + 1) * 6;

            // 5. 쿼리 구성
            return jpaQueryFactory
                    .select(
                            Projections.fields(
                                    FlatAnnouncementDTO.class,
                                    qAnnouncement.id.as("teamMemberAnnouncementId"),
                                    qTeam.teamLogoImagePath.as("teamLogoImagePath"),
                                    qTeam.teamName.as("teamName"),
                                    qTeam.teamCode.as("teamCode"),
                                    qScale.scaleName.as("teamScaleName"),
                                    qRegion.cityName.as("cityName"),
                                    qRegion.divisionName.as("divisionName"),
                                    qAnnouncement.isAnnouncementInProgress.as(
                                            "isAnnouncementInProgress"),
                                    qAnnouncement.announcementEndDate.as("announcementEndDate"),
                                    qAnnouncement.isPermanentRecruitment.as(
                                            "isPermanentRecruitment"),
                                    qAnnouncement.announcementTitle.as("announcementTitle"),
                                    qAnnouncement.viewCount.as("viewCount"),
                                    qPosition.majorPosition.as("majorPosition"),
                                    qPosition.subPosition.as("subPosition"),
                                    qSkill.skillName.as("announcementSkillName"),
                                    qAnnouncement.createdAt.as("createdAt"),
                                    qProjectType.projectTypeName.as("projectTypeName"),
                                    qWorkType.workTypeName.as("workTypeName")))
                    .from(qAnnouncement)
                    .leftJoin(qAnnouncement.team, qTeam)
                    .leftJoin(qTeam.teamScales, qTeamScale)
                    .leftJoin(qTeamScale.scale, qScale)
                    .leftJoin(qTeam.teamRegions, qTeamRegion)
                    .leftJoin(qTeamRegion.region, qRegion)
                    .leftJoin(qAnnouncement.announcementPosition, qAnnouncementPosition)
                    .leftJoin(qAnnouncementPosition.position, qPosition)
                    .leftJoin(qAnnouncement.announcementSkills, qAnnouncementSkill)
                    .leftJoin(qAnnouncementSkill.skill, qSkill)
                    .leftJoin(qAnnouncement.announcementProjectType, qAnnouncementProjectType)
                    .leftJoin(qAnnouncementProjectType.projectType, qProjectType)
                    .leftJoin(qAnnouncement.announcementWorkType, qAnnouncementWorkType)
                    .leftJoin(qAnnouncementWorkType.workType, qWorkType)
                    .where(condition)
                    .orderBy(qAnnouncement.id.desc())
                    .limit(pageSize)
                    .fetch();
        } catch (Exception e) {
            log.error("Error in findAllProfilesWithoutFilter: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<FlatAnnouncementDTO> findFilteredFlatAnnouncementsWithCursor(
            List<String> subPosition,
            List<String> cityName,
            List<String> projectTypeName,
            List<String> workTypeName,
            AnnouncementSortType sortType,
            CursorRequest cursorRequest) {

        QTeamMemberAnnouncement qAnnouncement = QTeamMemberAnnouncement.teamMemberAnnouncement;
        QTeam qTeam = QTeam.team;
        QTeamScale qTeamScale = QTeamScale.teamScale;
        QScale qScale = QScale.scale;
        QTeamRegion qTeamRegion = QTeamRegion.teamRegion;
        QRegion qRegion = QRegion.region;
        QAnnouncementPosition qAnnouncementPosition = QAnnouncementPosition.announcementPosition;
        QPosition qPosition = QPosition.position;
        QAnnouncementSkill qAnnouncementSkill = QAnnouncementSkill.announcementSkill;
        QSkill qSkill = QSkill.skill;
        QAnnouncementProjectType qAnnouncementProjectType =
                QAnnouncementProjectType.announcementProjectType;
        QProjectType qProjectType = QProjectType.projectType;
        QAnnouncementWorkType qAnnouncementWorkType = QAnnouncementWorkType.announcementWorkType;
        QWorkType qWorkType = QWorkType.workType;

        try {
            // 1. 커서 파싱
            Long cursorId = null;
            if (cursorRequest != null
                    && cursorRequest.hasNext()
                    && cursorRequest.cursor() != null) {
                try {
                    cursorId = Long.parseLong(cursorRequest.cursor());
                } catch (NumberFormatException e) {
                    log.warn("Invalid cursor: {}", cursorRequest.cursor());
                }
            }

            // 2. 기본 조건
            BooleanExpression condition =
                    qAnnouncement
                            .status
                            .eq(USABLE)
                            .and(qAnnouncement.isAnnouncementPublic.isTrue());

            // 커서 조건 추가
            if (cursorId != null) {
                condition = condition.and(qAnnouncement.id.lt(cursorId));
            }

            // 필터 조건 추가
            if (isNotEmpty(subPosition)) {
                condition = condition.and(qPosition.subPosition.in(subPosition));
            }
            if (isNotEmpty(cityName)) {
                condition = condition.and(qRegion.cityName.in(cityName));
            }
            if (isNotEmpty(projectTypeName)) {
                condition = condition.and(qProjectType.projectTypeName.in(projectTypeName));
            }
            if (isNotEmpty(workTypeName)) {
                condition = condition.and(qWorkType.workTypeName.in(workTypeName));
            }

            // 3. 정렬 조건 구성
            List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
            AnnouncementSortType effectiveSort =
                    (sortType != null) ? sortType : AnnouncementSortType.LATEST;

            switch (effectiveSort) {
                case LATEST:
                    orderSpecifiers.add(qAnnouncement.createdAt.desc());
                    break;
                case POPULAR:
                    orderSpecifiers.add(qAnnouncement.viewCount.desc());
                    break;
                case DEADLINE:
                    // Define common conditions first for better readability
                    String todayYearMonth =
                            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

                    // 상시모집 조건
                    BooleanExpression isPermanent = qAnnouncement.isPermanentRecruitment.eq(true);

                    // 모집 중 조건 (상시모집이 아니면서 공고가 진행 중인 경우)
                    BooleanExpression isInProgress =
                            qAnnouncement
                                    .isPermanentRecruitment
                                    .eq(false)
                                    .and(qAnnouncement.isAnnouncementInProgress.eq(true))
                                    .and(
                                            qAnnouncement
                                                    .announcementEndDate
                                                    .isNull()
                                                    .or(
                                                            qAnnouncement.announcementEndDate.gt(
                                                                    todayYearMonth)));

                    // 마감된 조건 (상시모집이 아니면서 공고가 마감된 경우)
                    BooleanExpression isClosed =
                            qAnnouncement
                                    .isPermanentRecruitment
                                    .eq(false)
                                    .and(qAnnouncement.isAnnouncementInProgress.eq(false));

                    // 1. 그룹 우선순위 정렬
                    orderSpecifiers.add(
                            new CaseBuilder()
                                    .when(isPermanent)
                                    .then(0)
                                    .when(isInProgress)
                                    .then(1)
                                    .when(isClosed)
                                    .then(2)
                                    .otherwise(3)
                                    .asc());

                    // 2. 상시 모집: 생성일 오름차순
                    orderSpecifiers.add(
                            new CaseBuilder()
                                    .when(qAnnouncement.isPermanentRecruitment.eq(true))
                                    .then(qAnnouncement.createdAt)
                                    .otherwise((LocalDateTime) null)
                                    .asc()
                                    .nullsLast());

                    // 3. 모집 중: 마감일 오름차순
                    orderSpecifiers.add(
                            new CaseBuilder()
                                    .when(isInProgress)
                                    .then(qAnnouncement.announcementEndDate)
                                    .otherwise((String) null)
                                    .asc()
                                    .nullsLast());

                    // 4. 모집 마감: 생성일 오름차순
                    orderSpecifiers.add(
                            new CaseBuilder()
                                    .when(isClosed)
                                    .then(qAnnouncement.createdAt)
                                    .otherwise((LocalDateTime) null)
                                    .asc()
                                    .nullsLast());
                    break;
                default:
                    orderSpecifiers.add(qAnnouncement.createdAt.desc());
            }

            // 4. 페이지 사이즈 계산
            int requestedSize = (cursorRequest != null) ? Math.max(1, cursorRequest.size()) : 10;
            int pageSize = (requestedSize % 6 == 0) ? requestedSize : ((requestedSize / 6) + 1) * 6;

            // 5. 쿼리 구성
            List<FlatAnnouncementDTO> results =
                    jpaQueryFactory
                            .select(
                                    Projections.fields(
                                            FlatAnnouncementDTO.class,
                                            qAnnouncement.id.as("teamMemberAnnouncementId"),
                                            qTeam.teamLogoImagePath.as("teamLogoImagePath"),
                                            qTeam.teamName.as("teamName"),
                                            qTeam.teamCode.as("teamCode"),
                                            qScale.scaleName.as("teamScaleName"),
                                            qRegion.cityName.as("cityName"),
                                            qRegion.divisionName.as("divisionName"),
                                            qAnnouncement.isAnnouncementInProgress.as(
                                                    "isAnnouncementInProgress"),
                                            qAnnouncement.announcementEndDate.as(
                                                    "announcementEndDate"),
                                            qAnnouncement.isPermanentRecruitment.as(
                                                    "isPermanentRecruitment"),
                                            qAnnouncement.announcementTitle.as("announcementTitle"),
                                            qAnnouncement.viewCount.as("viewCount"),
                                            qPosition.majorPosition.as("majorPosition"),
                                            qPosition.subPosition.as("subPosition"),
                                            qSkill.skillName.as("announcementSkillName"),
                                            qAnnouncement.createdAt.as("createdAt"),
                                            qProjectType.projectTypeName.as("projectTypeName"),
                                            qWorkType.workTypeName.as("workTypeName")))
                            .from(qAnnouncement)
                            .leftJoin(qAnnouncement.team, qTeam)
                            .leftJoin(qTeam.teamScales, qTeamScale)
                            .leftJoin(qTeamScale.scale, qScale)
                            .leftJoin(qTeam.teamRegions, qTeamRegion)
                            .leftJoin(qTeamRegion.region, qRegion)
                            .leftJoin(qAnnouncement.announcementPosition, qAnnouncementPosition)
                            .leftJoin(qAnnouncementPosition.position, qPosition)
                            .leftJoin(qAnnouncement.announcementSkills, qAnnouncementSkill)
                            .leftJoin(qAnnouncementSkill.skill, qSkill)
                            .innerJoin(
                                    qAnnouncement.announcementProjectType, qAnnouncementProjectType)
                            .innerJoin(qAnnouncementProjectType.projectType, qProjectType)
                            .innerJoin(qAnnouncement.announcementWorkType, qAnnouncementWorkType)
                            .innerJoin(qAnnouncementWorkType.workType, qWorkType)
                            .where(condition)
                            .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                            .limit(pageSize + 1)
                            .fetch();

            return results;

        } catch (Exception e) {
            log.error("Error in findFilteredFlatAnnouncementsWithCursor", e);
            return List.of();
        }
    }
}
