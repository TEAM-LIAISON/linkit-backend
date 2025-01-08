package liaison.linkit.scrap.business.mapper;

import java.util.List;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.scrap.presentation.dto.teamScrap.TeamScrapResponseDTO;
import liaison.linkit.scrap.presentation.dto.teamScrap.TeamScrapResponseDTO.UpdateTeamScrap;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenu;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenus;

@Mapper
public class TeamScrapMapper {

    public TeamScrapResponseDTO.UpdateTeamScrap toUpdateTeamScrap(
            final String teamCode,
            final boolean changeScrapValue
    ) {
        return UpdateTeamScrap.builder()
                .teamCode(teamCode)
                .isTeamScrap(changeScrapValue)
                .build();
    }

    public TeamInformMenus toTeamInformMenus(final List<TeamInformMenu> teamInformMenus) {
        return TeamInformMenus.builder()
                .teamInformMenus(teamInformMenus)
                .build();
    }
}
