package liaison.linkit.team.domain.repository.currentState;

import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.state.QTeamState;
import liaison.linkit.team.domain.state.TeamState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TeamStateCustomRepositoryImpl implements TeamStateCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<TeamState> findByStateName(final String teamStateName) {
        QTeamState qTeamState = QTeamState.teamState;

        TeamState teamState =
                jpaQueryFactory
                        .selectFrom(qTeamState)
                        .where(qTeamState.teamStateName.eq(teamStateName))
                        .fetchOne();

        return Optional.ofNullable(teamState);
    }
}
