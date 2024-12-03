package liaison.linkit.team.business.log;

import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.team.domain.log.TeamLog;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO.TeamLogItem;

@Mapper
public class TeamLogMapper {
    public TeamLogResponseDTO.TeamLogItem toTeamLogItem(final TeamLog teamLog) {
        return TeamLogResponseDTO.TeamLogItem.builder()
                .teamLogId(teamLog.getId())
                .isLogPublic(teamLog.isLogPublic())
                .logType(teamLog.getLogType())
                .modifiedAt(teamLog.getModifiedAt())
                .logTitle(teamLog.getLogTitle())
                .logContent(teamLog.getLogContent())
                .build();
    }

    public TeamLogResponseDTO.TeamLogItems toTeamLogItems(final List<TeamLog> teamLogs) {
        List<TeamLogItem> items = teamLogs.stream()
                .map(this::toTeamLogItem)
                .collect(Collectors.toList());

        return TeamLogResponseDTO.TeamLogItems.builder()
                .teamLogItems(items)
                .build();
    }

    public TeamLogResponseDTO.AddTeamLogResponse toAddTeamLogResponse(final TeamLog teamLog) {
        return TeamLogResponseDTO.AddTeamLogResponse
                .builder()
                .teamLogId(teamLog.getId())
                .logTitle(teamLog.getLogTitle())
                .logContent(teamLog.getLogContent())
                .createdAt(teamLog.getCreatedAt())
                .logType(teamLog.getLogType())
                .isLogPublic(teamLog.isLogPublic())
                .build();
    }

    public TeamLogResponseDTO.RemoveTeamLogResponse toRemoveTeamLog(final Long teamLogId) {
        return TeamLogResponseDTO.RemoveTeamLogResponse
                .builder()
                .teamLogId(teamLogId)
                .build();
    }

    public TeamLogResponseDTO.UpdateTeamLogTypeResponse toUpdateTeamLogType(final TeamLog teamLog) {
        return TeamLogResponseDTO.UpdateTeamLogTypeResponse
                .builder()
                .teamLogId(teamLog.getId())
                .logType(teamLog.getLogType())
                .build();
    }


    public TeamLogResponseDTO.AddTeamLogBodyImageResponse toAddTeamLogBodyImageResponse(final String teamLogBodyImagePath) {
        return TeamLogResponseDTO.AddTeamLogBodyImageResponse
                .builder()
                .teamLogBodyImagePath(teamLogBodyImagePath)
                .build();
    }
}
