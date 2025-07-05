package liaison.linkit.team.domain.repository.log;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.log.QTeamLogImage;
import liaison.linkit.team.domain.log.TeamLog;
import liaison.linkit.team.domain.log.TeamLogImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TeamLogImageCustomRepositoryImpl implements TeamLogImageCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<TeamLogImage> findByTeamLog(final TeamLog teamLog) {
        QTeamLogImage qTeamLogImage = QTeamLogImage.teamLogImage;

        return queryFactory
                .selectFrom(qTeamLogImage)
                .where(qTeamLogImage.teamLog.eq(teamLog))
                .fetch();
    }
}
