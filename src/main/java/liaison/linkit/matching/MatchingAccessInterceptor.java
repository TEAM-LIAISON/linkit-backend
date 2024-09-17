package liaison.linkit.matching;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import liaison.linkit.login.infrastructure.BearerAuthorizationExtractor;
import liaison.linkit.login.infrastructure.JwtProvider;
import liaison.linkit.member.domain.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
@Slf4j
public class MatchingAccessInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;
    private final BearerAuthorizationExtractor extractor;
    private final MemberRepository memberRepository;

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {

            final CheckMatchingToPrivateProfileAccess checkMatchingToPrivateProfileAccess =
                    handlerMethod.getMethodAnnotation(CheckMatchingToPrivateProfileAccess.class);

            final CheckMatchingToTeamProfileAccess checkMatchingToTeamProfileAccess =
                    handlerMethod.getMethodAnnotation(CheckMatchingToTeamProfileAccess.class);

            log.info("checkMatchingToPrivateProfileAccess={}", checkMatchingToPrivateProfileAccess);
            log.info("checkMatchingToTeamProfileAccess={}", checkMatchingToTeamProfileAccess);
        }
        return true;
    }


    // 회원 ID 조회
    private Long getMemberId(HttpServletRequest request) {
        String accessToken = extractor.extractAccessToken(request.getHeader("Authorization"));
        return Long.valueOf(jwtProvider.getSubject(accessToken));
    }
}
