package liaison.linkit.login.infrastructure;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthTokenMigrationInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 헤더에 토큰이 있고 쿠키에 토큰이 없는 경우에만 처리
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        Cookie[] cookies = request.getCookies();
        boolean hasAccessTokenCookie = false;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    hasAccessTokenCookie = true;
                    break;
                }
            }
        }

        // 헤더 토큰은 있지만 쿠키 토큰은 없는 경우 - 임시 토큰 마이그레이션
        if (!hasAccessTokenCookie && authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);

            // 유효한 토큰인지 확인
            if (jwtProvider.isValidAccess(accessToken)) {
                // 응답에 쿠키로 추가 (다음 요청부터는 쿠키로 인증 가능)
                ResponseCookie accessCookie =
                        ResponseCookie.from("accessToken", accessToken)
                                .maxAge(jwtProvider.getTokenExpiration(accessToken))
                                .secure(true)
                                .sameSite("None")
                                .path("/")
                                .httpOnly(true)
                                .build();

                response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
                log.info("헤더 기반 토큰을 쿠키로 마이그레이션 했습니다.");
            }
        }

        return true;
    }
}
