package liaison.linkit.matching;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import liaison.linkit.login.infrastructure.BearerAuthorizationExtractor;
import liaison.linkit.login.infrastructure.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
@Slf4j
public class ProfileAccessInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;
    private final BearerAuthorizationExtractor extractor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            CheckProfileAccess access = handlerMethod.getMethodAnnotation(CheckProfileAccess.class);
            if (access != null) {
                Long memberId = getMemberId(request);
                double resumeCompletion = getPrivateProfileCompletion(memberId);
                double teamCompletion = getTeamProfileCompletion(memberId);

                if (isAccessDenied(access, resumeCompletion, teamCompletion)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return false;
                }
            }
        }
        return true;
    }

    private Long getMemberId(HttpServletRequest request) {
        String accessToken = extractor.extractAccessToken(request.getHeader("Authorization"));
        return Long.valueOf(jwtProvider.getSubject(accessToken));
    }

    private double getPrivateProfileCompletion(Long memberId) {
        // 이 메서드는 데이터베이스에서 개인 프로필 완성도를 조회합니다.
        return 80.0; // 예시 완성도
    }

    private double getTeamProfileCompletion(Long memberId) {
        // 이 메서드는 데이터베이스에서 팀 프로필 완성도를 조회합니다.
        return 85.0; // 예시 완성도
    }

    private boolean isAccessDenied(CheckProfileAccess access, double resumeCompletion, double teamCompletion) {
        if (access.checkTeam()) {
            return teamCompletion < 80;
        }
        return resumeCompletion < 80;
    }
}
