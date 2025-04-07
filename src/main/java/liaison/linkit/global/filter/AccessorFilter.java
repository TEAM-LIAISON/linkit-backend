package liaison.linkit.global.filter;

import java.io.IOException;
import java.util.Arrays;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.login.domain.repository.RefreshTokenRepository;
import liaison.linkit.login.infrastructure.BearerAuthorizationExtractor;
import liaison.linkit.login.infrastructure.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccessorFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final BearerAuthorizationExtractor extractor;
    private final RefreshTokenRepository refreshTokenRepository;

    private static final String REFRESH_TOKEN = "refreshToken";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String accessToken = extractor.extractAccessToken(authorizationHeader);
                String refreshToken = extractRefreshToken(request.getCookies());

                jwtProvider.validateTokens(new MemberTokens(accessToken, refreshToken));
                Long memberId = Long.valueOf(jwtProvider.getSubject(accessToken));

                Accessor accessor = Accessor.member(memberId);
                request.setAttribute("accessor", accessor);
            } else {
                request.setAttribute("accessor", Accessor.guest());
            }
        } catch (Exception e) {
            log.warn("[AccessorFilter] 인증 실패 처리 - 게스트로 진행: {}", e.getMessage());
            request.setAttribute("accessor", Accessor.guest());
        }

        filterChain.doFilter(request, response);
    }

    private String extractRefreshToken(Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(
                        c ->
                                REFRESH_TOKEN.equals(c.getName())
                                        && refreshTokenRepository.existsById(c.getValue()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
