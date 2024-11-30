package liaison.linkit.team.presentation.history;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO;
import liaison.linkit.team.service.history.TeamHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/team/{teamId}/history")
@Slf4j
public class TeamHistoryController {

    private final TeamHistoryService teamHistoryService;

    @GetMapping
    @MemberOnly
    public CommonResponse<TeamHistoryResponseDTO.TeamHistoryItems> getTeamHistoryItems(
            @Auth final Accessor accessor,
            @PathVariable final Long teamId
    ) {
        log.info("memberId = {}의 팀 ID = {}에 대한 팀 연혁 전체 조회 요청이 발생했습니다.", accessor.getMemberId(), teamId);
        return CommonResponse.onSuccess(teamHistoryService.getTeamHistoryItems(accessor.getMemberId(), teamId));
    }

    @GetMapping("/{teamHistoryId}")
    @MemberOnly
    public CommonResponse<TeamHistoryResponseDTO.TeamHistoryDetail> getTeamHistoryDetail(
            @Auth final Accessor accessor,
            @PathVariable final Long teamId,
            @PathVariable final Long teamHistoryId
    ) {
        log.info("memberId = {}의 팀 ID = {}에 대한 팀 연혁 상세 조회 요청이 발생했습니다.", accessor.getMemberId(), teamId);
        return CommonResponse.onSuccess(teamHistoryService.getTeamHistoryDetail(accessor.getMemberId(), teamId, teamHistoryId));
    }
}
