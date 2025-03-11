package liaison.linkit.team.presentation.log;

import java.util.Optional;

import jakarta.validation.Valid;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.team.business.service.log.TeamLogService;
import liaison.linkit.team.presentation.log.dto.TeamLogRequestDTO;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/team/{teamCode}/log")
@Slf4j
public class TeamLogController {

    private final TeamLogService teamLogService;

    // 로그 첨부 이미지 저장
    @PostMapping("/body/image")
    @MemberOnly
    @Logging(item = "Team_Log", action = "POST_ADD_TEAM_LOG_BODY_IMAGE", includeResult = true)
    public CommonResponse<TeamLogResponseDTO.AddTeamLogBodyImageResponse> addTeamLogBodyImage(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @RequestPart @Valid final MultipartFile teamLogBodyImage) {
        return CommonResponse.onSuccess(
                teamLogService.addTeamLogBodyImage(
                        accessor.getMemberId(), teamCode, teamLogBodyImage));
    }

    // 로그 전체 조회
    @GetMapping
    @Logging(item = "Team_Log", action = "GET_TEAM_LOG_ITEMS", includeResult = true)
    public CommonResponse<TeamLogResponseDTO.TeamLogItems> getTeamLogItems(
            @Auth final Accessor accessor, @PathVariable final String teamCode) {
        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        return CommonResponse.onSuccess(teamLogService.getTeamLogItems(optionalMemberId, teamCode));
    }

    // 로그 상세 조회
    @GetMapping("/{teamLogId}")
    @Logging(item = "Team_Log", action = "GET_TEAM_LOG_ITEM", includeResult = true)
    public CommonResponse<TeamLogResponseDTO.TeamLogItem> getTeamLogItem(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @PathVariable final Long teamLogId) {
        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        return CommonResponse.onSuccess(
                teamLogService.getTeamLogItem(optionalMemberId, teamCode, teamLogId));
    }

    // 대표글 조회
    @GetMapping("/represent")
    @Logging(item = "Team_Log", action = "GET_REPRESENT_TEAM_LOG_ITEM", includeResult = true)
    public CommonResponse<TeamLogResponseDTO.TeamLogItem> getRepresentTeamLogItem(
            @Auth final Accessor accessor, @PathVariable final String teamCode) {
        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        return CommonResponse.onSuccess(
                teamLogService.getRepresentTeamLogItem(optionalMemberId, teamCode));
    }

    // 로그 추가
    @PostMapping
    @MemberOnly
    @Logging(item = "Team_Log", action = "POST_ADD_TEAM_LOG", includeResult = true)
    public CommonResponse<TeamLogResponseDTO.AddTeamLogResponse> addTeamLog(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @RequestBody TeamLogRequestDTO.AddTeamLogRequest addTeamLogRequest) {
        return CommonResponse.onSuccess(
                teamLogService.addTeamLog(accessor.getMemberId(), teamCode, addTeamLogRequest));
    }

    // 팀 로그 수정
    @PostMapping("/{teamLogId}")
    @MemberOnly
    @Logging(item = "Team_Log", action = "POST_UPDATE_TEAM_LOG", includeResult = true)
    public CommonResponse<TeamLogResponseDTO.UpdateTeamLogResponse> updateTeamLog(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @PathVariable final Long teamLogId,
            @RequestBody TeamLogRequestDTO.UpdateTeamLogRequest updateTeamLogRequest) {

        return CommonResponse.onSuccess(
                teamLogService.updateTeamLog(
                        accessor.getMemberId(), teamCode, teamLogId, updateTeamLogRequest));
    }

    // 팀 로그 삭제
    @DeleteMapping("/{teamLogId}")
    @MemberOnly
    @Logging(item = "Team_Log", action = "DELETE_TEAM_LOG", includeResult = true)
    public CommonResponse<TeamLogResponseDTO.RemoveTeamLogResponse> deleteTeamLog(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @PathVariable final Long teamLogId) {
        return CommonResponse.onSuccess(
                teamLogService.removeTeamLog(accessor.getMemberId(), teamCode, teamLogId));
    }

    // 팀 로그 대표글로 변경
    @PostMapping("/type/{teamLogId}")
    @MemberOnly
    @Logging(item = "Team_Log", action = "POST_UPDATE_TEAM_LOG_TYPE", includeResult = true)
    public CommonResponse<TeamLogResponseDTO.UpdateTeamLogTypeResponse> updateTeamLogType(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @PathVariable final Long teamLogId) {
        return CommonResponse.onSuccess(
                teamLogService.updateTeamLogType(accessor.getMemberId(), teamCode, teamLogId));
    }

    // 팀 로그 공개 여부 수정
    @PostMapping("/state/{teamLogId}")
    @MemberOnly
    @Logging(item = "Team_Log", action = "POST_UPDATE_TEAM_LOG_PUBLIC_STATE", includeResult = true)
    public CommonResponse<TeamLogResponseDTO.UpdateTeamLogPublicStateResponse>
            updateTeamLogPublicState(
                    @Auth final Accessor accessor,
                    @PathVariable final String teamCode,
                    @PathVariable final Long teamLogId) {
        return CommonResponse.onSuccess(
                teamLogService.updateTeamLogPublicState(
                        accessor.getMemberId(), teamCode, teamLogId));
    }
}
