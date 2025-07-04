package liaison.linkit.team.presentation.history;

import java.util.Optional;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.team.business.service.history.TeamHistoryService;
import liaison.linkit.team.presentation.history.dto.TeamHistoryRequestDTO;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO;
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
@RequestMapping("/api/v1/team/{teamCode}/history")
@Slf4j
public class TeamHistoryController {

    private final TeamHistoryService teamHistoryService;

    // 팀 연혁 뷰어 조회
    @GetMapping("/view")
    @Logging(item = "Team_History", action = "GET_TEAM_HISTORY_CALENDAR", includeResult = true)
    public CommonResponse<TeamHistoryResponseDTO.TeamHistoryCalendarResponse>
            getTeamHistoryCalendar(
                    @Auth final Accessor accessor, @PathVariable final String teamCode) {

        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        return CommonResponse.onSuccess(
                teamHistoryService.getTeamHistoryCalendarResponses(optionalMemberId, teamCode));
    }

    // 팀 연혁 수정창 조회
    @GetMapping
    @MemberOnly
    @Logging(item = "Team_History", action = "GET_TEAM_HISTORY_ITEMS", includeResult = true)
    public CommonResponse<TeamHistoryResponseDTO.TeamHistoryItems> getTeamHistoryItems(
            @Auth final Accessor accessor, @PathVariable final String teamCode) {
        return CommonResponse.onSuccess(
                teamHistoryService.getTeamHistoryItems(accessor.getMemberId(), teamCode));
    }

    @GetMapping("/{teamHistoryId}")
    @MemberOnly
    @Logging(item = "Team_History", action = "GET_TEAM_HISTORY_DETAIL", includeResult = true)
    public CommonResponse<TeamHistoryResponseDTO.TeamHistoryDetail> getTeamHistoryDetail(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @PathVariable final Long teamHistoryId) {
        return CommonResponse.onSuccess(
                teamHistoryService.getTeamHistoryDetail(
                        accessor.getMemberId(), teamCode, teamHistoryId));
    }

    // 팀 연혁 생성
    @PostMapping
    @MemberOnly
    @Logging(item = "Team_History", action = "POST_ADD_TEAM_HISTORY", includeResult = true)
    public CommonResponse<TeamHistoryResponseDTO.AddTeamHistoryResponse> addTeamHistory(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @RequestBody final TeamHistoryRequestDTO.AddTeamHistoryRequest addTeamHistoryRequest) {
        return CommonResponse.onSuccess(
                teamHistoryService.addTeamHistory(
                        accessor.getMemberId(), teamCode, addTeamHistoryRequest));
    }

    @PostMapping("/{teamHistoryId}")
    @MemberOnly
    @Logging(item = "Team_History", action = "POST_UPDATE_TEAM_HISTORY", includeResult = true)
    public CommonResponse<TeamHistoryResponseDTO.UpdateTeamHistoryResponse> updateTeamHistory(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @PathVariable final Long teamHistoryId,
            @RequestBody
                    final TeamHistoryRequestDTO.UpdateTeamHistoryRequest updateTeamHistoryRequest) {
        return CommonResponse.onSuccess(
                teamHistoryService.updateTeamHistory(
                        accessor.getMemberId(), teamCode, teamHistoryId, updateTeamHistoryRequest));
    }

    @DeleteMapping("/{teamHistoryId}")
    @MemberOnly
    @Logging(item = "Team_History", action = "DELETE_REMOVE_TEAM_HISTORY", includeResult = true)
    public CommonResponse<TeamHistoryResponseDTO.RemoveTeamHistoryResponse> removeTeamHistory(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @PathVariable final Long teamHistoryId) {
        return CommonResponse.onSuccess(
                teamHistoryService.removeTeamHistory(
                        accessor.getMemberId(), teamCode, teamHistoryId));
    }
}
