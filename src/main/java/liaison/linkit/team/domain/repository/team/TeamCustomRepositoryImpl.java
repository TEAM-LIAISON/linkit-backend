package liaison.linkit.team.domain.repository.team;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.global.util.QueryDslUtil;
import liaison.linkit.profile.domain.region.QRegion;
import liaison.linkit.team.domain.announcement.QTeamMemberAnnouncement;
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
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
@Slf4j
public class TeamCustomRepositoryImpl implements TeamCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext
    private EntityManager entityManager; // EntityManager 주입

    @Override
    public Optional<Team> findByTeamCode(final String teamCode) {
        QTeam qTeam = QTeam.team;

        Team team = jpaQueryFactory
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
                .where(qTeam.teamCode.eq(teamCode)
                        .and(qTeam.status.eq(StatusType.USABLE)))
                .fetchFirst() != null;
    }

    @Override
    public Page<Team> findAllByFiltering(
            final List<String> scaleName,
            final Boolean isAnnouncement,
            final List<String> cityName,
            final List<String> teamStateName,
            final Pageable pageable
    ) {
        QTeam qTeam = QTeam.team;

        QTeamRegion qTeamRegion = QTeamRegion.teamRegion;
        QRegion qRegion = QRegion.region;

        QTeamCurrentState qTeamCurrentState = QTeamCurrentState.teamCurrentState;
        QTeamState qTeamState = QTeamState.teamState;

        QTeamScale qTeamScale = QTeamScale.teamScale;
        QScale qScale = QScale.scale;

        QTeamMemberAnnouncement qTeamMemberAnnouncement = QTeamMemberAnnouncement.teamMemberAnnouncement;

        try {
            List<Team> content = jpaQueryFactory
                    .selectDistinct(qTeam)
                    .from(qTeam)

                    .leftJoin(qTeamRegion).on(qTeamRegion.team.eq(qTeam))
                    .leftJoin(qTeamRegion.region, qRegion)

                    .leftJoin(qTeamCurrentState).on(qTeamCurrentState.team.eq(qTeam))
                    .leftJoin(qTeamCurrentState.teamState, qTeamState)

                    .leftJoin(qTeamScale).on(qTeamScale.team.eq(qTeam))
                    .leftJoin(qTeamScale.scale, qScale)

                    .leftJoin(qTeamMemberAnnouncement).on(qTeamMemberAnnouncement.team.eq(qTeam))

                    .where(
                            qTeam.status.eq(StatusType.USABLE),
                            qTeam.isTeamPublic.eq(true),
                            hasScaleNames(scaleName),
                            hasCityName(cityName),
                            hasTeamStateNames(teamStateName)
                    )
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .orderBy(QueryDslUtil.getOrderTeamSpecifier(
                            pageable.getSort(),
                            qTeam,
                            qTeamScale,
                            qTeamRegion,
                            qTeamCurrentState
                    ))
                    .fetch();

            // 카운트 쿼리
            Long totalLong = jpaQueryFactory
                    .select(qTeam.countDistinct())
                    .from(qTeam)

                    .leftJoin(qTeamRegion).on(qTeamRegion.team.eq(qTeam))
                    .leftJoin(qTeamRegion.region, qRegion)

                    .leftJoin(qTeamCurrentState).on(qTeamCurrentState.team.eq(qTeam))
                    .leftJoin(qTeamCurrentState.teamState, qTeamState)

                    .leftJoin(qTeamScale).on(qTeamScale.team.eq(qTeam))
                    .leftJoin(qTeamScale.scale, qScale)

                    .leftJoin(qTeamMemberAnnouncement).on(qTeamMemberAnnouncement.team.eq(qTeam))
                    .where(
                            qTeam.status.eq(StatusType.USABLE),
                            qTeam.isTeamPublic.eq(true),
                            hasScaleNames(scaleName),
                            hasCityName(cityName),
                            hasTeamStateNames(teamStateName)
                    )
                    .fetchOne();

            long total = (totalLong == null) ? 0L : totalLong;

            return PageableExecutionUtils.getPage(content, pageable, () -> total);
        } catch (Exception e) {
            // 예외 발생 시 에러 로그
            log.error("Error executing findAllTeamFiltering method", e);
            throw e; // 예외를 다시 던져 상위 계층에서 처리하도록 합니다.
        }
    }

    private BooleanExpression hasScaleNames(final List<String> scaleName) {
        if (scaleName == null || scaleName.isEmpty()) {
            return null;
        }
        QScale qScale = QScale.scale;

        return qScale.scaleName.in(scaleName);
    }

    private BooleanExpression hasCityName(List<String> cityName) {
        if (cityName == null || cityName.isEmpty()) {
            return null;
        }
        QRegion qRegion = QRegion.region;

        return qRegion.cityName.in(cityName);
    }

    private BooleanExpression hasTeamStateNames(List<String> teamStateName) {
        if (teamStateName == null || teamStateName.isEmpty()) {
            return null;
        }
        QTeamState qTeamState = QTeamState.teamState;

        return qTeamState.teamStateName.in(teamStateName);
    }

    @Override
    public void deleteTeamByTeamCode(final String teamCode) {
        QTeam qTeam = QTeam.team;

        try {
            long updatedRows = jpaQueryFactory
                    .update(qTeam)
                    .set(qTeam.status, StatusType.DELETED)
                    .where(qTeam.teamCode.eq(teamCode))
                    .execute();
        } catch (Exception e) {
            log.error("Error occurred while deleting team with teamCode: {}", teamCode, e);
            throw e; // 필요에 따라 커스텀 예외로 변환하여 던질 수 있습니다.
        }
    }

