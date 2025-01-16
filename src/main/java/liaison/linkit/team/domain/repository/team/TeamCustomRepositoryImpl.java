package liaison.linkit.team.domain.repository.team;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
@Slf4j
public class TeamCustomRepositoryImpl implements TeamCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Team> findByTeamCode(final String teamCode) {
        QTeam qTeam = QTeam.team;

        Team team = jpaQueryFactory
                .selectFrom(qTeam)
                .where(qTeam.teamCode.eq(teamCode))
                .fetchOne();

        return Optional.ofNullable(team);
    }

    @Override
    public boolean existsByTeamCode(final String teamCode) {
        QTeam qTeam = QTeam.team;

        return jpaQueryFactory
                .selectOne()
                .from(qTeam)
                .where(qTeam.teamCode.eq(teamCode))
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

    @Override
    public List<Team> findTopTeams(final int limit) {
        QTeam qTeam = QTeam.team;

        return jpaQueryFactory
                .selectFrom(qTeam)
                .where(
                        qTeam.isTeamPublic.eq(true),
                        qTeam.status.eq(StatusType.USABLE)
                )
                .orderBy(qTeam.createdAt.desc()) // 최신순으로 정렬
                .limit(limit)
                .fetch();
    }

}
