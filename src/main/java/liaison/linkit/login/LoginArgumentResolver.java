package liaison.linkit.login;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.global.exception.RefreshTokenException;
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

import java.util.Arrays;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_REQUEST;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_REFRESH_TOKEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@Component
@Slf4j
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String REFRESH_TOKEN = "refresh-token";

    private final JwtProvider jwtProvider;

    private final BearerAuthorizationExtractor extractor;

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.withContainingClass(Long.class)
                .hasParameterAnnotation(Auth.class);
    }

//    @Override
//    public Accessor resolveArgument(
//            final MethodParameter parameter,
//            final ModelAndViewContainer mavContainer,
//            final NativeWebRequest webRequest,
//            final WebDataBinderFactory binderFactory
//    ) {
//        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
//        if (request == null) {
//            throw new BadRequestException(INVALID_REQUEST);
//        }
//
//        try {
//            final String refreshToken = extractRefreshToken(request.getCookies(), "refreshTokenKey"); // Assume your refresh token cookie is named "refreshTokenKey"
//            final String accessToken = extractor.extractAccessToken(webRequest.getHeader(AUTHORIZATION));
//            log.info("refreshToken={}", refreshToken);
//            log.info("accessToken={}", accessToken);
//
//            log.info("터지는 여부 1");
//            jwtProvider.validateTokens(new MemberTokens(refreshToken, accessToken));
//            log.info("터지는 여부 2");
//
//            final Long memberId = Long.valueOf(jwtProvider.getSubject(accessToken));
//            log.info("터지는 여부 3");
//            return Accessor.member(memberId);
//        } catch (final RefreshTokenException e) {
//            log.info("asdf");
//            return Accessor.guest();
//        }
//    }


    @Override
    public Accessor resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) {
        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new BadRequestException(INVALID_REQUEST);
        }

        try {
            final String refreshToken = extractRefreshToken(request.getCookies(), "refresh-token"); // Assume your refresh token cookie is named "refreshTokenKey"
            final String accessToken = extractor.extractAccessToken(webRequest.getHeader(AUTHORIZATION));
            log.info("refreshToken={}", refreshToken);
            log.info("accessToken={}", accessToken);

            log.info("터지는 여부 1");
            jwtProvider.validateTokens(new MemberTokens(refreshToken, accessToken));
            log.info("터지는 여부 2");

            final Long memberId = Long.valueOf(jwtProvider.getSubject(accessToken));
            log.info("터지는 여부 3");
            return Accessor.member(memberId);
        } catch (final RefreshTokenException e) {
            log.info("asdf");
            return Accessor.guest();
        }
    }

//    private String extractRefreshToken(final Cookie... cookies) {
//        if (cookies == null) {
//            throw new RefreshTokenException(NOT_FOUND_REFRESH_TOKEN);
//        }
//        return Arrays.stream(cookies)
//                .filter(this::isValidRefreshToken)
//                .findFirst()
//                .orElseThrow(() -> new RefreshTokenException(NOT_FOUND_REFRESH_TOKEN))
//                .getValue();
//    }


    private String extractRefreshToken(final Cookie[] cookies, String key) {
        if (cookies == null) {
            throw new RefreshTokenException(NOT_FOUND_REFRESH_TOKEN);
        }
        return Arrays.stream(cookies)
                .filter(cookie -> key.equals(cookie.getName()))
                .findFirst()
                .orElseThrow(() -> new RefreshTokenException(NOT_FOUND_REFRESH_TOKEN))
                .getValue();
    }

    private boolean isValidRefreshToken(final Cookie cookie) {
        return REFRESH_TOKEN.equals(cookie.getName()) &&
                refreshTokenRepository.existsById(cookie.getValue());
    }
}
