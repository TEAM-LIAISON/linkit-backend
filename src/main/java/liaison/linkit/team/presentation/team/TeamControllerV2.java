package liaison.linkit.team.presentation.team;

import java.util.Optional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.config.AuthProperties;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.team.business.service.team.TeamService;
import liaison.linkit.team.presentation.team.dto.TeamRequestDTO;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
            @Auth final Accessor accessor,
            HttpServletRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent) {

        // 인증 정보 디버깅 로그
        logAuthenticationDetails(request, accessor, "홈 팀 조회");

        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        // 성능 측정 시작
        long startTime = System.currentTimeMillis();

        TeamResponseDTO.TeamInformMenus result =
                teamService.getHomeTeamInformMenus(optionalMemberId);

        // 성능 측정 종료 및 로깅
        long executionTime = System.currentTimeMillis() - startTime;
        log.info(
                "[성능측정] 홈 팀 조회 API 실행시간: {}ms, 인증모드: {}, 클라이언트: {}",
                executionTime,
                authProperties.getMode(),
                userAgent);

        return CommonResponse.onSuccess(result);
    }

    // 팀 기본 정보 수정
    @PostMapping("/team/{teamCode}")
    @MemberOnly
    @Logging(item = "Team", action = "POST_UPDATE_TEAM_V2", includeResult = true)
    public CommonResponse<TeamResponseDTO.UpdateTeamResponse> updateTeam(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            @RequestPart(required = false) MultipartFile teamLogoImage,
            @RequestPart @Valid TeamRequestDTO.UpdateTeamRequest updateTeamRequest,
            HttpServletRequest request) {

        // 인증 정보 디버깅 로그
        logAuthenticationDetails(request, accessor, "팀 정보 수정");

        // 회원 검증 추가 - @MemberOnly가 있지만 명시적으로 한번 더 확인
        if (!accessor.isMember()) {
            log.warn("팀 정보 수정 시도 - 비회원 접근");
            throw new IllegalStateException("팀 정보 수정은 회원만 가능합니다");
        }

        return CommonResponse.onSuccess(
                teamService.updateTeam(
                        accessor.getMemberId(), teamCode, teamLogoImage, updateTeamRequest));
    }

    // 팀 상단 메뉴
    @GetMapping("/team/{teamCode}")
    @Logging(item = "Team", action = "GET_TEAM_DETAIL_V2", includeResult = true)
    public CommonResponse<TeamResponseDTO.TeamDetail> getTeamDetail(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode,
            HttpServletRequest request) {

        // 인증 정보 디버깅 로그
        logAuthenticationDetails(request, accessor, "팀 상세 조회");

        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        return CommonResponse.onSuccess(teamService.getTeamDetail(optionalMemberId, teamCode));
    }

    /** 요청의 인증 관련 정보를 로깅하는 유틸리티 메서드 */
    private void logAuthenticationDetails(
            HttpServletRequest request, Accessor accessor, String apiName) {
        try {
            // 현재 인증 모드
            String authMode = authProperties.getMode();

            // 쿠키 정보 확인
            Cookie[] cookies = request.getCookies();
            boolean hasAccessTokenCookie = false;
            boolean hasRefreshTokenCookie = false;
            String accessTokenValue = "없음";

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("accessToken".equals(cookie.getName())) {
                        hasAccessTokenCookie = true;
                        // 토큰의 앞부분만 로깅 (보안)
                        accessTokenValue = maskToken(cookie.getValue());
                    }
                    if ("refreshToken".equals(cookie.getName())) {
                        hasRefreshTokenCookie = true;
                    }
                }
            }

            // 헤더 정보 확인
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            boolean hasAuthHeader = authHeader != null && authHeader.startsWith("Bearer ");
            String headerTokenValue = "없음";

            if (hasAuthHeader) {
                headerTokenValue = maskToken(authHeader.substring(7));
            }

            // 인증 상세 정보 로깅
            log.info("===== 인증 정보 [{}] =====", apiName);
            log.info("인증 모드: {}", authMode);
            log.info(
                    "회원 여부: {}",
                    accessor.isMember() ? "회원 (ID: " + accessor.getMemberId() + ")" : "비회원");
            log.info(
                    "쿠키 토큰 존재: AccessToken={}, RefreshToken={}",
                    hasAccessTokenCookie,
                    hasRefreshTokenCookie);
            log.info("헤더 토큰 존재: {}", hasAuthHeader);
            log.info("클라이언트 IP: {}", getClientIp(request));
            log.info("요청 경로: {}", request.getRequestURI());

            // 모드별 인증 진입점 확인
            if ("cookie".equals(authMode) && hasAccessTokenCookie && accessor.isMember()) {
                log.info("★ 쿠키 기반 인증 성공 ★");
            } else if ("header".equals(authMode) && hasAuthHeader && accessor.isMember()) {
                log.info("★ 헤더 기반 인증 성공 ★");
            } else if ("hybrid".equals(authMode) && accessor.isMember()) {
                if (hasAccessTokenCookie) {
                    log.info("★ 하이브리드 모드 - 쿠키 기반 인증 성공 ★");
                } else if (hasAuthHeader) {
                    log.info("★ 하이브리드 모드 - 헤더 기반 인증 성공 ★");
                }
            } else if (!accessor.isMember()) {
                log.info("⚠ 인증 실패 또는 게스트 접근 ⚠");
            }

            // 인증 토큰 불일치 검사 (디버깅에 유용)
            if (hasAccessTokenCookie
                    && hasAuthHeader
                    && !accessTokenValue.equals(headerTokenValue)) {
                log.warn("⚠ 쿠키 토큰과 헤더 토큰이 일치하지 않음 ⚠");
            }

        } catch (Exception e) {
            log.error("인증 정보 로깅 중 오류 발생", e);
        }
    }

    /** 토큰 값을 마스킹하여 반환 (로깅 시 보안) */
    private String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return "유효하지 않은 토큰";
        }
        // 앞 8자만 표시하고 나머지는 마스킹
        return token.substring(0, 8) + "...";
    }

    /** 클라이언트 IP 주소 가져오기 */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
