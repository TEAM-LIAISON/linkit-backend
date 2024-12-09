package liaison.linkit.team.domain.repository.currentState;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import liaison.linkit.team.domain.QTeamCurrentState;
import liaison.linkit.team.domain.TeamCurrentState;
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

}
