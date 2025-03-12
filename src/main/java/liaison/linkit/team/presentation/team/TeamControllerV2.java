package liaison.linkit.team.presentation.team;

import java.util.Optional;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.config.AuthProperties;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.team.business.service.team.TeamService;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2") // 새 버전 API
@Slf4j
public class TeamControllerV2 {
    private final TeamService teamService;
    private final AuthProperties authProperties;

    // 홈화면에서 팀 조회 API (쿠키 전용)
    @GetMapping("/home/team")
    @Logging(item = "Team", action = "GET_HOME_TEAM_INFORM_MENUS_V2", includeResult = false)
    public CommonResponse<TeamResponseDTO.TeamInformMenus> getHomeTeamInformMenus(
            @Auth final Accessor accessor) {

        // 쿠키 전용 인증 로직 적용
        if ("cookie".equals(authProperties.getMode()) && !accessor.isMember()) {
            // 쿠키 모드에서 추가 검증이 필요한 경우
            log.info("쿠키 인증 모드에서 특별한 처리 적용");
        }

        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        return CommonResponse.onSuccess(teamService.getHomeTeamInformMenus(optionalMemberId));
    }
}
