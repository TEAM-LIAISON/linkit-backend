package liaison.linkit.team.presentation.history;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.team.presentation.history.dto.TeamHistoryRequestDTO;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO;
import liaison.linkit.team.business.service.history.TeamHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/team/{teamName}/history")
@Slf4j
public class TeamHistoryController {

    private final TeamHistoryService teamHistoryService;

    // 팀 연혁 뷰어 조회
    @GetMapping("/view")
    public CommonResponse<TeamHistoryResponseDTO.TeamHistoryCalendarResponse> getTeamHistoryCalendarResponses(
            @PathVariable final String teamName
    ) {
        log.info("팀 이름 = {}에 대한 팀 연혁 뷰어 전체 조회 요청이 발생했습니다.", teamName);
        return CommonResponse.onSuccess(teamHistoryService.getTeamHistoryCalendarResponses(teamName));
    }

    @GetMapping
    @MemberOnly
    public CommonResponse<TeamHistoryResponseDTO.TeamHistoryItems> getTeamHistoryItems(
            @Auth final Accessor accessor,
            @PathVariable final String teamName
    ) {
        log.info("memberId = {}의 팀 이름 = {}에 대한 팀 연혁 전체 조회 요청이 발생했습니다.", accessor.getMemberId(), teamName);
        return CommonResponse.onSuccess(teamHistoryService.getTeamHistoryItems(accessor.getMemberId(), teamName));
    }

    @GetMapping("/{teamHistoryId}")
    @MemberOnly
    public CommonResponse<TeamHistoryResponseDTO.TeamHistoryDetail> getTeamHistoryDetail(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @PathVariable final Long teamHistoryId
    ) {
        log.info("memberId = {}의 팀 이름 = {}에 대한 팀 연혁 상세 조회 요청이 발생했습니다.", accessor.getMemberId(), teamName);
        return CommonResponse.onSuccess(teamHistoryService.getTeamHistoryDetail(accessor.getMemberId(), teamName, teamHistoryId));
    }

    // 팀 연혁 생성
    @PostMapping
    @MemberOnly
    public CommonResponse<TeamHistoryResponseDTO.AddTeamHistoryResponse> addTeamHistory(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @RequestBody final TeamHistoryRequestDTO.AddTeamHistoryRequest addTeamHistoryRequest
    ) {
        log.info("memberId = {}의 팀 이름 = {}에 대한 팀 연혁 단일 생성 요청이 발생했습니다.", accessor.getMemberId(), teamName);
        return CommonResponse.onSuccess(teamHistoryService.addTeamHistory(accessor.getMemberId(), teamName, addTeamHistoryRequest));
    }

    @PostMapping("/{teamHistoryId}")
    @MemberOnly
    public CommonResponse<TeamHistoryResponseDTO.UpdateTeamHistoryResponse> updateTeamHistory(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @PathVariable final Long teamHistoryId,
            @RequestBody final TeamHistoryRequestDTO.UpdateTeamHistoryRequest updateTeamHistoryRequest
    ) {
        log.info("memberId = {}의 팀 이름 = {}에 대한 팀 연혁 단일 수정 요청이 발생했습니다.", accessor.getMemberId(), teamName);
        return CommonResponse.onSuccess(teamHistoryService.updateTeamHistory(accessor.getMemberId(), teamName, teamHistoryId, updateTeamHistoryRequest));
    }

    @DeleteMapping("/{teamHistoryId}")
    @MemberOnly
    public CommonResponse<TeamHistoryResponseDTO.RemoveTeamHistoryResponse> removeTeamHistory(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @PathVariable final Long teamHistoryId
    ) {
        return CommonResponse.onSuccess(teamHistoryService.removeTeamHistory(accessor.getMemberId(), teamName, teamHistoryId));
    }

}
