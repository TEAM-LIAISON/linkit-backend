package liaison.linkit.team.domain.repository.teamScale;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.scale.QTeamScale;
import liaison.linkit.team.domain.scale.TeamScale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TeamScaleCustomRepositoryImpl implements TeamScaleCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsTeamScaleByTeamId(final Long teamId) {
        QTeamScale qTeamScale = QTeamScale.teamScale;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(qTeamScale)
                .where(qTeamScale.team.id.eq(teamId))
                .fetchFirst();

        return count != null;
    }

    @Override
    public TeamScale findTeamScaleByTeamId(final Long teamId) {
        QTeamScale qTeamScale = QTeamScale.teamScale;

        return jpaQueryFactory
                .selectFrom(qTeamScale)
                .where(qTeamScale.team.id.eq(teamId))
                .fetchOne();
    }
}
