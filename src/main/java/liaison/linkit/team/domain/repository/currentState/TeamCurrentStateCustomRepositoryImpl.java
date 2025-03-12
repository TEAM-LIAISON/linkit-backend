package liaison.linkit.team.domain.repository.currentState;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.state.QTeamCurrentState;
import liaison.linkit.team.domain.state.TeamCurrentState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TeamCurrentStateCustomRepositoryImpl implements TeamCurrentStateCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<TeamCurrentState> findTeamCurrentStatesByTeamId(final Long teamId) {
        QTeamCurrentState qTeamCurrentState = QTeamCurrentState.teamCurrentState;

        return jpaQueryFactory
                .selectFrom(qTeamCurrentState)
                .where(qTeamCurrentState.team.id.eq(teamId))
                .fetch();
    }

    @Override
    public boolean existsTeamCurrentStatesByTeamId(final Long teamId) {
        QTeamCurrentState qTeamCurrentState = QTeamCurrentState.teamCurrentState;

        Integer count =
                jpaQueryFactory
                        .selectOne()
                        .from(qTeamCurrentState)
                        .where(qTeamCurrentState.team.id.eq(teamId))
                        .fetchFirst();

        return count != null;
    }

    @Override
    public void deleteAllByTeamId(final Long teamId) {
        QTeamCurrentState qTeamCurrentState = QTeamCurrentState.teamCurrentState;

        jpaQueryFactory
                .delete(qTeamCurrentState)
                .where(qTeamCurrentState.team.id.eq(teamId))
                .execute();
    }
}
