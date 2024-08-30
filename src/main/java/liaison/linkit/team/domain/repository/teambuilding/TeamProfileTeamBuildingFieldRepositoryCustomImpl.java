package liaison.linkit.team.domain.repository.teambuilding;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.teambuilding.QTeamProfileTeamBuildingField;
import liaison.linkit.team.domain.teambuilding.TeamProfileTeamBuildingField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class TeamProfileTeamBuildingFieldRepositoryCustomImpl implements TeamProfileTeamBuildingFieldRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByTeamProfileId(final Long teamProfileId) {
        QTeamProfileTeamBuildingField teamProfileTeamBuildingField = QTeamProfileTeamBuildingField.teamProfileTeamBuildingField;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(teamProfileTeamBuildingField)
                .where(teamProfileTeamBuildingField.teamProfile.id.eq(teamProfileId))
                .fetchFirst();

        return count != null;
    }

    @Override
    public List<TeamProfileTeamBuildingField> findAllByTeamProfileId(final Long teamProfileId) {
        QTeamProfileTeamBuildingField teamProfileTeamBuildingField = QTeamProfileTeamBuildingField.teamProfileTeamBuildingField;

        return jpaQueryFactory
                .selectFrom(teamProfileTeamBuildingField)
                .where(teamProfileTeamBuildingField.teamProfile.id.eq(teamProfileId))
                .fetch();
    }

    @Override
    public void deleteAllByTeamProfileId(final Long teamProfileId) {
        QTeamProfileTeamBuildingField teamProfileTeamBuildingField = QTeamProfileTeamBuildingField.teamProfileTeamBuildingField;

        jpaQueryFactory
                .delete(teamProfileTeamBuildingField)
                .where(teamProfileTeamBuildingField.teamProfile.id.eq(teamProfileId))
                .execute();
    }
}
