package liaison.linkit.login.presentation;

import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.HttpStatus.CREATED;

import jakarta.servlet.http.HttpServletResponse;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.login.dto.LoginRequest;
import liaison.linkit.login.dto.LoginResponse;
import liaison.linkit.login.dto.MemberTokensAndOnBoardingStepInform;
import liaison.linkit.login.dto.RenewTokenResponse;
import liaison.linkit.login.presentation.dto.AccountResponseDTO;
import liaison.linkit.login.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
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

    private final LoginService loginService;

    // 로그인
    @PostMapping("/login/{provider}")
    public ResponseEntity<LoginResponse> login(
            @PathVariable final String provider,
            @RequestBody final LoginRequest loginRequest,
            final HttpServletResponse response
    ) {
        final MemberTokensAndOnBoardingStepInform memberTokensAndOnBoardingStepInform = loginService.login(provider, loginRequest.getCode());

        final ResponseCookie cookie = ResponseCookie.from("refresh-token", memberTokensAndOnBoardingStepInform.getRefreshToken())
                .maxAge(COOKIE_AGE_SECONDS)
                .secure(true)
                .sameSite("None")
                .path("/")
                .httpOnly(true)
                .build();

        response.addHeader(SET_COOKIE, cookie.toString());

        return ResponseEntity.status(CREATED).body(
                new LoginResponse(
                        memberTokensAndOnBoardingStepInform.getAccessToken(),
                        memberTokensAndOnBoardingStepInform.getEmail(),
                        memberTokensAndOnBoardingStepInform.isExistMemberBasicInform()
                )
        );
    }

    // 토큰 재발행
    @PostMapping("/renew/token")
    public ResponseEntity<RenewTokenResponse> extendLogin(
            @CookieValue("refresh-token") final String refreshToken,
            @RequestHeader("Authorization") final String authorizationHeader
    ) {
        final RenewTokenResponse renewTokenResponse = loginService.renewalAccessToken(refreshToken, authorizationHeader);
        return ResponseEntity.status(CREATED).body(renewTokenResponse);
    }
    
    @DeleteMapping("/logout")
    @MemberOnly
    public CommonResponse<AccountResponseDTO.LogoutResponse> logout(
            @Auth final Accessor accessor,
            @CookieValue("refresh-token") final String refreshToken
    ) {
        return CommonResponse.onSuccess(loginService.logout(accessor.getMemberId(), refreshToken));
    }

    // 회원 탈퇴
    @DeleteMapping("/withdraw")
    @MemberOnly
    public ResponseEntity<Void> deleteAccount(
            @Auth final Accessor accessor
    ) {
        loginService.deleteAccount(accessor.getMemberId());
        return ResponseEntity.noContent().build();
    }
}
