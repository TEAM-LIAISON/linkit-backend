package liaison.linkit.matching;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.login.infrastructure.BearerAuthorizationExtractor;
import liaison.linkit.login.infrastructure.JwtProvider;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.MemberRepository;
import liaison.linkit.member.domain.type.ProfileType;
import liaison.linkit.member.domain.type.TeamProfileType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;

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

            // 내 이력서에 매칭 요청이 발생한 경우
            if (checkMatchingToPrivateProfileAccess != null) {
                final Long memberId = getMemberId(request);
                log.info("memberId={}", memberId);

                final ProfileType profileType = getPrivateProfileType(memberId);
                log.info("profileType={}",profileType);

                final TeamProfileType teamProfileType = getTeamProfileType(memberId);
                log.info("teamProfileType={}",teamProfileType);

                if (!matchingAccessJudge(profileType, teamProfileType)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return false;
                }
            } else if (checkMatchingToTeamProfileAccess != null) {
                final Long memberId = getMemberId(request);
                log.info("memberId={}", memberId);

                final ProfileType profileType = getPrivateProfileType(memberId);
                log.info("profileType={}",profileType);

                final TeamProfileType teamProfileType = getTeamProfileType(memberId);
                log.info("teamProfileType={}",teamProfileType);

                if (!matchingAccessJudge(profileType, teamProfileType)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return false;
                }
            }
        }
        return true;
    }

    private boolean matchingAccessJudge(
            final ProfileType profileType,
            final TeamProfileType teamProfileType
    ) {
        if (ProfileType.NO_PERMISSION.equals(profileType) && TeamProfileType.NO_PERMISSION.equals(teamProfileType)) {
            return false;
        } else if (ProfileType.NO_PERMISSION.equals(profileType) && TeamProfileType.ALLOW_BROWSE.equals(teamProfileType)) {
            return false;
        } else if (ProfileType.ALLOW_BROWSE.equals(profileType) && TeamProfileType.NO_PERMISSION.equals(teamProfileType)) {
            return false;
        } else if (ProfileType.ALLOW_BROWSE.equals(profileType) && TeamProfileType.ALLOW_BROWSE.equals(teamProfileType)) {
            return false;
        } else return true;
    }

    // 회원 ID 조회
    private Long getMemberId(HttpServletRequest request) {
        String accessToken = extractor.extractAccessToken(request.getHeader("Authorization"));
        return Long.valueOf(jwtProvider.getSubject(accessToken));
    }

    // 내 이력서 타입 반환
    private ProfileType getPrivateProfileType(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));
        return member.getProfileType();
    }

    // 팀 소개서 타입 반환
    private TeamProfileType getTeamProfileType(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));
        return member.getTeamProfileType();
    }
}
