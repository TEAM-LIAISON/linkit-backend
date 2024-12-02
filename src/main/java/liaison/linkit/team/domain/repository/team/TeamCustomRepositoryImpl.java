package liaison.linkit.team.domain.repository.team;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import liaison.linkit.team.domain.QTeam;
import liaison.linkit.team.domain.Team;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TeamCustomRepositoryImpl implements TeamCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Team> findByTeamName(final String teamName) {
        QTeam qTeam = QTeam.team;

        Team team = jpaQueryFactory
                .selectFrom(qTeam)
                .where(qTeam.teamName.eq(teamName))
                .fetchOne();

        return Optional.ofNullable(team);
    }
}
