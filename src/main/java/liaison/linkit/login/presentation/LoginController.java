package liaison.linkit.login.presentation;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import jakarta.servlet.http.HttpServletResponse;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.config.AuthProperties;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.login.presentation.dto.AccountRequestDTO;
import liaison.linkit.login.presentation.dto.AccountResponseDTO;
import liaison.linkit.login.service.LoginService;
import liaison.linkit.login.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class LoginController {

    // 쿠키 만료 시간 상수
    public static final int REFRESH_TOKEN_AGE_SECONDS = 604800; // 7일
    public static final int ACCESS_TOKEN_AGE_SECONDS = 86400; // 1일 (실제 환경에 맞게 조정)

    private final LoginService loginService;
    private final AuthProperties authProperties;

    // 회원이 로그인한다
    @PostMapping("/login/{provider}")
    @Logging(item = "Login", action = "POST_LOGIN", includeResult = true)
    public CommonResponse<AccountResponseDTO.LoginResponse> login(
            @PathVariable final String provider,
            @RequestBody final AccountRequestDTO.LoginRequest loginRequest,
            final HttpServletResponse response) {
        final AccountResponseDTO.LoginServiceResponse loginResponse =
                loginService.login(provider, loginRequest.getCode());

        // AccessToken 쿠키 설정 (CookieUtil 사용)
        ResponseCookie accessCookie =
                CookieUtil.createAccessTokenCookie(
                        loginResponse.getAccessToken(), ACCESS_TOKEN_AGE_SECONDS);

        // RefreshToken 쿠키 설정 (CookieUtil 사용)
        ResponseCookie refreshCookie =
                CookieUtil.createRefreshTokenCookie(
                        loginResponse.getRefreshToken(), REFRESH_TOKEN_AGE_SECONDS);

        response.addHeader(SET_COOKIE, accessCookie.toString());
        response.addHeader(SET_COOKIE, refreshCookie.toString());

        // 인증 모드에 따라 응답 내용 조정
        if ("cookie".equals(authProperties.getMode())) {
            // 쿠키 전용 모드: 응답 본문에 토큰 포함하지 않음
            return CommonResponse.onSuccess(
                    new AccountResponseDTO.LoginResponse(
                            null, // AccessToken 제외
                            loginResponse.getEmail(),
                            loginResponse.getEmailId(),
                            loginResponse.getMemberName(),
                            loginResponse.getIsMemberBasicInform()));
        } else {
            // 하이브리드 또는 헤더 모드: 응답 본문에 토큰 포함 (기존 호환성)
            return CommonResponse.onSuccess(
                    new AccountResponseDTO.LoginResponse(
                            loginResponse.getAccessToken(),
                            loginResponse.getEmail(),
                            loginResponse.getEmailId(),
                            loginResponse.getMemberName(),
                            loginResponse.getIsMemberBasicInform()));
        }
    }

    // accessToken을 재발행한다
    @PostMapping("/renew/token")
    @Logging(item = "Login", action = "POST_RENEW_TOKEN", includeResult = true)
    public CommonResponse<AccountResponseDTO.RenewTokenResponse> renewToken(
            @CookieValue("refreshToken") final String refreshToken,
            @RequestHeader("Authorization") final String authorizationHeader) {
        return CommonResponse.onSuccess(
                loginService.renewalAccessToken(refreshToken, authorizationHeader));
    }

    // 회원이 로그아웃을 한다
    @DeleteMapping("/logout")
    @MemberOnly
    @Logging(item = "Login", action = "DELETE_LOGOUT", includeResult = true)
    public CommonResponse<AccountResponseDTO.LogoutResponse> logout(
            @Auth final Accessor accessor,
            @CookieValue(value = "refreshToken", required = false) final String refreshToken,
            final HttpServletResponse response) {

        // 로그아웃 처리
        final AccountResponseDTO.LogoutResponse logoutResponse =
                loginService.logout(accessor.getMemberId(), refreshToken);

        // 쿠키 삭제
        response.addHeader(SET_COOKIE, CookieUtil.createLogoutCookie("accessToken").toString());
        response.addHeader(SET_COOKIE, CookieUtil.createLogoutCookie("refreshToken").toString());

        return CommonResponse.onSuccess(logoutResponse);
    }

    // 회원이 회원 탈퇴를 한다
    @DeleteMapping("/quit")
    @MemberOnly
    @Logging(item = "Login", action = "DELETE_QUIT_ACCOUNT", includeResult = true)
    public CommonResponse<AccountResponseDTO.QuitAccountResponse> quitAccount(
            @Auth final Accessor accessor,
            @CookieValue(value = "refreshToken", required = false) final String refreshToken,
            final HttpServletResponse response) {

        // 회원 탈퇴 처리
        final AccountResponseDTO.QuitAccountResponse quitResponse =
                loginService.quitAccount(accessor.getMemberId(), refreshToken);

        // 쿠키 삭제
        response.addHeader(SET_COOKIE, CookieUtil.createLogoutCookie("accessToken").toString());
        response.addHeader(SET_COOKIE, CookieUtil.createLogoutCookie("refreshToken").toString());

        return CommonResponse.onSuccess(quitResponse);
    }
}
