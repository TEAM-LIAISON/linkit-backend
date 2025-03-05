package liaison.linkit.team.domain.repository.announcement;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.common.domain.QPosition;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.global.util.QueryDslUtil;
import liaison.linkit.profile.domain.region.QRegion;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.team.domain.announcement.QAnnouncementPosition;
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
                                qTeamMemberAnnouncement.isRegionFlexible,
                                request.getIsRegionFlexible())
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
        log.info("subPosition: {}, cityName: {}, scaleName: {}", subPosition, cityName, scaleName);

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
                                new CaseBuilder()
                                        .when(qTeamMemberAnnouncement.id.eq(51L))
                                        .then(0)
                                        .when(qTeamMemberAnnouncement.id.eq(35L))
                                        .then(1)
                                        .when(qTeamMemberAnnouncement.id.eq(27L))
                                        .then(2)
                                        .when(qTeamMemberAnnouncement.id.eq(37L))
                                        .then(3)
                                        .when(qTeamMemberAnnouncement.id.eq(4L))
                                        .then(4)
                                        .when(qTeamMemberAnnouncement.id.eq(50L))
                                        .then(5)
                                        .when(qTeamMemberAnnouncement.id.eq(46L))
                                        .then(6)
                                        .when(qTeamMemberAnnouncement.id.eq(39L))
                                        .then(7)
                                        .when(qTeamMemberAnnouncement.id.eq(48L))
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
                    && cursorRequest.getCursor() != null) {
                baseCondition =
                        baseCondition.and(
                                qTeamMemberAnnouncement.id.lt(
                                        Long.valueOf(cursorRequest.getCursor())));
            }

            // 페이지 크기 안전하게 설정
            int requestedSize = (cursorRequest != null) ? Math.max(1, cursorRequest.getSize()) : 10;
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
            final List<String> scaleName,
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
                    && cursorRequest.getCursor() != null) {
                announcementIdQuery =
                        announcementIdQuery.where(
                                qTeamMemberAnnouncement.id.lt(
                                        Long.valueOf(cursorRequest.getCursor())));
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

            // scaleName 필터링
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

            // ID 내림차순 정렬 및 제한
            int requestedSize = (cursorRequest != null) ? Math.max(1, cursorRequest.getSize()) : 10;
            int pageSize = (requestedSize % 6 == 0) ? requestedSize : (requestedSize / 6 + 1) * 6;

            List<Long> announcementIds =
                    announcementIdQuery
                            .orderBy(qTeamMemberAnnouncement.createdAt.desc())
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
                            // 필요한 연관관계를 모두 fetch join
                            .leftJoin(qTeamMemberAnnouncement.team, QTeam.team)
                            .fetchJoin()
                            .leftJoin(qTeamMemberAnnouncement.announcementPosition)
                            .fetchJoin()
                            // 예) team -> teamRegions -> region, team -> teamScale -> scale 등
                            //    팀에 필요한 다른 연관관계가 LAZY라면 추가로 fetch join
                            .where(qTeamMemberAnnouncement.id.in(announcementIds))
                            .orderBy(qTeamMemberAnnouncement.createdAt.desc())
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
                                .when(qTeamMemberAnnouncement.id.eq(51L))
                                .then(0)
                                .when(qTeamMemberAnnouncement.id.eq(35L))
                                .then(1)
                                .when(qTeamMemberAnnouncement.id.eq(27L))
                                .then(2)
                                .when(qTeamMemberAnnouncement.id.eq(37L))
                                .then(3)
                                .when(qTeamMemberAnnouncement.id.eq(4L))
                                .then(4)
                                .when(qTeamMemberAnnouncement.id.eq(50L))
                                .then(5)
                                .when(qTeamMemberAnnouncement.id.eq(46L))
                                .then(6)
                                .when(qTeamMemberAnnouncement.id.eq(39L))
                                .then(7)
                                .when(qTeamMemberAnnouncement.id.eq(48L))
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
            log.info("No team IDs provided for fetching deletable announcements.");
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
            log.info("No team ID provided for fetching deletable announcements.");
            return Collections.emptySet();
        }

        return new HashSet<>(
                jpaQueryFactory
                        .selectFrom(qTeamMemberAnnouncement)
                        .where(
                                qTeamMemberAnnouncement.team.id.eq(teamId) // 특정 팀 ID와 일치
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

            log.info("Deleted {} announcements with IDs: {}", updatedCount, announcementIds);

            entityManager.flush();
            entityManager.clear();

        } catch (Exception e) {
            log.error(
                    "Error occurred while deleting announcements with IDs: {}", announcementIds, e);
        }
    }

    @Override
    public List<TeamMemberAnnouncement> findAllAnnouncementsByTeamId(final Long teamId) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement =
                QTeamMemberAnnouncement.teamMemberAnnouncement;

        return jpaQueryFactory
                .selectFrom(qTeamMemberAnnouncement)
                .where(qTeamMemberAnnouncement.team.id.eq(teamId))
                .fetch();
    }

    @Override
    public List<TeamMemberAnnouncement> findPublicAnnouncementsByTeamId(final Long teamId) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement =
                QTeamMemberAnnouncement.teamMemberAnnouncement;

        return jpaQueryFactory
                .selectFrom(qTeamMemberAnnouncement)
                .where(
                        qTeamMemberAnnouncement
                                .team
                                .id
                                .eq(teamId)
                                .and(qTeamMemberAnnouncement.isAnnouncementPublic.eq(true)))
                .fetch();
    }

    private boolean isNotEmpty(List<?> list) {
        return list != null && !list.isEmpty();
    }
}
