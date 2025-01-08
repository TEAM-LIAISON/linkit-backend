package liaison.linkit.scrap.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.scrap.business.service.TeamScrapService;
import liaison.linkit.scrap.presentation.dto.teamScrap.TeamScrapRequestDTO;
import liaison.linkit.scrap.presentation.dto.teamScrap.TeamScrapResponseDTO;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/team/scrap")
public class TeamScrapController {
    private final TeamScrapService teamScrapService;

    @PostMapping("/{teamCode}")
    @MemberOnly
    public CommonResponse<TeamScrapResponseDTO.UpdateTeamScrap> updateTeamScrap(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @RequestBody final TeamScrapRequestDTO.UpdateTeamScrapRequest updateTeamScrapRequest  // 변경하고자 하는 boolean 상태
    ) {
        return CommonResponse.onSuccess(teamScrapService.updateTeamScrap(accessor.getMemberId(), teamCode, updateTeamScrapRequest));
    }

    // 내가 스크랩한 목록 전체 조회
    @GetMapping
    @MemberOnly
    public CommonResponse<TeamInformMenus> getProfileScraps(
            @Auth final Accessor accessor
    ) {
        return CommonResponse.onSuccess(teamScrapService.getTeamScraps(accessor.getMemberId()));
    }

}
