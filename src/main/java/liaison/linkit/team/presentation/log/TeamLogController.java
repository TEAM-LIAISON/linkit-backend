package liaison.linkit.team.presentation.log;

import java.util.Optional;

import jakarta.validation.Valid;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
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
    public CommonResponse<TeamLogResponseDTO.TeamLogItems> getTeamLogItems(
            @Auth final Accessor accessor, @PathVariable final String teamCode) {
        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();
        log.info(
                "memberId = {}의 teamCode = {}에 대한 팀 로그 전체 조회 요청이 발생했습니다.",
                optionalMemberId,
                teamCode);

        return CommonResponse.onSuccess(teamLogService.getTeamLogItems(optionalMemberId, teamCode));
    }

    // 로그 상세 조회
    @GetMapping("/{teamLogId}")
    public CommonResponse<TeamLogResponseDTO.TeamLogItem> getTeamLogItem(
            @PathVariable final String teamCode, @PathVariable final Long teamLogId) {
        log.info("teamCode = {}에 대한 팀 로그 ID = {}의 단일 조회 요청이 발생했습니다.", teamCode, teamLogId);
        return CommonResponse.onSuccess(teamLogService.getTeamLogItem(teamCode, teamLogId));
    }

    // 대표글 조회
    @GetMapping("/represent")
    public CommonResponse<TeamLogResponseDTO.TeamLogItem> getRepresentTeamLogItem(
            @PathVariable final String teamCode) {
        log.info("teamCode = {}에 대한 대표글 조회 요청이 발생했습니다.", teamCode);
        return CommonResponse.onSuccess(teamLogService.getRepresentTeamLogItem(teamCode));
    }

    // 로그 추가
    @PostMapping
    @MemberOnly
    public CommonResponse<TeamLogResponseDTO.AddTeamLogResponse> addTeamLog(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @RequestBody TeamLogRequestDTO.AddTeamLogRequest addTeamLogRequest) {
        log.info(
                "memberId = {}의 teamCode = {}에 대한 팀 로그에 생성 요청이 발생했습니다.",
                accessor.getMemberId(),
                teamCode);
        return CommonResponse.onSuccess(
                teamLogService.addTeamLog(accessor.getMemberId(), teamCode, addTeamLogRequest));
    }

    // 팀 로그 수정
    @PostMapping("/{teamLogId}")
    @MemberOnly
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
    public CommonResponse<TeamLogResponseDTO.RemoveTeamLogResponse> deleteTeamLog(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @PathVariable final Long teamLogId) {
        log.info(
                "memberId = {}가 teamCode = {}의 팀 로그 = {}에 대한 삭제 요청이 발생했습니다.",
                accessor.getMemberId(),
                teamCode,
                teamLogId);
        return CommonResponse.onSuccess(
                teamLogService.removeTeamLog(accessor.getMemberId(), teamCode, teamLogId));
    }

    // 팀 로그 대표글로 변경
    @PostMapping("/type/{teamLogId}")
    @MemberOnly
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
    public CommonResponse<TeamLogResponseDTO.UpdateTeamLogPublicStateResponse>
            updateTeamLogPublicState(
                    @Auth final Accessor accessor,
                    @PathVariable final String teamCode,
                    @PathVariable final Long teamLogId) {
        log.info(
                "memberId = {}가 teamCode = {}의 팀 로그 = {}에 대한 공개 여부 수정 요청이 발생했습니다.",
                accessor.getMemberId(),
                teamCode,
                teamLogId);
        return CommonResponse.onSuccess(
                teamLogService.updateTeamLogPublicState(
                        accessor.getMemberId(), teamCode, teamLogId));
    }
}
