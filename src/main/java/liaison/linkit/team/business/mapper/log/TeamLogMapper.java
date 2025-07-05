package liaison.linkit.team.business.mapper.log;

import java.util.List;
import java.util.stream.Collectors;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.global.util.DateUtils;
import liaison.linkit.image.domain.Image;
import liaison.linkit.team.domain.log.TeamLog;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO.UpdateTeamLogResponse;

@Mapper
public class TeamLogMapper {
    public TeamLogResponseDTO.TeamLogItem toTeamLogItem(
            final boolean isTeamManager, final TeamLog teamLog) {
        return TeamLogResponseDTO.TeamLogItem.builder()
                .isTeamManager(isTeamManager)
                .teamLogId(teamLog.getId())
                .isLogPublic(teamLog.isLogPublic())
                .logType(teamLog.getLogType())
                .createdAt(DateUtils.formatRelativeTime(teamLog.getCreatedAt()))
                .modifiedAt(teamLog.getModifiedAt())
                .logTitle(teamLog.getLogTitle())
                .logContent(teamLog.getLogContent())
                .logViewCount(teamLog.getViewCount())
                .commentCount(teamLog.getCommentCount())
                .build();
    }

    public TeamLogResponseDTO.TeamLogItems toTeamLogItems(
            final boolean isTeamManager, final List<TeamLog> teamLogs) {
        List<TeamLogResponseDTO.TeamLogItem> items =
                teamLogs.stream()
                        .map(teamLog -> toTeamLogItem(isTeamManager, teamLog))
                        .collect(Collectors.toList());

        return TeamLogResponseDTO.TeamLogItems.builder().teamLogItems(items).build();
    }

    public TeamLogResponseDTO.TeamLogRepresentItem toTeamLogRepresentItem(
            final boolean isTeamManager, final boolean isMyTeam, final List<TeamLog> teamLogs) {
        List<TeamLogResponseDTO.TeamLogItem> items =
                teamLogs.stream()
                        .map(teamLog -> toTeamLogItem(isTeamManager, teamLog))
                        .collect(Collectors.toList());

        return TeamLogResponseDTO.TeamLogRepresentItem.builder()
                .isTeamManager(isTeamManager)
                .isMyTeam(isMyTeam)
                .teamLogItems(items)
                .build();
    }

    public TeamLogResponseDTO.AddTeamLogResponse toAddTeamLogResponse(final TeamLog teamLog) {
        return TeamLogResponseDTO.AddTeamLogResponse.builder()
                .teamLogId(teamLog.getId())
                .logTitle(teamLog.getLogTitle())
                .logContent(teamLog.getLogContent())
                .createdAt(teamLog.getCreatedAt())
                .logType(teamLog.getLogType())
                .isLogPublic(teamLog.isLogPublic())
                .build();
    }

    public TeamLogResponseDTO.RemoveTeamLogResponse toRemoveTeamLog(final Long teamLogId) {
        return TeamLogResponseDTO.RemoveTeamLogResponse.builder().teamLogId(teamLogId).build();
    }

    public TeamLogResponseDTO.UpdateTeamLogTypeResponse toUpdateTeamLogType(final TeamLog teamLog) {
        return TeamLogResponseDTO.UpdateTeamLogTypeResponse.builder()
                .teamLogId(teamLog.getId())
                .logType(teamLog.getLogType())
                .build();
    }

    public TeamLogResponseDTO.AddTeamLogBodyImageResponse toAddTeamLogBodyImageResponse(
            final Image image) {
        return TeamLogResponseDTO.AddTeamLogBodyImageResponse.builder()
                .teamLogBodyImagePath(image.getImageUrl())
                .build();
    }

    public TeamLogResponseDTO.UpdateTeamLogPublicStateResponse toUpdateTeamLogPublicState(
            final TeamLog teamLog) {
        return TeamLogResponseDTO.UpdateTeamLogPublicStateResponse.builder()
                .teamLogId(teamLog.getId())
                .isLogPublic(teamLog.isLogPublic())
                .build();
    }

    public UpdateTeamLogResponse toUpdateTeamLogResponse(final TeamLog teamLog) {
        return UpdateTeamLogResponse.builder()
                .teamLogId(teamLog.getId())
                .logTitle(teamLog.getLogTitle())
                .logContent(teamLog.getLogContent())
                .isLogPublic(teamLog.isLogPublic())
                .logType(teamLog.getLogType())
                .build();
    }
}
