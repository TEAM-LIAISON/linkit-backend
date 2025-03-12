package liaison.linkit.login;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Arrays;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.exception.RefreshTokenExpiredException;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.login.domain.repository.RefreshTokenRepository;
import liaison.linkit.login.infrastructure.BearerAuthorizationExtractor;
import liaison.linkit.login.infrastructure.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
@Slf4j
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String ACCESS_TOKEN = "accessToken";
    private final JwtProvider jwtProvider;
    private final BearerAuthorizationExtractor extractor;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.withContainingClass(Long.class).hasParameterAnnotation(Auth.class);
    }

    @Override
    public Accessor resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory) {
        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        // 1. 먼저 쿠키에서 액세스 토큰 추출 시도
        String accessToken = extractTokenFromCookie(request.getCookies(), ACCESS_TOKEN);

        // 2. 쿠키에 액세스 토큰이 없으면 헤더에서 추출 시도 (기존 방식)
        if (accessToken == null) {
            final String authorizationHeader = webRequest.getHeader(AUTHORIZATION);
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                log.info("Authorization 헤더가 없거나 형식이 올바르지 않습니다. 게스트로 처리됩니다.");
                return Accessor.guest();
            }
            accessToken = extractor.extractAccessToken(authorizationHeader);
        }

        try {
            // 3. 리프레시 토큰은 항상 쿠키에서 추출
            final String refreshToken = extractRefreshToken(request.getCookies());

            jwtProvider.validateTokens(new MemberTokens(accessToken, refreshToken));
            final Long memberId = Long.valueOf(jwtProvider.getSubject(accessToken));
            return Accessor.member(memberId);
        } catch (final RefreshTokenExpiredException e) {
            throw RefreshTokenExpiredException.EXCEPTION;
        }
    }

    private String extractRefreshToken(final Cookie... cookies) {
        if (cookies == null) {
            throw RefreshTokenExpiredException.EXCEPTION;
        }
        return Arrays.stream(cookies)
                .filter(this::isValidRefreshToken)
                .findFirst()
                .orElseThrow(() -> RefreshTokenExpiredException.EXCEPTION)
                .getValue();
    }

    private String extractTokenFromCookie(final Cookie[] cookies, final String tokenName) {
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> tokenName.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private boolean isValidRefreshToken(final Cookie cookie) {
        return REFRESH_TOKEN.equals(cookie.getName())
                && refreshTokenRepository.existsById(cookie.getValue());
    }
}
