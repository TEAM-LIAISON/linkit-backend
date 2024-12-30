package liaison.linkit.team.presentation.log;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.team.presentation.log.dto.TeamLogRequestDTO;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO;
import liaison.linkit.team.service.log.TeamLogService;
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
@RequestMapping("/api/v1/team/{teamName}/log")
@Slf4j
public class TeamLogController {
    private final TeamLogService teamLogService;

    // 로그 첨부 이미지 저장
    @PostMapping("/body/image")
    @MemberOnly
    public CommonResponse<TeamLogResponseDTO.AddTeamLogBodyImageResponse> addTeamLogBodyImage(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @RequestPart @Valid final MultipartFile teamLogBodyImage
    ) {
        return CommonResponse.onSuccess(teamLogService.addTeamLogBodyImage(accessor.getMemberId(), teamName, teamLogBodyImage));
    }

    // 로그 전체 뷰어 조회 (public만 허용)
    @GetMapping("/view")
    public CommonResponse<TeamLogResponseDTO.TeamLogItems> getTeamLogViewItems(
            @PathVariable final String teamName
    ) {
        return CommonResponse.onSuccess(teamLogService.getTeamLogViewItems(teamName));
    }

    // 로그 전체 조회
    @GetMapping
    @MemberOnly
    public CommonResponse<TeamLogResponseDTO.TeamLogItems> getTeamLogItems(
            @Auth final Accessor accessor,
            @PathVariable final String teamName
    ) {
        log.info("memberId = {}의 팀 이름 = {}에 대한 팀 로그 전체 조회 요청이 발생했습니다.", accessor.getMemberId(), teamName);
        return CommonResponse.onSuccess(teamLogService.getTeamLogItems(accessor.getMemberId(), teamName));
    }

    // 로그 상세 조회
    @GetMapping("/{teamLogId}")
    @MemberOnly
    public CommonResponse<TeamLogResponseDTO.TeamLogItem> getTeamLogItem(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @PathVariable final Long teamLogId
    ) {
        log.info("memberId = {}의 팀 이름 = {}에 대한 팀 로그 ID = {}의 단일 조회 요청이 발생했습니다.", accessor.getMemberId(), teamName, teamLogId);
        return CommonResponse.onSuccess(teamLogService.getTeamLogItem(accessor.getMemberId(), teamName, teamLogId));
    }

    // 로그 추가
    @PostMapping
    @MemberOnly
    public CommonResponse<TeamLogResponseDTO.AddTeamLogResponse> addTeamLog(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @RequestBody TeamLogRequestDTO.AddTeamLogRequest addTeamLogRequest
    ) {
        log.info("memberId = {}의 팀 이름 = {}에 대한 팀 로그에 생성 요청이 발생했습니다.", accessor.getMemberId(), teamName);
        return CommonResponse.onSuccess(teamLogService.addTeamLog(accessor.getMemberId(), teamName, addTeamLogRequest));
    }

    // 팀 로그 수정
    @PostMapping("/{teamLogId}")
    @MemberOnly
    public CommonResponse<TeamLogResponseDTO.UpdateTeamLogResponse> updateTeamLog(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @PathVariable final Long teamLogId,
            @RequestBody TeamLogRequestDTO.UpdateTeamLogRequest updateTeamLogRequest
    ) {

        return CommonResponse.onSuccess(teamLogService.updateTeamLog(accessor.getMemberId(), teamName, teamLogId, updateTeamLogRequest));
    }

    // 팀 로그 삭제
    @DeleteMapping("/{teamLogId}")
    @MemberOnly
    public CommonResponse<TeamLogResponseDTO.RemoveTeamLogResponse> deleteTeamLog(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @PathVariable final Long teamLogId
    ) {
        log.info("memberId = {}가 팀 이름 = {}의 팀 로그 = {}에 대한 삭제 요청이 발생했습니다.", accessor.getMemberId(), teamName, teamLogId);
        return CommonResponse.onSuccess(teamLogService.removeTeamLog(accessor.getMemberId(), teamName, teamLogId));
    }

    // 팀 로그 대표글로 변경
    @PostMapping("/type/{teamLogId}")
    @MemberOnly
    public CommonResponse<TeamLogResponseDTO.UpdateTeamLogTypeResponse> updateTeamLogType(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @PathVariable final Long teamLogId
    ) {
        return CommonResponse.onSuccess(teamLogService.updateTeamLogType(accessor.getMemberId(), teamName, teamLogId));
    }

    // 팀 로그 공개 여부 수정
    @PostMapping("/state/{teamLogId}")
    @MemberOnly
    public CommonResponse<TeamLogResponseDTO.UpdateTeamLogPublicStateResponse> updateTeamLogPublicState(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @PathVariable final Long teamLogId
    ) {
        log.info("memberId = {}가 팀 이름 = {}의 팀 로그 = {}에 대한 공개 여부 수정 요청이 발생했습니다.", accessor.getMemberId(), teamName, teamLogId);
        return CommonResponse.onSuccess(teamLogService.updateTeamLogPublicState(accessor.getMemberId(), teamName, teamLogId));
    }
}
