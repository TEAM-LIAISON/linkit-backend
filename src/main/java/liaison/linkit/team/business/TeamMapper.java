package liaison.linkit.team.business;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.profile.domain.region.Region;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.presentation.team.dto.TeamRequestDTO;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO;

@Mapper
public class TeamMapper {

    public Team toTeam(final String teamLogoImagePath, final TeamRequestDTO.AddTeamRequest request, final Region region) {
        return Team.builder()
                .teamLogoImagePath(teamLogoImagePath)
                .teamName(request.getTeamName())
                .teamShortDescription(request.getTeamShortDescription())
                .region(region)
                .build();
    }
    
    public TeamResponseDTO.AddTeamResponse toAddTeam(final Team team) {
        return TeamResponseDTO.AddTeamResponse.builder()
                .teamId(team.getId())
                .createdAt(team.getCreatedAt())
                .build();
    }
}
