package liaison.linkit.global.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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

    // 주요 검색 엔진 봇 User-Agent 목록
    private static final List<String> SEARCH_ENGINE_BOTS =
            Arrays.asList(
                    "Googlebot",
                    "bingbot",
                    "Yeti",
                    "Daum",
                    "Baiduspider",
                    "facebookexternalhit",
                    "Twitterbot",
                    "NaverBot",
                    "YandexBot",
                    "DuckDuckBot");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 검색 엔진 봇 감지
        String userAgent = request.getHeader("User-Agent");
        boolean isBot = isBotUserAgent(userAgent);

        // 봇 정보를 요청 속성에 저장 (다른 컴포넌트에서 활용할 수 있도록)
        if (isBot) {
            request.setAttribute("isBot", true);
            request.setAttribute("botType", identifyBotType(userAgent));

            // 검색 엔진 봇을 위한 SEO 헤더 추가
            addSeoHeaders(response);

            // 봇은 항상 게스트로 처리
            request.setAttribute("accessor", Accessor.guest());
        } else {
            // 2. 일반 사용자 인증 처리 (기존 로직)
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
        }

        // 3. URL 정규화 확인 (SEO 최적화)
        if (shouldRedirectToCanonicalUrl(request)) {
            redirectToCanonicalUrl(request, response);
            return;
        }

        // 4. 모바일 기기 감지 (모바일 SEO 최적화에 활용)
        boolean isMobile = isMobileDevice(userAgent);
        request.setAttribute("isMobile", isMobile);

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

    // 검색 엔진 봇 감지
    private boolean isBotUserAgent(String userAgent) {
        if (userAgent == null) {
            return false;
        }

        return SEARCH_ENGINE_BOTS.stream().anyMatch(userAgent::contains);
    }

    // 봇 유형 식별
    private String identifyBotType(String userAgent) {
        if (userAgent == null) {
            return "unknown";
        }

        if (userAgent.contains("Googlebot")) {
            return "google";
        }
        if (userAgent.contains("bingbot")) {
            return "bing";
        }
        if (userAgent.contains("Yeti")) {
            return "naver";
        }
        if (userAgent.contains("Daum")) {
            return "kakao";
        }
        if (userAgent.contains("facebookexternalhit")) {
            return "facebook";
        }
        if (userAgent.contains("Twitterbot")) {
            return "twitter";
        }

        return "other";
    }

    // SEO 관련 응답 헤더 추가
    private void addSeoHeaders(HttpServletResponse response) {
        // 중요: X-Robots-Tag 헤더 추가
        response.setHeader("X-Robots-Tag", "index, follow");

        // 캐싱 최적화 헤더
        response.setHeader("Cache-Control", "public, max-age=86400"); // 1일

        // 보안 관련 헤더 (SEO에도 영향을 줌)
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
    }

    // URL 정규화가 필요한지 확인 (예: www vs non-www, 중복 슬래시 등)
    private boolean shouldRedirectToCanonicalUrl(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String host = request.getHeader("Host");

        // 중복 슬래시 확인
        boolean hasDuplicateSlashes = requestUri.contains("//");

        // www 도메인 확인 (linkit.im을 표준으로 간주)
        boolean shouldRemoveWww = host != null && host.startsWith("www.");

        // 대문자 URI 확인 (SEO 관점에서는 소문자가 좋음)
        boolean hasUpperCase = !requestUri.equals(requestUri.toLowerCase());

        return hasDuplicateSlashes || shouldRemoveWww || hasUpperCase;
    }

    // 정규화된 URL로 리다이렉트
    private void redirectToCanonicalUrl(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String scheme = request.getScheme();
        String host = request.getHeader("Host");
        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();

        // www 제거
        if (host != null && host.startsWith("www.")) {
            host = host.substring(4);
        }

        // 중복 슬래시 제거
        requestUri = requestUri.replaceAll("/+", "/");

        // 소문자로 변환
        requestUri = requestUri.toLowerCase();

        // 새 URL 구성
        StringBuilder newUrl = new StringBuilder();
        newUrl.append(scheme).append("://").append(host).append(requestUri);
        if (queryString != null) {
            newUrl.append("?").append(queryString);
        }

        // 301 영구 리다이렉트 설정 (SEO에 유리)
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", newUrl.toString());
    }

    // 모바일 기기 감지
    private boolean isMobileDevice(String userAgent) {
        if (userAgent == null) {
            return false;
        }

        return userAgent.contains("Mobile")
                || userAgent.contains("Android")
                || userAgent.contains("iPhone")
                || userAgent.contains("iPad");
    }
}
