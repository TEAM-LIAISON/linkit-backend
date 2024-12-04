package liaison.linkit.team.presentation.team;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.team.presentation.team.dto.TeamRequestDTO;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO;
import liaison.linkit.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class TeamController {

    private final TeamService teamService;

    // 팀 생성하기
    @PostMapping("/team")
    @MemberOnly
    public CommonResponse<TeamResponseDTO.AddTeamResponse> createTeam(
            @Auth final Accessor accessor,
            @RequestPart(required = false) MultipartFile teamLogoImage,
            @RequestPart @Valid TeamRequestDTO.AddTeamBasicInformRequest addTeamBasicInformRequest
    ) {
        return CommonResponse.onSuccess(teamService.createTeam(accessor.getMemberId(), teamLogoImage, addTeamBasicInformRequest));
    }

    // 팀 기본정보 저장
    @PostMapping("/{teamId}")
    @MemberOnly
    public CommonResponse<TeamResponseDTO.SaveTeamBasicInformResponse> saveTeamBasicInform(
            @Auth final Accessor accessor,
            @RequestParam final Long teamId,
            @RequestPart(required = false) MultipartFile teamLogoImage,
            @RequestPart @Valid TeamRequestDTO.AddTeamBasicInformRequest addTeamBasicInformRequest
    ) {
        return CommonResponse.onSuccess(teamService.saveTeamBasicInform(accessor.getMemberId(), teamId, teamLogoImage, addTeamBasicInformRequest));
    }
}
