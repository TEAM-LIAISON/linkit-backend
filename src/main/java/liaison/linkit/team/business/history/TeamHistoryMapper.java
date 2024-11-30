package liaison.linkit.team.business.history;

import java.util.List;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.team.domain.TeamHistory;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO.TeamHistoryItem;

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
    
}
