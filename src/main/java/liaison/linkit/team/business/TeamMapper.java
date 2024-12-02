package liaison.linkit.team.business;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.profile.domain.region.Region;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.domain.scale.TeamScale;
import liaison.linkit.team.presentation.team.dto.TeamRequestDTO.AddTeamBasicInformRequest;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO;

@Mapper
public class TeamMapper {

    public Team toTeam(
            final String teamLogoImagePath,
            final AddTeamBasicInformRequest request,
            final TeamScale teamScale,
            final Region region
    ) {
        return Team.builder()
                .teamLogoImagePath(teamLogoImagePath)
                .teamName(request.getTeamName())
                .teamShortDescription(request.getTeamShortDescription())
                .teamScale(teamScale)
                .region(region)
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

}
