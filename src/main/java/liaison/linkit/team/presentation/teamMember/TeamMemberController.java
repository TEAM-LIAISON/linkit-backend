package liaison.linkit.team.presentation.teamMember;

import java.util.Optional;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.team.business.service.teamMember.TeamMemberService;
import liaison.linkit.team.presentation.team.dto.TeamRequestDTO.UpdateManagingTeamStateRequest;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberRequestDTO;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberRequestDTO.RemoveTeamMemberRequest;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.TeamMemberItems;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.TeamMemberViewItems;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.UpdateManagingTeamStateResponse;
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
@RequestMapping("/api/v1/team/{teamCode}")
@Slf4j
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    // 팀원 뷰어 전체 조회
    // 수락한 팀원만 뜨도록 (명세 완료)
    @GetMapping("/members/view")
    @Logging(item = "Team_Member", action = "GET_TEAM_MEMBER_VIEW_ITEMS", includeResult = true)
    public CommonResponse<TeamMemberViewItems> getTeamMemberViewItems(
            @Auth final Accessor accessor, @PathVariable final String teamCode) {

        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        return CommonResponse.onSuccess(
                teamMemberService.getTeamMemberViewItems(optionalMemberId, teamCode));
    }

    // 팀원 목록 조회 (명세 완료)
    @GetMapping("/members/edit")
    @Logging(item = "Team_Member", action = "GET_TEAM_MEMBER_ITEMS", includeResult = true)
    public CommonResponse<TeamMemberItems> getTeamMemberItems(
            @Auth final Accessor accessor, @PathVariable final String teamCode) {
        return CommonResponse.onSuccess(
                teamMemberService.getTeamMemberItems(accessor.getMemberId(), teamCode));
    }

    // 뷰어 및 관리자를 선택해서 팀 구성원 추가하기 (명세 완료)
    @PostMapping("/member")
    @MemberOnly
    @Logging(item = "Team_Member", action = "POST_ADD_TEAM_MEMBER", includeResult = true)
    public CommonResponse<TeamMemberResponseDTO.AddTeamMemberResponse> addTeamMember(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @RequestBody TeamMemberRequestDTO.AddTeamMemberRequest addTeamMemberRequest)
            throws Exception {
        return CommonResponse.onSuccess(
                teamMemberService.addTeamMember(
                        accessor.getMemberId(), teamCode, addTeamMemberRequest));
    }

    // 뷰어 및 관리자 변경 요청 (명세 완료)
    @PostMapping("/member/type/{emailId}")
    @MemberOnly
    @Logging(item = "Team_Member", action = "POST_UPDATE_TEAM_MEMBER_TYPE", includeResult = true)
    public CommonResponse<TeamMemberResponseDTO.UpdateTeamMemberTypeResponse> updateTeamMemberType(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @PathVariable final String emailId,
            @RequestBody
                    TeamMemberRequestDTO.UpdateTeamMemberTypeRequest updateTeamMemberTypeRequest) {
        return CommonResponse.onSuccess(
                teamMemberService.updateTeamMemberType(
                        accessor.getMemberId(), teamCode, emailId, updateTeamMemberTypeRequest));
    }

    // 팀 스스로 나가기 요청 (명세 완료)
    @DeleteMapping("/member/out")
    @MemberOnly
    @Logging(item = "Team_Member", action = "DELETE_GET_OUT_TEAM", includeResult = true)
    public CommonResponse<TeamMemberResponseDTO.TeamOutResponse> getOutTeam(
            @Auth final Accessor accessor, @PathVariable final String teamCode) {
        return CommonResponse.onSuccess(
                teamMemberService.getOutTeam(accessor.getMemberId(), teamCode));
    }

    // 팀 삭제 수락 또는 거절 (명세 완료)
    @PostMapping("/managing/teamState")
    @MemberOnly
    @Logging(item = "Team_Member", action = "POST_UPDATE_MANAGING_TEAM_STATE", includeResult = true)
    public CommonResponse<UpdateManagingTeamStateResponse> updateManagingTeamState(
            @PathVariable final String teamCode,
            @Auth final Accessor accessor,
            @RequestBody final UpdateManagingTeamStateRequest updateManagingTeamStateRequest) {
        return CommonResponse.onSuccess(
                teamMemberService.updateManagingTeamState(
                        accessor.getMemberId(), teamCode, updateManagingTeamStateRequest));
    }

    // 팀원 삭제하기 요청 (오너 / 관리자)
    @PostMapping("/member/remove")
    @MemberOnly
    @Logging(item = "Team_Member", action = "POST_REMOVE_TEAM_MEMBER", includeResult = true)
    public CommonResponse<TeamMemberResponseDTO.RemoveTeamMemberResponse> removeTeamMember(
            @PathVariable final String teamCode,
            @Auth final Accessor accessor,
            @RequestBody final RemoveTeamMemberRequest removeTeamMemberRequest) {
        return CommonResponse.onSuccess(
                teamMemberService.removeTeamMember(
                        teamCode, accessor.getMemberId(), removeTeamMemberRequest));
    }

    // 팀 초대 수락하기 (명세 완료)
    @PostMapping("/member/join")
    @MemberOnly
    @Logging(item = "Team_Member", action = "POST_JOIN_TEAM", includeResult = true)
    public CommonResponse<TeamMemberResponseDTO.TeamJoinResponse> joinTeam(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @RequestBody final TeamMemberRequestDTO.TeamJoinRequest teamJoinRequest) {
        return CommonResponse.onSuccess(
                teamMemberService.joinTeam(accessor.getMemberId(), teamCode, teamJoinRequest));
    }
}
