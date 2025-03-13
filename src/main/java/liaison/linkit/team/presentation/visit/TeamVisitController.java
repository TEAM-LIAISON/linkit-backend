package liaison.linkit.team.presentation.visit;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.team.presentation.visit.dto.TeamVisitResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/team/{teamCode}/visit")
@Slf4j
public class TeamVisitController {

    private final TeamVisitService teamVisitService;

    // 프로필 방문자를 조회한다.
    @GetMapping
    @MemberOnly
    @Logging(item = "Team_Visit", action = "GET_TEAM_VISIT_INFORMS", includeResult = true)
    public CommonResponse<TeamVisitResponseDTO.TeamVisitInforms> getTeamVisitInforms(
            @Auth final Accessor accessor, @PathVariable final String teamCode) {
        return CommonResponse.onSuccess(
                teamVisitService.getTeamVisitInforms(accessor.getMemberId(), teamCode));
    }
}
