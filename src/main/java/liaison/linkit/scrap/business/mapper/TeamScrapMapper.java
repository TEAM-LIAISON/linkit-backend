package liaison.linkit.scrap.business.mapper;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.scrap.presentation.dto.teamScrap.TeamScrapResponseDTO;
import liaison.linkit.scrap.presentation.dto.teamScrap.TeamScrapResponseDTO.UpdateTeamScrap;

@Mapper
public class TeamScrapMapper {

    public TeamScrapResponseDTO.UpdateTeamScrap toUpdateTeamScrap(
            final String teamName,
            final boolean changeScrapValue
    ) {
        return UpdateTeamScrap.builder()
                .teamName(teamName)
                .isTeamScrap(changeScrapValue)
                .build();
    }
}
