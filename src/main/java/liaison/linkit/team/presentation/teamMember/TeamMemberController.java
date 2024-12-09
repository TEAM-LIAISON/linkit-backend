package liaison.linkit.team.presentation.teamMember;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO;
import liaison.linkit.team.service.teamMember.TeamMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}
