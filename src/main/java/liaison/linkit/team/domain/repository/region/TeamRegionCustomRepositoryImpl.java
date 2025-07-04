package liaison.linkit.team.domain.repository.region;

import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.region.QTeamRegion;
import liaison.linkit.team.domain.region.TeamRegion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TeamRegionCustomRepositoryImpl implements TeamRegionCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsTeamRegionByTeamId(final Long teamId) {
        QTeamRegion qTeamRegion = QTeamRegion.teamRegion;

        Integer count =
                jpaQueryFactory
                        .selectOne()
                        .from(qTeamRegion)
                        .where(qTeamRegion.team.id.eq(teamId))
                        .fetchFirst();

        return count != null;
    }

    @Override
    public Optional<TeamRegion> findTeamRegionByTeamId(final Long teamId) {
        QTeamRegion qTeamRegion = QTeamRegion.teamRegion;

        TeamRegion teamRegion =
                jpaQueryFactory
                        .selectFrom(qTeamRegion)
                        .where(qTeamRegion.team.id.eq(teamId))
                        .fetchOne();

        return Optional.ofNullable(teamRegion);
    }

    @Override
    public void deleteAllByTeamId(final Long teamId) {
        QTeamRegion qTeamRegion = QTeamRegion.teamRegion;

        jpaQueryFactory.delete(qTeamRegion).where(qTeamRegion.team.id.eq(teamId)).execute();
    }
}
