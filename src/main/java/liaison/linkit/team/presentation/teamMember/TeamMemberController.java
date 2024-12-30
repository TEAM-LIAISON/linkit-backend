package liaison.linkit.team.presentation.teamMember;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberRequestDTO;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.TeamMemberItems;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.TeamMemberViewItems;
import liaison.linkit.team.service.teamMember.TeamMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/team/{teamName}")
@Slf4j
public class TeamMemberController {

    private final TeamMemberService teamMemberService;

    // 팀원 뷰어 전체 조회
    @GetMapping("/members/view")
    public CommonResponse<TeamMemberViewItems> getTeamMemberViewItems(
            @PathVariable final String teamName
    ) {
        return CommonResponse.onSuccess(teamMemberService.getTeamMemberViewItems(teamName));
    }

    // 팀원 목록 조회
    @GetMapping("/members/edit")
    @MemberOnly
    public CommonResponse<TeamMemberItems> getTeamMemberItems(
            @Auth final Accessor accessor,
            @PathVariable final String teamName
    ) {
        return CommonResponse.onSuccess(teamMemberService.getTeamMemberItems(accessor.getMemberId(), teamName));
    }

    // 뷰어 및 관리자를 선택해서 팀 구성원 추가하기
    @PostMapping("/member")
    @MemberOnly
    public CommonResponse<TeamMemberResponseDTO.AddTeamMemberResponse> addTeamMember(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @RequestBody TeamMemberRequestDTO.AddTeamMemberRequest addTeamMemberRequest
    ) throws Exception {
        return CommonResponse.onSuccess(teamMemberService.addTeamMember(accessor.getMemberId(), teamName, addTeamMemberRequest));
    }

    // 뷰어 및 관리자 변경 요청
    @PostMapping("/member/type/{emailId}")
    @MemberOnly
    public CommonResponse<TeamMemberResponseDTO.UpdateTeamMemberTypeResponse> updateTeamMemberType(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @PathVariable final String emailId,
            @RequestBody TeamMemberRequestDTO.UpdateTeamMemberTypeRequest updateTeamMemberTypeRequest
    ) {
        return CommonResponse.onSuccess(teamMemberService.updateTeamMemberType(accessor.getMemberId(), teamName, emailId, updateTeamMemberTypeRequest));
    }

}
