//package liaison.linkit.admin.presentation;
//
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.validation.Valid;
//import liaison.linkit.admin.dto.request.AdminLoginRequest;
//import liaison.linkit.admin.service.AdminLoginService;
//import liaison.linkit.auth.AdminAuth;
//import liaison.linkit.auth.domain.Accessor;
//import liaison.linkit.login.domain.MemberTokens;
//import liaison.linkit.login.dto.AccessTokenResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseCookie;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import static org.springframework.http.HttpHeaders.SET_COOKIE;
//import static org.springframework.http.HttpStatus.CREATED;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/admin")
//public class AdminLoginController {
//
//    private static final int COOKIE_AGE_SECONDS = 604800; // 1주일
//
//    private final AdminLoginService adminLoginService;
//
//    @PostMapping("/login")
//    public ResponseEntity<AccessTokenResponse> login(
//            @RequestBody @Valid final AdminLoginRequest adminLoginRequest,
//            final HttpServletResponse response
//    ) {
//        final MemberTokens memberTokens = adminLoginService.login(adminLoginRequest);
//        final ResponseCookie cookie = ResponseCookie.from("refresh-token", memberTokens.getRefreshToken())
//                .maxAge(COOKIE_AGE_SECONDS)
//                .sameSite("None")
//                .secure(true)
//                .httpOnly(true)
//                .path("/")
//                .build();
//        response.addHeader(SET_COOKIE, cookie.toString());
//        return ResponseEntity.status(CREATED).body(new AccessTokenResponse(memberTokens.getAccessToken()));
//    }
//
//    @PostMapping("/token")
//    public ResponseEntity<AccessTokenResponse> extendLogin(
//            @CookieValue("refresh-token") final String refreshToken,
//            @RequestHeader("Authorization") final String authorizationHeader
//    ) {
//        final String renewalRefreshToken = adminLoginService.renewalAccessToken(refreshToken, authorizationHeader);
//        return ResponseEntity.status(CREATED).body(new AccessTokenResponse(renewalRefreshToken));
//    }
//
//    @DeleteMapping("/logout")
//    public ResponseEntity<Void> logout(
//            @AdminAuth final Accessor accessor,
//            @CookieValue("refresh-token") final String refreshToken
//    ) {
//        adminLoginService.removeRefreshToken(refreshToken);
//        return ResponseEntity.noContent().build();
//    }
//}
