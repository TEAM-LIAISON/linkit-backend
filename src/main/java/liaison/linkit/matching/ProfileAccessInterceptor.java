package liaison.linkit.matching;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.global.exception.RefreshTokenException;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.login.domain.repository.RefreshTokenRepository;
import liaison.linkit.login.infrastructure.BearerAuthorizationExtractor;
import liaison.linkit.login.infrastructure.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_REQUEST;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_REFRESH_TOKEN;

@Component
@Slf4j
public class ProfileAccessInterceptor implements HandlerInterceptor {

    private static final String REFRESH_TOKEN = "refresh-token";
    private final JwtProvider jwtProvider;
    private final BearerAuthorizationExtractor extractor;
    private final RefreshTokenRepository refreshTokenRepository;

    public ProfileAccessInterceptor(JwtProvider jwtProvider, BearerAuthorizationExtractor extractor, RefreshTokenRepository refreshTokenRepository) {
        this.jwtProvider = jwtProvider;
        this.extractor = extractor;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            CheckProfileAccess access = handlerMethod.getMethodAnnotation(CheckProfileAccess.class);
            log.info("CheckProfileAccess annotation present: {}", access != null);

            if (access != null) {
                String memberId = getMemberId(request);
                log.info("Member ID: {}", memberId);

                double resumeCompletion = getResumeCompletion(memberId);
                double teamCompletion = getTeamCompletion(memberId);

                if (isAccessDenied(access, resumeCompletion, teamCompletion)) {
                    log.info("Access denied for member ID: {} - ResumeCompletion: {}, TeamCompletion: {}", memberId, resumeCompletion, teamCompletion);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return false;
                }
            }
        }
        return true;
    }

    private String getMemberId(HttpServletRequest request) {
        try {
            String refreshToken = extractRefreshToken(request.getCookies());
            String accessToken = extractor.extractAccessToken(request.getHeader("Authorization"));
            log.info("refreshToken={}", refreshToken);
            log.info("accessToken={}", accessToken);
            jwtProvider.validateTokens(new MemberTokens(refreshToken, accessToken));
            return jwtProvider.getSubject(accessToken);
        } catch (RefreshTokenException e) {
            log.error("Failed to extract user ID", e);
            throw new BadRequestException(INVALID_REQUEST);
        }
    }

    private String extractRefreshToken(final Cookie... cookies) {
        System.out.println("cookies = " + cookies);
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

    private double getResumeCompletion(final String memberId) {
        // 이력서 완성도를 계산하는 로직
        // 예: DB에서 조회하여 계산
        log.info("getTeamResume 메서드가 실행됩니다.");
        return 45.0; // 예시 값
    }

    private double getTeamCompletion(final String memberId) {
        // 팀 소개서 완성도를 계산하는 로직
        // 예: DB에서 조회하여 계산
        log.info("getTeamCompletion 메서드가 실행됩니다.");
        return 45.0; // 예시 값
    }

    private boolean isAccessDenied(CheckProfileAccess access, double resumeCompletion, double teamCompletion) {
        if (access.checkTeam() && teamCompletion < 80) {
            return true;
        } else return !access.checkTeam() && resumeCompletion < 80;
    }
}
