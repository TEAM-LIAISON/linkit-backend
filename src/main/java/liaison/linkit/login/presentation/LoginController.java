package liaison.linkit.login.presentation;

import static org.springframework.http.HttpHeaders.SET_COOKIE;

import jakarta.servlet.http.HttpServletResponse;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.login.presentation.dto.AccountRequestDTO;
import liaison.linkit.login.presentation.dto.AccountResponseDTO;
import liaison.linkit.login.service.LoginService;
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

    public static final int COOKIE_AGE_SECONDS = 604800;
    public static final int ACCESS_COOKIE_AGE_SECONDS = 302400;
    private final LoginService loginService;

    // 회원이 로그인한다
    @PostMapping("/login/{provider}")
    public CommonResponse<AccountResponseDTO.LoginResponse> login(
        @PathVariable final String provider,
        @RequestBody final AccountRequestDTO.LoginRequest loginRequest,
        final HttpServletResponse response
    ) {
        final AccountResponseDTO.LoginServiceResponse loginResponse = loginService.login(provider, loginRequest.getCode());
        log.info("loginResponse = {}", loginResponse);

        // 1) refreshToken 쿠키 설정
        final ResponseCookie cookie = ResponseCookie.from("refreshToken", loginResponse.getRefreshToken())
            .maxAge(COOKIE_AGE_SECONDS)
            .secure(true)
            .sameSite("Lax")
            .path("/")
            .httpOnly(true)
            .build();

        response.addHeader(SET_COOKIE, cookie.toString());

        log.info("response 설정 = {}", response);
        return CommonResponse.onSuccess(
            new AccountResponseDTO.LoginResponse(
                loginResponse.getAccessToken(),
                loginResponse.getEmail(),
                loginResponse.getEmailId(),
                loginResponse.getMemberName(),
                loginResponse.getIsMemberBasicInform()
            )
        );
    }

    // accessToken을 재발행한다
    @PostMapping("/renew/token")
    public CommonResponse<AccountResponseDTO.RenewTokenResponse> renewToken(
        @CookieValue("refreshToken") final String refreshToken,
        @RequestHeader("Authorization") final String authorizationHeader
    ) {
        return CommonResponse.onSuccess(loginService.renewalAccessToken(refreshToken, authorizationHeader));
    }

    // 회원이 로그아웃을 한다
    @DeleteMapping("/logout")
    @MemberOnly
    public CommonResponse<AccountResponseDTO.LogoutResponse> logout(
        @Auth final Accessor accessor,
        @CookieValue("refreshToken") final String refreshToken
    ) {
        return CommonResponse.onSuccess(loginService.logout(accessor.getMemberId(), refreshToken));
    }

    // 회원이 회원 탈퇴를 한다
    @DeleteMapping("/quit")
    @MemberOnly
    public CommonResponse<AccountResponseDTO.QuitAccountResponse> quitAccount(
        @Auth final Accessor accessor,
        @CookieValue("refreshToken") final String refreshToken
    ) {
        return CommonResponse.onSuccess(loginService.quitAccount(accessor.getMemberId(), refreshToken));
    }
}
