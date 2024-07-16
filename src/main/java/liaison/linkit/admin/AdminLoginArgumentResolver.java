package liaison.linkit.admin;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import liaison.linkit.admin.domain.AdminMember;
import liaison.linkit.admin.domain.repository.AdminMemberRepository;
import liaison.linkit.auth.AdminAuth;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.global.exception.RefreshTokenException;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.login.domain.repository.RefreshTokenRepository;
import liaison.linkit.login.infrastructure.BearerAuthorizationExtractor;
import liaison.linkit.login.infrastructure.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;

import static liaison.linkit.admin.domain.type.AdminType.ADMIN;
import static liaison.linkit.admin.domain.type.AdminType.MASTER;
import static liaison.linkit.global.exception.ExceptionCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class AdminLoginArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String REFRESH_TOKEN = "refresh-token";

    private final JwtProvider jwtProvider;

    private final BearerAuthorizationExtractor extractor;

    private final RefreshTokenRepository refreshTokenRepository;

    private final AdminMemberRepository adminMemberRepository;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.withContainingClass(Long.class)
                .hasParameterAnnotation(AdminAuth.class);
    }

    @Override
    public Accessor resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mvcContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) {
        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new BadRequestException(INVALID_REQUEST);
        }

        final String refreshToken = extractRefreshToken(request.getCookies());
        final String accessToken = extractor.extractAccessToken(webRequest.getHeader(AUTHORIZATION));
        jwtProvider.validateTokens(new MemberTokens(refreshToken, accessToken));

        final Long memberId = Long.valueOf(jwtProvider.getSubject(accessToken));

        final AdminMember adminMember = adminMemberRepository.findById(memberId)
                .orElseThrow(() -> new RefreshTokenException(INVALID_ADMIN_AUTHORITY));

        if (adminMember.getAdminType().equals(MASTER)) {
            return Accessor.master(memberId);
        }
        if (adminMember.getAdminType().equals(ADMIN)) {
            return Accessor.admin(memberId);
        }

        throw new RefreshTokenException(INVALID_ADMIN_AUTHORITY);
    }

    private String extractRefreshToken(final Cookie... cookies) {
        if (cookies == null) {
            throw new RefreshTokenException(NOT_FOUND_REFRESH_TOKEN);
        }
        return Arrays.stream(cookies)
                .filter(this::isValidRefreshToken)
                .findFirst()
                .orElseThrow(() -> new RefreshTokenException(NOT_FOUND_REFRESH_TOKEN))
                .getValue();
    }

    private boolean isValidRefreshToken(final Cookie cookie) {
        return REFRESH_TOKEN.equals(cookie.getName()) &&
                refreshTokenRepository.existsById(cookie.getValue());
    }
}
