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
    public TeamScale findByScaleName(final String scaleName) {
        QTeamScale qTeamScale = QTeamScale.teamScale;

        return jpaQueryFactory
                .select(qTeamScale)
                .where(qTeamScale.scaleName.eq(scaleName))
                .fetchOne();
    }
}
