package liaison.linkit.team.business;

import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.team.domain.TeamCurrentState;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamCurrentStateItem;

@Mapper
public class TeamCurrentStateMapper {
    public List<TeamCurrentStateItem> toTeamCurrentStateItems(
            final List<TeamCurrentState> teamCurrentStates) {
        return teamCurrentStates.stream()
                .map(currentState -> TeamCurrentStateItem.builder()
                        .teamStateName(currentState.getTeamState().getTeamStateName())
                        .build())
                .collect(Collectors.toList());
    }
}
