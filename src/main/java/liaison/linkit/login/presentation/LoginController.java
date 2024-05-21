package liaison.linkit.login.presentation;

import jakarta.servlet.http.HttpServletResponse;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.login.dto.AccessTokenResponse;
import liaison.linkit.login.dto.LoginRequest;
import liaison.linkit.login.dto.LoginResponse;
import liaison.linkit.login.dto.MemberTokensAndIsBasicInform;
import liaison.linkit.login.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
public class LoginController {

    public static final int COOKIE_AGE_SECONDS = 604800;

    private final LoginService loginService;

    // 로그인
    @PostMapping("/login/{provider}")
    public ResponseEntity<LoginResponse> login(
            @PathVariable final String provider,
            @RequestBody final LoginRequest loginRequest,
            final HttpServletResponse response
    ){
        final MemberTokensAndIsBasicInform memberTokensAndIsBasicInform = loginService.login(provider, loginRequest.getCode());
        final ResponseCookie cookie = ResponseCookie.from("refresh-token", memberTokensAndIsBasicInform.getRefreshToken())
                .maxAge(COOKIE_AGE_SECONDS)
                .sameSite("None")
                .path("/")
                .build();
        response.addHeader(SET_COOKIE, cookie.toString());

        return ResponseEntity.status(CREATED).body(
                new LoginResponse(
                        memberTokensAndIsBasicInform.getAccessToken(),
                        memberTokensAndIsBasicInform.getIsMemberBasicInform(),
                        memberTokensAndIsBasicInform.getEmail()
                )
        );
    }

    // 토큰 재발행
    @PostMapping("/token")
    public ResponseEntity<AccessTokenResponse> extendLogin(
            @CookieValue("refresh-token") final String refreshToken,
            @RequestHeader("Authorization") final String authorizationHeader
    ) {
        final String renewalRefreshToken = loginService.renewalAccessToken(refreshToken, authorizationHeader);
        return ResponseEntity.status(CREATED).body(new AccessTokenResponse(renewalRefreshToken));
    }

    // 로그아웃
    @DeleteMapping("/logout")
    @MemberOnly
    public ResponseEntity<Void> logout(
            @Auth final Accessor accessor,
            @CookieValue("refresh-token") final String refreshToken) {
        loginService.removeRefreshToken(refreshToken);
        return ResponseEntity.noContent().build();
    }

    // 회원 탈퇴
    @DeleteMapping("/account")
    @MemberOnly
    public ResponseEntity<Void> deleteAccount(@Auth final Accessor accessor) {
        loginService.deleteAccount(accessor.getMemberId());
        return ResponseEntity.noContent().build();
    }
}
