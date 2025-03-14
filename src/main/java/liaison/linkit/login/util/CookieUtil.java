package liaison.linkit.login.util;

import org.springframework.http.ResponseCookie;

public class CookieUtil {
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";

    // AccessToken 쿠키 생성
    public static ResponseCookie createAccessTokenCookie(String token, long maxAgeSeconds) {
        return ResponseCookie.from(ACCESS_TOKEN, token)
                .maxAge(maxAgeSeconds)
                .secure(true)
                .sameSite("None")
                .path("/")
                .httpOnly(true)
                .build();
    }

    // RefreshToken 쿠키 생성
    public static ResponseCookie createRefreshTokenCookie(String token, long maxAgeSeconds) {
        return ResponseCookie.from(REFRESH_TOKEN, token)
                .maxAge(maxAgeSeconds)
                .secure(true)
                .sameSite("None")
                .path("/")
                .httpOnly(true)
                .build();
    }

    // 쿠키 삭제 (로그아웃 시 사용)
    public static ResponseCookie createLogoutCookie(String name) {
        return ResponseCookie.from(name, "")
                .maxAge(0)
                .secure(true)
                .sameSite("None")
                .path("/")
                .httpOnly(true)
                .build();
    }
}
