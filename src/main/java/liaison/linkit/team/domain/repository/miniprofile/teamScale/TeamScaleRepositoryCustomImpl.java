package liaison.linkit.team.domain.repository.miniprofile.teamScale;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.miniprofile.QTeamScale;
import liaison.linkit.team.domain.miniprofile.TeamScale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TeamScaleRepositoryCustomImpl implements TeamScaleRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public TeamScale findBySizeType(final String sizeType) {
        QTeamScale teamScale = QTeamScale.teamScale;

        return jpaQueryFactory
                .selectFrom(teamScale)
                .where(teamScale.sizeType.eq(sizeType))
                .fetchOne();
    }
}
