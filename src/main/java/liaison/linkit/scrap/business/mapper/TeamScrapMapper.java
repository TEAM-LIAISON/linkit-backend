package liaison.linkit.scrap.business.mapper;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.scrap.domain.TeamScrap;
import liaison.linkit.scrap.presentation.dto.teamScrap.TeamScrapResponseDTO.AddTeamScrap;
import liaison.linkit.scrap.presentation.dto.teamScrap.TeamScrapResponseDTO.RemoveTeamScrap;

@Mapper
public class TeamScrapMapper {

    public AddTeamScrap toAddTeamScrap(final TeamScrap teamScrap) {
        return AddTeamScrap.builder()
                .createdAt(teamScrap.getCreatedAt())
                .build();
    }

    public RemoveTeamScrap toRemoveTeamScrap() {
        return RemoveTeamScrap.builder()
                .build();
    }
}
