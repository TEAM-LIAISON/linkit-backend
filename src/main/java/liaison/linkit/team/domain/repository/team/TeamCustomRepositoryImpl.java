package liaison.linkit.team.domain.repository.team;

import static liaison.linkit.global.type.StatusType.USABLE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.profile.domain.region.QRegion;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.search.presentation.dto.team.FlatTeamDTO;
import liaison.linkit.team.domain.region.QTeamRegion;
import liaison.linkit.team.domain.scale.QScale;
import liaison.linkit.team.domain.scale.QTeamScale;
import liaison.linkit.team.domain.state.QTeamCurrentState;
import liaison.linkit.team.domain.state.QTeamState;
import liaison.linkit.team.domain.team.QTeam;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.team.type.TeamStatus;
import liaison.linkit.team.presentation.team.dto.TeamDynamicResponse;
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
    public List<FlatTeamDTO> findHomeTopTeams(final int limit) {
        QTeam qTeam = QTeam.team;
        QTeamRegion qTeamRegion = QTeamRegion.teamRegion;
        QRegion qRegion = QRegion.region;
        QTeamScale qTeamScale = QTeamScale.teamScale;
        QScale qScale = QScale.scale;
        QTeamCurrentState qTeamCurrentState = QTeamCurrentState.teamCurrentState;
        QTeamState qTeamState = QTeamState.teamState;

        List<Long> targetTeamIds =
                jpaQueryFactory
                        .select(qTeam.id)
                        .from(qTeam)
                        .where(
                                qTeam.status
                                        .eq(USABLE)
                                        .and(qTeam.isTeamPublic.eq(true))
                                        .and(qTeam.id.in(69L, 67L, 63L, 66L)))
                        .orderBy(
                                new CaseBuilder()
                                        .when(qTeam.id.eq(69L))
                                        .then(0)
                                        .when(qTeam.id.eq(67L))
                                        .then(1)
                                        .when(qTeam.id.eq(63L))
                                        .then(2)
                                        .when(qTeam.id.eq(66L))
                                        .then(3)
                                        .otherwise(4)
                                        .asc())
                        .limit(limit)
                        .fetch();

        return jpaQueryFactory
                .select(
                        Projections.fields(
                                FlatTeamDTO.class,
                                qTeam.id.as("teamId"),
                                qTeam.teamName.as("teamName"),
                                qTeam.teamCode.as("teamCode"),
                                qTeam.teamShortDescription.as("teamShortDescription"),
                                qTeam.teamLogoImagePath.as("teamLogoImagePath"),
                                qScale.scaleName.as("teamScaleName"),
                                qRegion.cityName.as("cityName"),
                                qRegion.divisionName.as("divisionName"),
                                qTeamState.teamStateName.as("teamCurrentStateName")))
                .from(qTeam)
                .leftJoin(qTeam.teamRegions, qTeamRegion)
                .leftJoin(qTeamRegion.region, qRegion)
                .leftJoin(qTeam.teamScales, qTeamScale)
                .leftJoin(qTeamScale.scale, qScale)
                .leftJoin(qTeam.teamCurrentStates, qTeamCurrentState)
                .leftJoin(qTeamCurrentState.teamState, qTeamState)
                .where(qTeam.id.in(targetTeamIds))
                .orderBy(
                        new CaseBuilder()
                                .when(qTeam.id.eq(69L))
                                .then(0)
                                .when(qTeam.id.eq(67L))
                                .then(1)
                                .when(qTeam.id.eq(63L))
                                .then(2)
                                .when(qTeam.id.eq(66L))
                                .then(3)
                                .otherwise(4)
                                .asc())
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

            // 커서 조건 - teamCode로 ID를 찾아서 적용
            if (cursorRequest != null
                    && cursorRequest.hasNext()
                    && cursorRequest.cursor() != null) {
                // teamCode로 해당 팀의 ID를 조회
                String cursorTeamCode = cursorRequest.cursor();
                Long cursorTeamId =
                        jpaQueryFactory
                                .select(qTeam.id)
                                .from(qTeam)
                                .where(qTeam.teamCode.eq(cursorTeamCode))
                                .fetchOne();

                // 찾은 ID가 있으면 해당 ID보다 작은 ID를 가진 팀만 조회
                if (cursorTeamId != null) {
                    baseCondition = baseCondition.and(qTeam.id.lt(cursorTeamId));
                }
            }

            // 페이지 크기 안전하게 설정
            int requestedSize = (cursorRequest != null) ? Math.max(1, cursorRequest.size()) : 10;
            int pageSize = (requestedSize % 2 == 0) ? requestedSize : (requestedSize / 2 + 1) * 2;

            // 1. ID와 teamCode를 함께 조회
            List<Tuple> teamTuples =
                    jpaQueryFactory
                            .select(qTeam.id, qTeam.teamCode)
                            .from(qTeam)
                            .where(baseCondition)
                            .orderBy(qTeam.id.desc()) // ID 기준으로 정렬
                            .limit(pageSize + 1)
                            .fetch();

            if (teamTuples.isEmpty()) {
                return CursorResponse.of(List.of(), null);
            }

            // teamIds 추출
            List<Long> teamIds = new ArrayList<>();
            for (int i = 0;
                    i < (teamTuples.size() > pageSize ? pageSize : teamTuples.size());
                    i++) {
                teamIds.add(teamTuples.get(i).get(qTeam.id));
            }

            // 다음 커서 계산
            String nextCursor = null;
            boolean hasNext = teamTuples.size() > pageSize;

            if (hasNext) {
                // 다음 페이지의 첫 번째 팀의 teamCode를 다음 커서로 설정
                nextCursor = teamTuples.get(pageSize).get(qTeam.teamCode);
            }

            // 2. ID로 팀 엔터티 조회하고 생성 시간 기준으로 정렬
            List<Team> teams =
                    jpaQueryFactory
                            .selectFrom(qTeam)
                            .where(qTeam.id.in(teamIds))
                            .orderBy(qTeam.createdAt.desc()) // 생성 시간 기준으로 정렬
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
            // teamCode로 ID를 찾는 로직
            Long cursorTeamId = null;
            if (cursorRequest != null
                    && cursorRequest.hasNext()
                    && cursorRequest.cursor() != null) {
                String cursorTeamCode = cursorRequest.cursor();
                cursorTeamId =
                        jpaQueryFactory
                                .select(qTeam.id)
                                .from(qTeam)
                                .where(qTeam.teamCode.eq(cursorTeamCode))
                                .fetchOne();
            }

            // 1. 필터링된 팀 ID와 teamCode를 함께 조회
            JPAQuery<Tuple> teamQuery =
                    jpaQueryFactory
                            .select(qTeam.id, qTeam.teamCode)
                            .distinct()
                            .from(qTeam)
                            .where(qTeam.status.eq(USABLE).and(qTeam.isTeamPublic.eq(true)));

            // 커서 조건 추가 - 팀 ID 기준
            if (cursorTeamId != null) {
                teamQuery = teamQuery.where(qTeam.id.lt(cursorTeamId));
            }
            // 규모 필터링
            if (isNotEmpty(scaleName)) {
                QTeamScale qTeamScale = QTeamScale.teamScale;
                QScale qScale = QScale.scale;

                teamQuery
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

                teamQuery
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

                teamQuery
                        .leftJoin(qTeamCurrentState)
                        .on(qTeamCurrentState.team.eq(qTeam))
                        .leftJoin(qTeamState)
                        .on(qTeamCurrentState.teamState.eq(qTeamState))
                        .where(qTeamState.teamStateName.in(teamStateName));
            }

            // ID 기준으로 정렬 및 제한
            int requestedSize = (cursorRequest != null) ? Math.max(1, cursorRequest.size()) : 10;
            int pageSize = (requestedSize % 6 == 0) ? requestedSize : (requestedSize / 6 + 1) * 6;

            List<Tuple> teamTuples =
                    teamQuery
                            .orderBy(qTeam.id.desc()) // ID 기준 정렬
                            .limit(pageSize + 1) // 다음 페이지 확인을 위해 +1
                            .fetch();

            // 조회할 팀이 없는 경우 빈 응답 반환
            if (teamTuples.isEmpty()) {
                return CursorResponse.of(List.of(), null);
            }

            // teamIds 추출
            List<Long> teamIds = new ArrayList<>();
            for (int i = 0;
                    i < (teamTuples.size() > pageSize ? pageSize : teamTuples.size());
                    i++) {
                teamIds.add(teamTuples.get(i).get(qTeam.id));
            }

            // 다음 커서 계산
            boolean hasNext = teamTuples.size() > pageSize;
            String nextCursor = null;

            // 다음 페이지가 있는 경우
            if (hasNext) {
                // 마지막 엔티티의 teamCode를 다음 커서로 사용
                nextCursor = teamTuples.get(pageSize).get(qTeam.teamCode);
            }

            // 2. 실제 데이터 조회 - 생성 시간 기준으로 정렬
            List<Team> content =
                    jpaQueryFactory
                            .selectFrom(qTeam)
                            .where(qTeam.id.in(teamIds))
                            .orderBy(qTeam.createdAt.desc()) // 생성 시간 기준으로 정렬
                            .fetch();

            return CursorResponse.of(content, nextCursor);
        } catch (Exception e) {
            log.error("Error in findAllByFilteringWithCursor: {}", e.getMessage(), e);
            return CursorResponse.of(List.of(), null);
        }
    }

    @Override
    public List<TeamDynamicResponse> findAllDynamicVariablesWithTeam() {
        QTeam qTeam = QTeam.team;

        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                TeamDynamicResponse.class,
                                qTeam.teamName,
                                qTeam.teamCode,
                                qTeam.createdAt))
                .from(qTeam)
                .where(qTeam.status.eq(USABLE).and(qTeam.isTeamPublic.eq(true)))
                .orderBy(qTeam.id.desc())
                .fetch();
    }

    @Override
    public List<FlatTeamDTO> findTopVentureTeams(int limit) {
        QTeam qTeam = QTeam.team;
        QTeamRegion qTeamRegion = QTeamRegion.teamRegion;
        QRegion qRegion = QRegion.region;
        QTeamScale qTeamScale = QTeamScale.teamScale;
        QScale qScale = QScale.scale;
        QTeamCurrentState qTeamCurrentState = QTeamCurrentState.teamCurrentState;
        QTeamState qTeamState = QTeamState.teamState;

        List<Long> targetTeamIds =
                jpaQueryFactory
                        .select(qTeam.id)
                        .from(qTeam)
                        .where(
                                qTeam.status
                                        .eq(USABLE)
                                        .and(qTeam.isTeamPublic.eq(true))
                                        .and(qTeam.id.in(69L, 67L, 63L, 66L)))
                        .orderBy(
                                new CaseBuilder()
                                        .when(qTeam.id.eq(69L))
                                        .then(0)
                                        .when(qTeam.id.eq(67L))
                                        .then(1)
                                        .when(qTeam.id.eq(63L))
                                        .then(2)
                                        .when(qTeam.id.eq(66L))
                                        .then(3)
                                        .otherwise(4)
                                        .asc())
                        .limit(limit)
                        .fetch();

        return jpaQueryFactory
                .select(
                        Projections.fields(
                                FlatTeamDTO.class,
                                qTeam.id.as("teamId"),
                                qTeam.teamName.as("teamName"),
                                qTeam.teamCode.as("teamCode"),
                                qTeam.teamShortDescription.as("teamShortDescription"),
                                qTeam.teamLogoImagePath.as("teamLogoImagePath"),
                                qScale.scaleName.as("teamScaleName"),
                                qRegion.cityName.as("cityName"),
                                qRegion.divisionName.as("divisionName"),
                                qTeamState.teamStateName.as("teamCurrentStateName")))
                .from(qTeam)
                .leftJoin(qTeam.teamRegions, qTeamRegion)
                .leftJoin(qTeamRegion.region, qRegion)
                .leftJoin(qTeam.teamScales, qTeamScale)
                .leftJoin(qTeamScale.scale, qScale)
                .leftJoin(qTeam.teamCurrentStates, qTeamCurrentState)
                .leftJoin(qTeamCurrentState.teamState, qTeamState)
                .where(qTeam.id.in(targetTeamIds))
                .orderBy(
                        new CaseBuilder()
                                .when(qTeam.id.eq(69L))
                                .then(0)
                                .when(qTeam.id.eq(67L))
                                .then(1)
                                .when(qTeam.id.eq(63L))
                                .then(2)
                                .when(qTeam.id.eq(66L))
                                .then(3)
                                .otherwise(4)
                                .asc())
                .fetch();
    }

    @Override
    public List<FlatTeamDTO> findTopSupportTeams(int limit) {
        QTeam qTeam = QTeam.team;
        QTeamRegion qTeamRegion = QTeamRegion.teamRegion;
        QRegion qRegion = QRegion.region;
        QTeamScale qTeamScale = QTeamScale.teamScale;
        QScale qScale = QScale.scale;
        QTeamCurrentState qTeamCurrentState = QTeamCurrentState.teamCurrentState;
        QTeamState qTeamState = QTeamState.teamState;

        List<Long> targetTeamIds =
                jpaQueryFactory
                        .select(qTeam.id)
                        .from(qTeam)
                        .where(
                                qTeam.status
                                        .eq(USABLE)
                                        .and(qTeam.isTeamPublic.eq(true))
                                        .and(qTeam.id.in(37L, 29L, 3L, 19L)))
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
                        .limit(limit)
                        .fetch();

        return jpaQueryFactory
                .select(
                        Projections.fields(
                                FlatTeamDTO.class,
                                qTeam.id.as("teamId"),
                                qTeam.teamName.as("teamName"),
                                qTeam.teamCode.as("teamCode"),
                                qTeam.teamShortDescription.as("teamShortDescription"),
                                qTeam.teamLogoImagePath.as("teamLogoImagePath"),
                                qScale.scaleName.as("teamScaleName"),
                                qRegion.cityName.as("cityName"),
                                qRegion.divisionName.as("divisionName"),
                                qTeamState.teamStateName.as("teamCurrentStateName")))
                .from(qTeam)
                .leftJoin(qTeam.teamRegions, qTeamRegion)
                .leftJoin(qTeamRegion.region, qRegion)
                .leftJoin(qTeam.teamScales, qTeamScale)
                .leftJoin(qTeamScale.scale, qScale)
                .leftJoin(qTeam.teamCurrentStates, qTeamCurrentState)
                .leftJoin(qTeamCurrentState.teamState, qTeamState)
                .where(qTeam.id.in(targetTeamIds))
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
                .fetch();
    }

    public List<FlatTeamDTO> findFlatTeamsWithoutCursor(List<Long> excludeTeamIds, int size) {
        QTeam qTeam = QTeam.team;
        QTeamRegion qTeamRegion = QTeamRegion.teamRegion;
        QRegion qRegion = QRegion.region;
        QTeamScale qTeamScale = QTeamScale.teamScale;
        QScale qScale = QScale.scale;
        QTeamCurrentState qTeamCurrentState = QTeamCurrentState.teamCurrentState;
        QTeamState qTeamState = QTeamState.teamState;

        return jpaQueryFactory
                .select(
                        Projections.fields(
                                FlatTeamDTO.class,
                                qTeam.id.as("teamId"),
                                qTeam.teamName.as("teamName"),
                                qTeam.teamCode.as("teamCode"),
                                qTeam.teamShortDescription.as("teamShortDescription"),
                                qTeam.teamLogoImagePath.as("teamLogoImagePath"),
                                qScale.scaleName.as("teamScaleName"),
                                qRegion.cityName.as("cityName"),
                                qRegion.divisionName.as("divisionName"),
                                qTeamState.teamStateName.as("teamCurrentStateName")))
                .from(qTeam)
                .leftJoin(qTeam.teamRegions, qTeamRegion)
                .leftJoin(qTeamRegion.region, qRegion)
                .leftJoin(qTeam.teamScales, qTeamScale)
                .leftJoin(qTeamScale.scale, qScale)
                .leftJoin(qTeam.teamCurrentStates, qTeamCurrentState)
                .leftJoin(qTeamCurrentState.teamState, qTeamState)
                .where(
                        qTeam.status
                                .eq(StatusType.USABLE)
                                .and(qTeam.isTeamPublic.eq(true))
                                .and(qTeam.id.notIn(excludeTeamIds)))
                .orderBy(qTeam.id.desc())
                .limit(size * 5)
                .fetch();
    }

    public List<FlatTeamDTO> findAllTeamsWithoutFilter(
            final List<Long> excludeTeamIds, final CursorRequest cursorRequest) {
        QTeam qTeam = QTeam.team;
        QTeamRegion qTeamRegion = QTeamRegion.teamRegion;
        QRegion qRegion = QRegion.region;
        QTeamScale qTeamScale = QTeamScale.teamScale;
        QScale qScale = QScale.scale;
        QTeamCurrentState qTeamCurrentState = QTeamCurrentState.teamCurrentState;
        QTeamState qTeamState = QTeamState.teamState;

        try {
            // Step 1. 커서 teamCode → teamId
            Long cursorTeamId = null;
            if (cursorRequest != null
                    && cursorRequest.hasNext()
                    && cursorRequest.cursor() != null) {
                String teamCode = cursorRequest.cursor();
                cursorTeamId =
                        jpaQueryFactory
                                .select(qTeam.id)
                                .from(qTeam)
                                .where(qTeam.teamCode.eq(teamCode))
                                .fetchOne();
            }

            // Step 2. 조건 설정
            BooleanExpression condition =
                    qTeam.status.eq(StatusType.USABLE).and(qTeam.isTeamPublic.isTrue());

            if (excludeTeamIds != null && !excludeTeamIds.isEmpty()) {
                condition = condition.and(qTeam.id.notIn(excludeTeamIds));
            }

            if (cursorTeamId != null) {
                condition = condition.and(qTeam.id.lt(cursorTeamId));
            }

            int requestedSize = Math.max(1, cursorRequest.size());
            int pageSize = (requestedSize % 6 == 0) ? requestedSize : (requestedSize / 6 + 1) * 6;

            // Step 3. 조회
            return jpaQueryFactory
                    .select(
                            Projections.fields(
                                    FlatTeamDTO.class,
                                    qTeam.id.as("teamId"),
                                    qTeam.teamName.as("teamName"),
                                    qTeam.teamCode.as("teamCode"),
                                    qTeam.teamShortDescription.as("teamShortDescription"),
                                    qTeam.teamLogoImagePath.as("teamLogoImagePath"),
                                    qScale.scaleName.as("teamScaleName"),
                                    qRegion.cityName.as("cityName"),
                                    qRegion.divisionName.as("divisionName"),
                                    qTeamState.teamStateName.as("teamCurrentStateName")))
                    .from(qTeam)
                    .leftJoin(qTeam.teamRegions, qTeamRegion)
                    .leftJoin(qTeamRegion.region, qRegion)
                    .leftJoin(qTeam.teamScales, qTeamScale)
                    .leftJoin(qTeamScale.scale, qScale)
                    .leftJoin(qTeam.teamCurrentStates, qTeamCurrentState)
                    .leftJoin(qTeamCurrentState.teamState, qTeamState)
                    .where(condition)
                    .orderBy(qTeam.id.desc())
                    .limit(pageSize)
                    .fetch();

        } catch (Exception e) {
            log.error("Error in findAllTeamsWithoutFilter: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public List<FlatTeamDTO> findFilteredFlatTeamsWithCursor(
            List<String> scaleName,
            List<String> cityName,
            List<String> teamStateName,
            CursorRequest cursorRequest) {
        QTeam qTeam = QTeam.team;
        QTeamRegion qTeamRegion = QTeamRegion.teamRegion;
        QRegion qRegion = QRegion.region;
        QTeamScale qTeamScale = QTeamScale.teamScale;
        QScale qScale = QScale.scale;
        QTeamCurrentState qTeamCurrentState = QTeamCurrentState.teamCurrentState;
        QTeamState qTeamState = QTeamState.teamState;

        try {
            // Step 1. 커서 teamCode → teamId
            Long cursorTeamId = null;
            if (cursorRequest != null
                    && cursorRequest.hasNext()
                    && cursorRequest.cursor() != null) {
                String teamCode = cursorRequest.cursor();
                cursorTeamId =
                        jpaQueryFactory
                                .select(qTeam.id)
                                .from(qTeam)
                                .where(qTeam.teamCode.eq(teamCode))
                                .fetchOne();
            }

            // Step 2. 조건 설정
            BooleanExpression condition =
                    qTeam.status.eq(StatusType.USABLE).and(qTeam.isTeamPublic.isTrue());

            if (cursorTeamId != null) {
                condition = condition.and(qTeam.id.lt(cursorTeamId));
            }

            int requestedSize = Math.max(1, cursorRequest.size());
            int pageSize = (requestedSize % 6 == 0) ? requestedSize : ((requestedSize / 6) + 1) * 6;

            // Step 3. 쿼리 구성
            var query =
                    jpaQueryFactory
                            .select(
                                    Projections.fields(
                                            FlatTeamDTO.class,
                                            qTeam.id.as("teamId"),
                                            qTeam.teamName.as("teamName"),
                                            qTeam.teamCode.as("teamCode"),
                                            qTeam.teamShortDescription.as("teamShortDescription"),
                                            qTeam.teamLogoImagePath.as("teamLogoImagePath"),
                                            qScale.scaleName.as("teamScaleName"),
                                            qRegion.cityName.as("cityName"),
                                            qRegion.divisionName.as("divisionName"),
                                            qTeamState.teamStateName.as("teamCurrentStateName")))
                            .from(qTeam)
                            .leftJoin(qTeam.teamRegions, qTeamRegion)
                            .leftJoin(qTeamRegion.region, qRegion)
                            .leftJoin(qTeam.teamScales, qTeamScale)
                            .leftJoin(qTeamScale.scale, qScale)
                            .leftJoin(qTeam.teamCurrentStates, qTeamCurrentState)
                            .leftJoin(qTeamCurrentState.teamState, qTeamState)
                            .where(condition)
                            .orderBy(qTeam.id.desc());

            // Step 4. 필터 적용
            if (isNotEmpty(scaleName)) {
                query = query.where(qScale.scaleName.in(scaleName));
            }

            if (isNotEmpty(cityName)) {
                query = query.where(qRegion.cityName.in(cityName));
            }

            if (isNotEmpty(teamStateName)) {
                query = query.where(qTeamState.teamStateName.in(teamStateName));
            }

            // Step 5. 페이징 처리
            List<FlatTeamDTO> content = query.limit(pageSize + 1).fetch();

            // 필요시 커서 처리 정보는 추후 CursorResponse<FlatProfileDTO>로 확장 가능
            return content.size() > pageSize ? content.subList(0, pageSize) : content;

        } catch (Exception e) {
            log.error("Error in findFilteredFlatTeamsWithCursor: {}", e.getMessage(), e);
            return List.of();
        }
    }
}
