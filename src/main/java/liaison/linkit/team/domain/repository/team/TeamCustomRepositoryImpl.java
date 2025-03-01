package liaison.linkit.team.domain.repository.team;

import static liaison.linkit.global.type.StatusType.USABLE;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.profile.domain.region.QRegion;
import liaison.linkit.search.presentation.dto.CursorRequest;
import liaison.linkit.search.presentation.dto.CursorResponse;
import liaison.linkit.team.domain.region.QTeamRegion;
import liaison.linkit.team.domain.scale.QScale;
import liaison.linkit.team.domain.scale.QTeamScale;
import liaison.linkit.team.domain.state.QTeamCurrentState;
import liaison.linkit.team.domain.state.QTeamState;
import liaison.linkit.team.domain.team.QTeam;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.team.type.TeamStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
@Slf4j
public class TeamCustomRepositoryImpl implements TeamCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext private EntityManager entityManager; // EntityManager 주입

    @Override
    public Optional<Team> findByTeamCode(final String teamCode) {
        QTeam qTeam = QTeam.team;

        Team team =
                jpaQueryFactory
                        .selectFrom(qTeam)
                        .where(qTeam.teamCode.eq(teamCode).and(qTeam.status.eq(USABLE)))
                        .fetchOne();

        return Optional.ofNullable(team);
    }

    @Override
    public boolean existsByTeamCode(final String teamCode) {
        QTeam qTeam = QTeam.team;

        return jpaQueryFactory
                        .selectOne()
                        .from(qTeam)
                        .where(qTeam.teamCode.eq(teamCode).and(qTeam.status.eq(USABLE)))
                        .fetchFirst()
                != null;
    }

    //    @Override
    //    public Page<Team> findAllByFiltering(
    //            final List<String> scaleName,
    //            final List<String> cityName,
    //            final List<String> teamStateName,
    //            final CursorRequest cursorRequest) {
    //        QTeam qTeam = QTeam.team;
    //
    //        JPAQuery<Long> teamIdQuery =
    //                jpaQueryFactory
    //                        .select(qTeam.id)
    //                        .distinct()
    //                        .from(qTeam)
    //                        .where(qTeam.status.eq(USABLE).and(qTeam.isTeamPublic.eq(true)));
    //
    //        if (isNotEmpty(scaleName)) {
    //            QTeamScale qTeamScale = QTeamScale.teamScale;
    //            QScale qScale = QScale.scale;
    //
    //            teamIdQuery
    //                    .leftJoin(qTeamScale)
    //                    .on(qTeamScale.team.eq(qTeam))
    //                    .leftJoin(qScale)
    //                    .on(qTeamScale.scale.eq(qScale))
    //                    .where(qScale.scaleName.in(scaleName));
    //        }
    //
    //        if (isNotEmpty(cityName)) {
    //            QTeamRegion qTeamRegion = QTeamRegion.teamRegion;
    //            QRegion qRegion = QRegion.region;
    //
    //            teamIdQuery
    //                    .leftJoin(qTeamRegion)
    //                    .on(qTeamRegion.team.eq(qTeam))
    //                    .leftJoin(qRegion)
    //                    .on(qTeamRegion.region.eq(qRegion))
    //                    .where(qRegion.cityName.in(cityName));
    //        }
    //
    //        if (isNotEmpty(teamStateName)) {
    //            QTeamCurrentState qTeamCurrentState = QTeamCurrentState.teamCurrentState;
    //            QTeamState qTeamState = QTeamState.teamState;
    //
    //            teamIdQuery
    //                    .leftJoin(qTeamCurrentState)
    //                    .on(qTeamCurrentState.teamState.eq(qTeamState))
    //                    .leftJoin(qTeamState)
    //                    .on(qTeamCurrentState.teamState.eq(qTeamState))
    //                    .where(qTeamState.teamStateName.in(teamStateName));
    //        }
    //
    //        List<Long> teamIds =
    //
    // teamIdQuery.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
    //
    //        // 2. 실제 데이터 조회 - OneToOne 관계는 fetch join 사용
    //        List<Team> content =
    //                jpaQueryFactory
    //                        .selectFrom(qTeam)
    //                        .leftJoin(qTeam.teamScales)
    //                        .leftJoin(qTeam.teamRegions)
    //                        .leftJoin(qTeam.teamCurrentStates)
    //                        .where(qTeam.id.in(teamIds))
    //                        .orderBy(
    //                                QueryDslUtil.getOrderTeamSpecifier(
    //                                        pageable.getSort(),
    //                                        qTeam,
    //                                        QTeamScale.teamScale,
    //                                        QTeamRegion.teamRegion,
    //                                        QTeamCurrentState.teamCurrentState))
    //                        .distinct()
    //                        .fetch();
    //
    //        // 3. Count 쿼리
    //        JPAQuery<Long> countQuery =
    //                jpaQueryFactory
    //                        .select(qTeam.countDistinct())
    //                        .from(qTeam)
    //                        .where(qTeam.status.eq(USABLE).and(qTeam.isTeamPublic.eq(true)));
    //
    //        applyFiltersToCountQuery(countQuery, qTeam, scaleName, cityName, teamStateName);
    //
    //        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    //    }

    private void applyFiltersToCountQuery(
            JPAQuery<Long> countQuery,
            QTeam qTeam,
            List<String> scaleName,
            List<String> cityName,
            List<String> teamStateName) {
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

        if (isNotEmpty(teamStateName)) {
            QTeamCurrentState qTeamCurrentState = QTeamCurrentState.teamCurrentState;
            QTeamState qTeamState = QTeamState.teamState;

            countQuery
                    .leftJoin(qTeamCurrentState)
                    .on(qTeamCurrentState.teamState.eq(qTeamState))
                    .leftJoin(qTeamState)
                    .on(qTeamCurrentState.teamState.eq(qTeamState))
                    .where(qTeamState.teamStateName.in(teamStateName));
        }
    }

    @Override
    public void deleteTeamByTeamCode(final String teamCode) {
        QTeam qTeam = QTeam.team;

        try {
            jpaQueryFactory
                    .update(qTeam)
                    .set(qTeam.status, StatusType.DELETED)
                    .where(qTeam.teamCode.eq(teamCode))
                    .execute();
        } catch (Exception e) {
            log.error("Error occurred while deleting team with teamCode: {}", teamCode, e);
            throw e; // 필요에 따라 커스텀 예외로 변환하여 던질 수 있습니다.
        }
    }

    @Override
    public List<Team> findHomeTopTeams(final int limit) {
        QTeam qTeam = QTeam.team;

        return jpaQueryFactory
                .selectFrom(qTeam)
                .where(qTeam.isTeamPublic.eq(true), qTeam.status.eq(USABLE))
                .orderBy(
                        // CASE WHEN 구문으로 지정한 순서대로 정렬
                        new CaseBuilder()
                                .when(qTeam.id.eq(44L))
                                .then(0)
                                .when(qTeam.id.eq(25L))
                                .then(1)
                                .when(qTeam.id.eq(1L))
                                .then(2)
                                .when(qTeam.id.eq(19L))
                                .then(3)
                                .otherwise(4)
                                .asc())
                .limit(limit)
                .fetch();
    }

    @Override
    public Team updateTeamStatus(final TeamStatus teamStatus, final String teamCode) {
        QTeam qTeam = QTeam.team;

        // 프로필 활동 업데이트
        long updatedCount =
                jpaQueryFactory
                        .update(qTeam)
                        .set(qTeam.teamStatus, teamStatus)
                        .where(qTeam.teamCode.eq(teamCode))
                        .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) {
            return jpaQueryFactory.selectFrom(qTeam).where(qTeam.teamCode.eq(teamCode)).fetchOne();
        } else {
            return null;
        }
    }

    @Override
    public boolean isTeamDeleteInProgress(final String teamCode) {
        QTeam qTeam = QTeam.team;

        return jpaQueryFactory
                        .selectOne()
                        .from(qTeam)
                        .where(
                                qTeam.teamCode
                                        .eq(teamCode)
                                        .and(qTeam.teamStatus.eq(TeamStatus.DELETE_PENDING)))
                        .fetchFirst()
                != null;
    }

    @Override
    public void updateTeam(final Team team) {
        QTeam qTeam = QTeam.team;

        long updatedRows =
                jpaQueryFactory
                        .update(qTeam)
                        .set(qTeam.teamName, team.getTeamName())
                        .set(qTeam.teamCode, team.getTeamCode())
                        .set(qTeam.teamShortDescription, team.getTeamShortDescription())
                        .set(qTeam.teamLogoImagePath, team.getTeamLogoImagePath())
                        .set(qTeam.isTeamPublic, team.isTeamPublic())
                        .set(qTeam.teamStatus, team.getTeamStatus())
                        .set(qTeam.status, team.getStatus())
                        .where(qTeam.id.eq(team.getId()))
                        .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedRows == 0) {
            throw new IllegalStateException("No team found with ID: " + team.getId());
        }
    }

    /**
     * 주어진 teamCode로 팀을 조회하여, 팀의 기본 상태(status)가 DELETED 인지를 반환한다. 단, 엔티티에 걸려있는 SQL
     * 제한(@SQLRestriction("status = 'USABLE'")) 를 우회하기 위해 해당 필터를 비활성화한 후 조회한다.
     *
     * @param teamCode 팀 코드
     * @return 팀이 존재하지 않거나 status가 DELETED이면 true, 그렇지 않으면 false.
     */
    @Override
    public boolean isTeamDeleted(final String teamCode) {
        QTeam qTeam = QTeam.team;

        Team team = jpaQueryFactory.selectFrom(qTeam).where(qTeam.teamCode.eq(teamCode)).fetchOne();

        // 6) 팀이 아예 없으면 => 이미 삭제되었거나 존재하지 않는 것으로 간주 => true
        if (team == null) {
            return true;
        }

        return team.getStatus() == StatusType.DELETED;
    }

    @Override
    public Page<Team> findTopVentureTeams(final Pageable pageable) {
        QTeam qTeam = QTeam.team;

        List<Team> content =
                jpaQueryFactory
                        .selectFrom(qTeam)
                        .where(qTeam.status.eq(USABLE).and(qTeam.isTeamPublic.eq(true)))
                        .orderBy(
                                new CaseBuilder()
                                        .when(qTeam.id.eq(44L))
                                        .then(0)
                                        .when(qTeam.id.eq(10L))
                                        .then(1)
                                        .when(qTeam.id.eq(36L))
                                        .then(2)
                                        .when(qTeam.id.eq(4L))
                                        .then(3)
                                        .when(qTeam.id.eq(1L))
                                        .then(4)
                                        .when(qTeam.id.eq(2L))
                                        .then(5)
                                        .otherwise(6)
                                        .asc())
                        .limit(4)
                        .fetch();

        // Pageable 정보와 함께 Page 객체로 반환 (항상 최대 6개의 레코드)
        return PageableExecutionUtils.getPage(content, pageable, content::size);
    }

    @Override
    public Page<Team> findSupportProjectTeams(final Pageable pageable) {
        QTeam qTeam = QTeam.team;

        List<Team> content =
                jpaQueryFactory
                        .selectFrom(qTeam)
                        .where(qTeam.status.eq(USABLE).and(qTeam.isTeamPublic.eq(true)))
                        .orderBy(
                                new CaseBuilder()
                                        .when(qTeam.id.eq(37L))
                                        .then(0)
                                        .when(qTeam.id.eq(29L))
                                        .then(1)
                                        .when(qTeam.id.eq(3L))
                                        .then(2)
                                        .when(qTeam.id.eq(19L))
                                        .then(3)
                                        .otherwise(4)
                                        .asc())
                        .limit(4)
                        .fetch();

        // Pageable 정보와 함께 Page 객체로 반환 (항상 최대 6개의 레코드)
        return PageableExecutionUtils.getPage(content, pageable, content::size);
    }

    @Override
    public Page<Team> findAllExcludingIds(
            final List<Long> excludeTeamIds, final Pageable pageable) {
        QTeam qTeam = QTeam.team;

        // 1. ID만 먼저 조회하는 서브쿼리
        List<Long> teamIds =
                jpaQueryFactory
                        .select(qTeam.id)
                        .from(qTeam)
                        .where(
                                qTeam.status
                                        .eq(USABLE)
                                        .and(qTeam.isTeamPublic.eq(true))
                                        .and(qTeam.id.notIn(excludeTeamIds)))
                        .orderBy(qTeam.createdAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        if (teamIds.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0L);
        }

        List<Team> content =
                jpaQueryFactory
                        .selectFrom(qTeam)
                        .where(qTeam.id.in(teamIds))
                        .orderBy(qTeam.createdAt.desc())
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
                                    .select(qTeam.count())
                                    .from(qTeam)
                                    .where(
                                            qTeam.status
                                                    .eq(USABLE)
                                                    .and(qTeam.isTeamPublic.eq(true))
                                                    .and(
                                                            excludeTeamIds.isEmpty()
                                                                    ? null
                                                                    : qTeam.id.notIn(
                                                                            excludeTeamIds)))
                                    .fetchOne();

                    return count != null ? count : 0L;
                });
    }

    private boolean isNotEmpty(List<?> list) {
        return list != null && !list.isEmpty();
    }

    // 방법 2: 개별 컬렉션 별도 로딩을 위한 추가 메서드
    public CursorResponse<Team> findAllExcludingIdsWithCursor(
            final List<Long> excludeTeamIds, final CursorRequest cursorRequest) {
        QTeam qTeam = QTeam.team;

        try {
            // 기본 쿼리 조건
            BooleanExpression baseCondition =
                    qTeam.status.eq(USABLE).and(qTeam.isTeamPublic.eq(true));

            // ID 제외 조건
            if (excludeTeamIds != null && !excludeTeamIds.isEmpty()) {
                baseCondition = baseCondition.and(qTeam.id.notIn(excludeTeamIds));
            }

            // 커서 조건
            if (cursorRequest != null
                    && cursorRequest.hasNext()
                    && cursorRequest.getCursor() != null) {
                baseCondition = baseCondition.and(qTeam.id.lt(cursorRequest.getCursor()));
            }

            // 페이지 크기 안전하게 설정
            int pageSize = (cursorRequest != null) ? Math.max(1, cursorRequest.getSize()) : 10;

            // 1. ID만 먼저 조회
            List<Long> teamIds =
                    jpaQueryFactory
                            .select(qTeam.id)
                            .from(qTeam)
                            .where(baseCondition)
                            .orderBy(qTeam.id.desc())
                            .limit(pageSize + 1)
                            .fetch();

            if (teamIds.isEmpty()) {
                return CursorResponse.of(List.of(), null);
            }

            // 다음 커서 계산
            Long nextCursor = null;
            boolean hasNext = teamIds.size() > pageSize;

            if (hasNext) {
                nextCursor = teamIds.get(teamIds.size() - 1);
                teamIds.remove(teamIds.size() - 1);
            }

            // 2. ID로 팀 엔터티만 조회 (컬렉션 없이)
            List<Team> teams =
                    jpaQueryFactory
                            .selectFrom(qTeam)
                            .where(qTeam.id.in(teamIds))
                            .orderBy(qTeam.id.desc())
                            .fetch();

            return CursorResponse.of(teams, nextCursor);
        } catch (Exception e) {
            log.error("Error in findAllExcludingIdsWithCursor: {}", e.getMessage(), e);
            return CursorResponse.of(List.of(), null);
        }
    }

    /** 필터링 조건으로 팀을 커서 기반으로 조회합니다. */
    public CursorResponse<Team> findAllByFilteringWithCursor(
            final List<String> scaleName,
            final List<String> cityName,
            final List<String> teamStateName,
            final CursorRequest cursorRequest) {

        QTeam qTeam = QTeam.team;

        try {
            // 1. 필터링된 팀 ID를 조회
            JPAQuery<Long> teamIdQuery =
                    jpaQueryFactory
                            .select(qTeam.id)
                            .distinct()
                            .from(qTeam)
                            .where(qTeam.status.eq(USABLE).and(qTeam.isTeamPublic.eq(true)));

            // 커서 조건 추가
            if (cursorRequest != null
                    && cursorRequest.hasNext()
                    && cursorRequest.getCursor() != null) {
                teamIdQuery = teamIdQuery.where(qTeam.id.lt(cursorRequest.getCursor()));
            }

            // 규모 필터링
            if (isNotEmpty(scaleName)) {
                QTeamScale qTeamScale = QTeamScale.teamScale;
                QScale qScale = QScale.scale;

                teamIdQuery
                        .leftJoin(qTeamScale)
                        .on(qTeamScale.team.eq(qTeam))
                        .leftJoin(qScale)
                        .on(qTeamScale.scale.eq(qScale))
                        .where(qScale.scaleName.in(scaleName));
            }

            // 지역 필터링
            if (isNotEmpty(cityName)) {
                QTeamRegion qTeamRegion = QTeamRegion.teamRegion;
                QRegion qRegion = QRegion.region;

                teamIdQuery
                        .leftJoin(qTeamRegion)
                        .on(qTeamRegion.team.eq(qTeam))
                        .leftJoin(qRegion)
                        .on(qTeamRegion.region.eq(qRegion))
                        .where(qRegion.cityName.in(cityName));
            }

            // 상태 필터링
            if (isNotEmpty(teamStateName)) {
                QTeamCurrentState qTeamCurrentState = QTeamCurrentState.teamCurrentState;
                QTeamState qTeamState = QTeamState.teamState;

                teamIdQuery
                        .leftJoin(qTeamCurrentState)
                        .on(qTeamCurrentState.team.eq(qTeam))
                        .leftJoin(qTeamState)
                        .on(qTeamCurrentState.teamState.eq(qTeamState))
                        .where(qTeamState.teamStateName.in(teamStateName));
            }

            // ID 내림차순 정렬 및 제한
            int pageSize = (cursorRequest != null) ? Math.max(1, cursorRequest.getSize()) : 10;
            List<Long> teamIds =
                    teamIdQuery
                            .orderBy(qTeam.id.desc())
                            .limit(pageSize + 1) // 다음 페이지 확인을 위해 +1
                            .fetch();

            // 조회할 팀이 없는 경우 빈 응답 반환
            if (teamIds.isEmpty()) {
                return CursorResponse.of(List.of(), null);
            }

            // 다음 커서 계산
            boolean hasNext = teamIds.size() > pageSize;
            Long nextCursor = null;

            // 다음 페이지가 있는 경우
            if (hasNext) {
                nextCursor = teamIds.get(teamIds.size() - 1);
                teamIds.remove(teamIds.size() - 1); // 마지막 요소 제거
            }

            // 2. 실제 데이터 조회 - fetch join을 하나만 적용하거나 제거
            // 방법 1: fetch join 없이 조회 (가장 안전한 방법)
            List<Team> content =
                    jpaQueryFactory
                            .selectFrom(qTeam)
                            .where(qTeam.id.in(teamIds))
                            .orderBy(qTeam.id.desc())
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
}
