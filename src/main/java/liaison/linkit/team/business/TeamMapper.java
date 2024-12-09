package liaison.linkit.team.business;

import java.util.List;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.presentation.team.dto.TeamRequestDTO.AddTeamRequest;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamCurrentStateItem;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenu;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;

@Mapper
public class TeamMapper {

    public Team toTeam(
            final String teamLogoImagePath,
            final AddTeamRequest request
    ) {
        return Team.builder()
                .teamLogoImagePath(teamLogoImagePath)
                .teamName(request.getTeamName())
                .teamShortDescription(request.getTeamShortDescription())
                .isTeamPublic(request.getIsTeamPublic())
                .build();
    }

    public TeamResponseDTO.AddTeamResponse toAddTeam(
            final Team team,
            final TeamScaleItem teamScaleItem,
            final RegionDetail regionDetail,
            final List<TeamCurrentStateItem> teamCurrentStateItems
    ) {
        return TeamResponseDTO.AddTeamResponse.builder()
                .teamId(team.getId())
                .teamLogoImagePath(team.getTeamLogoImagePath())
                .teamName(team.getTeamName())
                .teamShortDescription(team.getTeamShortDescription())
                .teamScaleItem(teamScaleItem)
                .regionDetail(regionDetail)
                .teamCurrentStates(teamCurrentStateItems)
                .isTeamPublic(team.isTeamPublic())
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
