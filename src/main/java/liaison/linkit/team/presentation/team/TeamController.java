package liaison.linkit.team.presentation.team;

import java.util.Optional;

import jakarta.validation.Valid;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.team.business.service.team.TeamService;
import liaison.linkit.team.presentation.team.dto.TeamRequestDTO;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.UpdateTeamResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    // 홈화면에서 팀 조회하기 (최대 4개)
    @GetMapping("/home/team")
    public CommonResponse<TeamResponseDTO.TeamInformMenus> getHomeTeamInformMenus(
            @Auth final Accessor accessor) {
        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        return CommonResponse.onSuccess(teamService.getHomeTeamInformMenus(optionalMemberId));
    }

    // 팀 생성하기
    @PostMapping("/team")
    @MemberOnly
    public CommonResponse<TeamResponseDTO.AddTeamResponse> createTeam(
            @Auth final Accessor accessor,
            @RequestPart(required = false) MultipartFile teamLogoImage,
            @RequestPart @Valid TeamRequestDTO.AddTeamRequest addTeamRequest) {
        return CommonResponse.onSuccess(
                teamService.createTeam(accessor.getMemberId(), teamLogoImage, addTeamRequest));
    }

    // 팀 기본 정보 수정
    @PostMapping("/team/{teamCode}")
    @MemberOnly
    public CommonResponse<UpdateTeamResponse> updateTeam(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @RequestPart(required = false) MultipartFile teamLogoImage,
            @RequestPart @Valid TeamRequestDTO.UpdateTeamRequest updateTeamRequest) {
        return CommonResponse.onSuccess(
                teamService.updateTeam(
                        accessor.getMemberId(), teamCode, teamLogoImage, updateTeamRequest));
    }

    // 팀 상단 메뉴
    @GetMapping("/team/{teamCode}")
    public CommonResponse<TeamResponseDTO.TeamDetail> getTeamDetail(
            @Auth final Accessor accessor, @PathVariable final String teamCode) {
        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        return CommonResponse.onSuccess(teamService.getTeamDetail(optionalMemberId, teamCode));
    }

    // 팀 삭제 요청
    @DeleteMapping("/team/{teamCode}")
    @MemberOnly
    public CommonResponse<TeamResponseDTO.DeleteTeamResponse> deleteTeam(
            @PathVariable final String teamCode, @Auth final Accessor accessor) {
        return CommonResponse.onSuccess(teamService.deleteTeam(accessor.getMemberId(), teamCode));
    }

    // 나의 팀 조회
    @GetMapping("/my/teams")
    @MemberOnly
    public CommonResponse<TeamResponseDTO.TeamItems> getTeamItems(@Auth final Accessor accessor) {
        return CommonResponse.onSuccess(teamService.getTeamItems(accessor.getMemberId()));
    }
}
