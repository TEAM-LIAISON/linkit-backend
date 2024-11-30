package liaison.linkit.team.business.history;

import java.util.List;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.domain.TeamHistory;
import liaison.linkit.team.presentation.history.dto.TeamHistoryRequestDTO.AddTeamHistoryRequest;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO.RemoveTeamHistoryResponse;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO.TeamHistoryItem;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO.UpdateTeamHistoryResponse;

@Mapper
public class TeamHistoryMapper {
    public TeamHistoryResponseDTO.TeamHistoryItems toTeamHistoryItems(List<TeamHistory> teamHistories) {
        List<TeamHistoryItem> items = teamHistories.stream()
                .map(this::toTeamHistoryItem)
                .toList();

        return TeamHistoryResponseDTO.TeamHistoryItems.builder()
                .teamHistoryItems(items)
                .build();
    }

    public TeamHistoryResponseDTO.TeamHistoryItem toTeamHistoryItem(final TeamHistory teamHistory) {
        return TeamHistoryResponseDTO.TeamHistoryItem
                .builder()
                .teamHistoryId(teamHistory.getId())
                .historyName(teamHistory.getHistoryName())
                .historyStartDate(teamHistory.getHistoryStartDate())
                .historyEndDate(teamHistory.getHistoryEndDate())
                .isHistoryInProgress(teamHistory.isHistoryInProgress())
                .build();
    }

    public TeamHistoryResponseDTO.TeamHistoryDetail toTeamHistoryDetail(final TeamHistory teamHistory) {
        return TeamHistoryResponseDTO.TeamHistoryDetail
                .builder()
                .teamHistoryId(teamHistory.getId())
                .historyName(teamHistory.getHistoryName())
                .historyStartDate(teamHistory.getHistoryStartDate())
                .historyEndDate(teamHistory.getHistoryEndDate())
                .isHistoryInProgress(teamHistory.isHistoryInProgress())
                .historyDescription(teamHistory.getHistoryDescription())
                .build();
    }

    public TeamHistory toAddTeamHistory(final Team team, final AddTeamHistoryRequest addTeamHistoryRequest) {
        return TeamHistory
                .builder()
                .id(null)
                .team(team)
                .historyName(addTeamHistoryRequest.getHistoryName())
                .historyStartDate(addTeamHistoryRequest.getHistoryStartDate())
                .historyEndDate(addTeamHistoryRequest.getHistoryEndDate())
                .isHistoryInProgress(addTeamHistoryRequest.getIsHistoryInProgress())
                .historyDescription(addTeamHistoryRequest.getHistoryDescription())
                .build();
    }

    public TeamHistoryResponseDTO.AddTeamHistoryResponse toAddTeamHistoryResponse(final TeamHistory teamHistory) {
        return TeamHistoryResponseDTO.AddTeamHistoryResponse
                .builder()
                .teamHistoryId(teamHistory.getId())
                .historyName(teamHistory.getHistoryName())
                .historyStartDate(teamHistory.getHistoryStartDate())
                .historyEndDate(teamHistory.getHistoryEndDate())
                .isHistoryInProgress(teamHistory.isHistoryInProgress())
                .historyDescription(teamHistory.getHistoryDescription())
                .build();
    }

    public TeamHistoryResponseDTO.UpdateTeamHistoryResponse toUpdateTeamHistoryResponse(final TeamHistory teamHistory) {
        return UpdateTeamHistoryResponse
                .builder()
                .teamHistoryId(teamHistory.getId())
                .historyName(teamHistory.getHistoryName())
                .historyStartDate(teamHistory.getHistoryStartDate())
                .historyEndDate(teamHistory.getHistoryEndDate())
                .isHistoryInProgress(teamHistory.isHistoryInProgress())
                .historyDescription(teamHistory.getHistoryDescription())
                .build();
    }

    public TeamHistoryResponseDTO.RemoveTeamHistoryResponse toRemoveTeamHistory(final Long teamHistoryId) {
        return RemoveTeamHistoryResponse.builder()
                .teamHistoryId(teamHistoryId).build();
    }
}
