package liaison.linkit.team.presentation.team;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.team.presentation.team.dto.TeamRequestDTO;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.UpdateTeamResponse;
import liaison.linkit.team.business.service.team.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
            @RequestPart @Valid TeamRequestDTO.AddTeamRequest addTeamRequest
    ) {
        return CommonResponse.onSuccess(teamService.createTeam(accessor.getMemberId(), teamLogoImage, addTeamRequest));
    }

    // 팀 기본 정보 수정
    @PostMapping("/team/{teamCode}")
    @MemberOnly
    public CommonResponse<UpdateTeamResponse> updateTeam(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @RequestPart(required = false) MultipartFile teamLogoImage,
            @RequestPart @Valid TeamRequestDTO.UpdateTeamRequest updateTeamRequest
    ) {
        return CommonResponse.onSuccess(teamService.updateTeam(accessor.getMemberId(), teamCode, teamLogoImage, updateTeamRequest));
    }

    // 팀 상단 메뉴
    @GetMapping("/team/{teamCode}")
    @MemberOnly
    public CommonResponse<TeamResponseDTO.TeamDetail> getTeamDetail(
            @PathVariable final String teamCode,
            @Auth final Accessor accessor
    ) {
        log.info("teamCode = {}", teamCode);
        if (accessor.isMember()) {
            log.info("memberId = {}의 팀 코드 = {}에 대한 팀 상세 조회 요청이 발생했습니다.", accessor.getMemberId(), teamCode);
            return CommonResponse.onSuccess(teamService.getLoggedInTeamDetail(accessor.getMemberId(), teamCode));
        } else {
            log.info("teamCode = {}에 대한 팀 상세 조회 요청이 발생했습니다.", teamCode);
            return CommonResponse.onSuccess(teamService.getLoggedOutTeamDetail(teamCode));
        }
    }

    // 팀 삭제 요청

    // 팀 나가기 요청

    // 나의 팀 조회
    @GetMapping("/my/teams")
    @MemberOnly
    public CommonResponse<TeamResponseDTO.TeamItems> getTeamItems(
            @Auth final Accessor accessor
    ) {
        return CommonResponse.onSuccess(teamService.getTeamItems(accessor.getMemberId()));
    }
}
