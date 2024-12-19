package liaison.linkit.team.presentation.teamMember;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberRequestDTO;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO;
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

    @GetMapping("/members")
    @MemberOnly
    public CommonResponse<TeamMemberResponseDTO.TeamMemberItems> getTeamMemberItems(
            @Auth final Accessor accessor,
            @PathVariable final String teamName
    ) {
        return CommonResponse.onSuccess(teamMemberService.getTeamMemberItems(accessor.getMemberId(), teamName));
    }

    // 팀 구성원 추가하기

    // 뷰어 및 관리자를 선택한다
    @PostMapping("/member")
    @MemberOnly
    public CommonResponse<TeamMemberResponseDTO.AddTeamMemberResponse> addTeamMember(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @RequestBody TeamMemberRequestDTO.AddTeamMemberRequest addTeamMemberRequest
    ) throws Exception {
        return CommonResponse.onSuccess(teamMemberService.addTeamMember(accessor.getMemberId(), teamName, addTeamMemberRequest));
    }
}
