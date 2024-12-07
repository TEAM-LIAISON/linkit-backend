package liaison.linkit.team.business;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.team.domain.scale.TeamScale;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;

@Mapper
public class TeamScaleMapper {

    public TeamScaleItem toTeamScaleItem(
            final TeamScale teamScale
    ) {
        return TeamScaleItem
                .builder()
                .teamScaleName(teamScale.getScale().getScaleName())
                .build();
    }
}
