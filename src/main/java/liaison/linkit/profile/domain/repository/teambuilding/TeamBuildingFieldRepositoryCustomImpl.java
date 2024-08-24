package liaison.linkit.profile.domain.repository.teambuilding;


import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.teambuilding.QTeamBuildingField;
import liaison.linkit.profile.domain.teambuilding.TeamBuildingField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class TeamBuildingFieldRepositoryCustomImpl implements TeamBuildingFieldRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<TeamBuildingField> findTeamBuildingFieldsByFieldNames(List<String> teamBuildingFieldNames) {
        QTeamBuildingField teamBuildingField = QTeamBuildingField.teamBuildingField;

        return jpaQueryFactory
                .selectFrom(teamBuildingField)
                .where(teamBuildingField.teamBuildingFieldName.in(teamBuildingFieldNames))
                .fetch();
    }
}
