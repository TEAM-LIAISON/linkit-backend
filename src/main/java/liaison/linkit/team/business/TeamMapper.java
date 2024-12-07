package liaison.linkit.team.business;

import java.util.List;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.presentation.team.dto.TeamRequestDTO.AddTeamBasicInformRequest;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamCurrentStateItem;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenu;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;

@Mapper
public class TeamMapper {

    public Team toTeam(
            final String teamLogoImagePath,
            final AddTeamBasicInformRequest request
    ) {
        return Team.builder()
                .teamLogoImagePath(teamLogoImagePath)
                .teamName(request.getTeamName())
                .teamShortDescription(request.getTeamShortDescription())
                .build();
    }

    public TeamResponseDTO.AddTeamResponse toAddTeam(final Team team) {
        return TeamResponseDTO.AddTeamResponse.builder()
                .teamId(team.getId())
                .createdAt(team.getCreatedAt())
                .build();
    }

    public TeamResponseDTO.SaveTeamBasicInformResponse toSaveTeam(final Team team) {
        return TeamResponseDTO.SaveTeamBasicInformResponse.builder()
                .teamId(team.getId())
                .modifiedAt(team.getModifiedAt())
                .build();
    }

    public TeamResponseDTO.TeamInformMenu toTeamInformMenu(
            final Team team,
            final List<TeamCurrentStateItem> teamCurrentStateItems,
            final TeamScaleItem teamScaleItem,
            final RegionDetail regionDetail
    ) {
        return TeamResponseDTO.TeamInformMenu
                .builder()
                .teamCurrentStates(teamCurrentStateItems)
                .teamName(team.getTeamName())
                .teamShortDescription(team.getTeamShortDescription())
                .teamLogoImagePath(team.getTeamLogoImagePath())
                .teamScaleItem(teamScaleItem)
                .regionDetail(regionDetail)
                .build();
    }
    
    public TeamResponseDTO.TeamDetail toTeamDetail(
            final boolean isMyTeam,
            final TeamInformMenu teamInformMenu
    ) {
        return TeamResponseDTO.TeamDetail
                .builder()
                .isMyTeam(isMyTeam)
                .teamInformMenu(teamInformMenu)
                .build();
    }

}
