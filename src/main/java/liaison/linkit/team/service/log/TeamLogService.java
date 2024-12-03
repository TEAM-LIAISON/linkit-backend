package liaison.linkit.team.service.log;

import static liaison.linkit.profile.domain.type.LogType.GENERAL_LOG;

import java.util.List;
import liaison.linkit.common.validator.ImageValidator;
import liaison.linkit.file.domain.ImageFile;
import liaison.linkit.file.infrastructure.S3Uploader;
import liaison.linkit.team.business.log.TeamLogMapper;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.domain.log.TeamLog;
import liaison.linkit.team.implement.TeamQueryAdapter;
import liaison.linkit.team.implement.log.TeamLogCommandAdapter;
import liaison.linkit.team.implement.log.TeamLogQueryAdapter;
import liaison.linkit.team.presentation.log.dto.TeamLogRequestDTO;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO.AddTeamLogBodyImageResponse;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO.AddTeamLogResponse;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO.RemoveTeamLogResponse;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO.TeamLogItem;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO.UpdateTeamLogTypeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamLogService {
    private final TeamQueryAdapter teamQueryAdapter;

    private final TeamLogQueryAdapter teamLogQueryAdapter;
    private final TeamLogCommandAdapter teamLogCommandAdapter;
    private final TeamLogMapper teamLogMapper;

    private final ImageValidator imageValidator;

    private final S3Uploader s3Uploader;


    @Transactional(readOnly = true)
    public TeamLogResponseDTO.TeamLogItems getTeamLogItems(final Long memberId, final String teamName) {
        log.info("memberId = {}의 팀 로그 Items 조회 요청 발생했습니다.", memberId);

        final Team team = teamQueryAdapter.findByTeamName(teamName);
        final List<TeamLog> teamLogs = teamLogQueryAdapter.getTeamLogs(team.getId());

        return teamLogMapper.toTeamLogItems(teamLogs);
    }

    @Transactional(readOnly = true)
    public TeamLogItem getTeamLogItem(final Long memberId, final String teamName, final Long teamLogId) {
        final TeamLog teamLog = teamLogQueryAdapter.getTeamLog(teamLogId);
        return teamLogMapper.toTeamLogItem(teamLog);
    }

    public AddTeamLogResponse addTeamLog(final Long memberId, final String teamName, final TeamLogRequestDTO.AddTeamLogRequest addTeamLogRequest) {
        final Team team = teamQueryAdapter.findByTeamName(teamName);
        final TeamLog TeamLog = new TeamLog(null, team, addTeamLogRequest.getLogTitle(), addTeamLogRequest.getLogContent(), addTeamLogRequest.getIsLogPublic(), GENERAL_LOG);
        final TeamLog savedTeamLog = teamLogCommandAdapter.addTeamLog(TeamLog);

        return teamLogMapper.toAddTeamLogResponse(savedTeamLog);
    }

    public RemoveTeamLogResponse removeTeamLog(final Long memberId, final String teamName, final Long teamLogId) {
        final TeamLog teamLog = teamLogQueryAdapter.getTeamLog(teamLogId);
        teamLogCommandAdapter.remove(teamLog);
        return teamLogMapper.toRemoveTeamLog(teamLogId);
    }

    public UpdateTeamLogTypeResponse updateTeamLogType(final Long memberId, final String teamName, final Long teamLogId, final TeamLogRequestDTO.UpdateTeamLogType updateTeamLogType) {
        final TeamLog teamLog = teamLogQueryAdapter.getTeamLog(teamLogId);
        final TeamLog updatedTeamLog = teamLogCommandAdapter.updateTeamLogType(teamLog, updateTeamLogType);
        return teamLogMapper.toUpdateTeamLogType(updatedTeamLog);
    }

    public AddTeamLogBodyImageResponse addTeamLogBodyImage(final Long memberId, final String teamName, final MultipartFile teamLogBodyImage) {
        String teamLogBodyImagePath = null;

        // 버켓에만 저장함
        if (imageValidator.validatingImageUpload(teamLogBodyImage)) {
            teamLogBodyImagePath = s3Uploader.uploadTeamLogBodyImage(new ImageFile(teamLogBodyImage));
        }

        return teamLogMapper.toAddTeamLogBodyImageResponse(teamLogBodyImagePath);
    }
}
