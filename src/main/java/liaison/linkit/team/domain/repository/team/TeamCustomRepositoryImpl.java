package liaison.linkit.team.domain.repository.team;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.global.util.QueryDslUtil;
import liaison.linkit.profile.domain.region.QRegion;
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
                        .where(qTeam.teamCode.eq(teamCode).and(qTeam.status.eq(StatusType.USABLE)))
                        .fetchOne();

        return Optional.ofNullable(team);
    }

    @Override
    public boolean existsByTeamCode(final String teamCode) {
        QTeam qTeam = QTeam.team;

        return jpaQueryFactory
                        .selectOne()
                        .from(qTeam)
                        .where(qTeam.teamCode.eq(teamCode).and(qTeam.status.eq(StatusType.USABLE)))
                        .fetchFirst()
                != null;
    }

    @Override
    public Page<Team> findAllByFiltering(
            final List<String> scaleName,
            final List<String> cityName,
            final List<String> teamStateName,
            final Pageable pageable) {
        QTeam qTeam = QTeam.team;

        JPAQuery<Long> teamIdQuery =
                jpaQueryFactory
                        .select(qTeam.id)
                        .distinct()
                        .from(qTeam)
                        .where(qTeam.status.eq(StatusType.USABLE).and(qTeam.isTeamPublic.eq(true)));

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

        if (isNotEmpty(teamStateName)) {
            QTeamCurrentState qTeamCurrentState = QTeamCurrentState.teamCurrentState;
            QTeamState qTeamState = QTeamState.teamState;

            teamIdQuery
                    .leftJoin(qTeamCurrentState)
                    .on(qTeamCurrentState.teamState.eq(qTeamState))
                    .leftJoin(qTeamState)
                    .on(qTeamCurrentState.teamState.eq(qTeamState))
                    .where(qTeamState.teamStateName.in(teamStateName));
        }

        List<Long> teamIds =
                teamIdQuery.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();

        // 2. 실제 데이터 조회 - OneToOne 관계는 fetch join 사용
        List<Team> content =
                jpaQueryFactory
                        .selectFrom(qTeam)
                        .leftJoin(qTeam.teamScales)
                        .leftJoin(qTeam.teamRegions)
                        .leftJoin(qTeam.teamCurrentStates)
                        .where(qTeam.id.in(teamIds))
                        .orderBy(
                                QueryDslUtil.getOrderTeamSpecifier(
                                        pageable.getSort(),
                                        qTeam,
                                        QTeamScale.teamScale,
                                        QTeamRegion.teamRegion,
                                        QTeamCurrentState.teamCurrentState))
                        .distinct()
                        .fetch();

        // 3. Count 쿼리
        JPAQuery<Long> countQuery =
                jpaQueryFactory
                        .select(qTeam.countDistinct())
                        .from(qTeam)
                        .where(qTeam.status.eq(StatusType.USABLE).and(qTeam.isTeamPublic.eq(true)));

        applyFiltersToCountQuery(countQuery, qTeam, scaleName, cityName, teamStateName);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
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
    public List<Team> findHomeTopTeams(final int limit) {
        QTeam qTeam = QTeam.team;

        return jpaQueryFactory
                .selectFrom(qTeam)
                .where(qTeam.isTeamPublic.eq(true), qTeam.status.eq(StatusType.USABLE))
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
                        .where(qTeam.status.eq(StatusType.USABLE).and(qTeam.isTeamPublic.eq(true)))
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
                        .where(qTeam.status.eq(StatusType.USABLE).and(qTeam.isTeamPublic.eq(true)))
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
                                        .eq(StatusType.USABLE)
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
                                                    .eq(StatusType.USABLE)
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
}