//    @Override
//    public List<Team> findTopTeams(final int limit) {
//        QTeam qTeam = QTeam.team;
//
//        return jpaQueryFactory
//                .selectFrom(qTeam)
//                .where(
//                        qTeam.isTeamPublic.eq(true),
//                        qTeam.status.eq(StatusType.USABLE)
//                )
//                .orderBy(qTeam.createdAt.desc()) // 최신순으로 정렬
//                .limit(limit)
//                .fetch();
//    }

    @Override
    public List<Team> findTopTeams(final int limit) {
        QTeam qTeam = QTeam.team;

        return jpaQueryFactory
                .selectFrom(qTeam)
                .where(
                        qTeam.isTeamPublic.eq(true),
                        qTeam.status.eq(StatusType.USABLE)
                )
                .orderBy(
                        // CASE WHEN 구문으로 지정한 순서대로 정렬
                        new CaseBuilder()
                                .when(qTeam.id.eq(10L)).then(0)
                                .when(qTeam.id.eq(4L)).then(1)
                                .when(qTeam.id.eq(2L)).then(2)
                                .when(qTeam.id.eq(15L)).then(3)
                                .when(qTeam.id.eq(19L)).then(4)
                                .when(qTeam.id.eq(21L)).then(5)
                                .otherwise(6)
                                .asc()
                )
                .limit(limit)
                .fetch();
    }

    @Override
    public Team updateTeamStatus(final TeamStatus teamStatus, final String teamCode) {
        QTeam qTeam = QTeam.team;

        // 프로필 활동 업데이트
        long updatedCount = jpaQueryFactory
                .update(qTeam)
                .set(qTeam.teamStatus, teamStatus)
                .where(qTeam.teamCode.eq(teamCode))
                .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) {
            return jpaQueryFactory
                    .selectFrom(qTeam)
                    .where(qTeam.teamCode.eq(teamCode))
                    .fetchOne();
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
                .where(qTeam.teamCode.eq(teamCode)
                        .and(qTeam.teamStatus.eq(TeamStatus.DELETE_PENDING)))
                .fetchFirst() != null;
    }

    @Override
    public void updateTeam(final Team team) {
        QTeam qTeam = QTeam.team;

        long updatedRows = jpaQueryFactory.update(qTeam)
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
     * 주어진 teamCode로 팀을 조회하여, 팀의 기본 상태(status)가 DELETED 인지를 반환한다. 단, 엔티티에 걸려있는 SQL 제한(@SQLRestriction("status = 'USABLE'")) 를 우회하기 위해 해당 필터를 비활성화한 후 조회한다.
     *
     * @param teamCode 팀 코드
     * @return 팀이 존재하지 않거나 status가 DELETED이면 true, 그렇지 않으면 false.
     */
    @Override
    public boolean isTeamDeleted(final String teamCode) {
        // Hibernate 세션을 얻어 SQL 제한 필터를 비활성화
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.getEnabledFilter("hibernateFilter"); // 만약 필터 이름이 있다면 사용 (필요에 따라)
        // 또는 @SQLRestriction으로 인해 적용된 글로벌 제한은 Hibernate의 "where" 절에 자동 포함되므로,
        // 이를 우회하기 위해 native query 또는 Hibernate의 세션 API를 활용할 수 있다.
        // 여기서는 native query 예시로 구현하겠습니다.

        String sql = "SELECT t.* FROM team t WHERE t.team_code = :teamCode";
        // SQL 제한은 엔티티 매핑 시 @SQLRestriction에 의해 자동 추가되지만,
        // native query는 그 제한을 우회할 수 있습니다.
        List<Team> teams = session.createNativeQuery(sql, Team.class)
                .setParameter("teamCode", teamCode)
                .getResultList();

        Team team = teams.isEmpty() ? null : teams.get(0);
        if (team == null) {
            // 팀이 존재하지 않으면 삭제된 것으로 간주
            return true;
        }
        // BaseEntity의 status 필드가 DELETED 인지 확인 (StatusType은 DELETED, USABLE 등)
        return team.getStatus() == StatusType.DELETED;
    }

}
