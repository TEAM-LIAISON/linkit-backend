package liaison.linkit.profile.browse;

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
public class ProfileBrowseAccessInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;
    private final BearerAuthorizationExtractor extractor;
    private final MemberRepository memberRepository;

//    public ProfileBrowseAccessInterceptor() {
//        this.jwtProvider = null;
//        this.extractor = null;
//        this.memberRepository = null;
//    }

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {

            // to_private 열람 요청
            final CheckBrowseToPrivateProfileAccess checkBrowseToPrivateProfileAccess =
                    handlerMethod.getMethodAnnotation(CheckBrowseToPrivateProfileAccess.class);

            // to_team 열람 요청
            final CheckBrowseToTeamProfileAccess checkBrowseToTeamProfileAccess =
                    handlerMethod.getMethodAnnotation(CheckBrowseToTeamProfileAccess.class);

            log.info("checkBrowseToPrivateProfileAccess={}", checkBrowseToPrivateProfileAccess);
            log.info("checkBrowseToTeamProfileAccess={}", checkBrowseToTeamProfileAccess);

            // 내 이력서 또는 팀 소개서에 열람 요청이 발생한 경우
            if (checkBrowseToPrivateProfileAccess != null || checkBrowseToTeamProfileAccess != null) {
                final Long memberId = getMemberId(request);
                log.info("memberId={}", memberId);

                final ProfileType profileType = getPrivateProfileType(memberId);
                log.info("profileType={}",profileType);

                final TeamProfileType teamProfileType = getTeamProfileType(memberId);
                log.info("teamProfileType={}",teamProfileType);

                // 접근 권한 판단 실행 여부
                if (!isAccessJudge(profileType, teamProfileType)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return false;
                }
            }
        }
        return true;
    }

    // 열람 권한 판단
    private boolean isAccessJudge(
            final ProfileType profileType,
            final TeamProfileType teamProfileType
    ) {
        // 열람 권한 판단
        if (ProfileType.NO_PERMISSION.equals(profileType) && TeamProfileType.NO_PERMISSION.equals(teamProfileType)) {
            log.info("내 이력서에 대한 열람 권한이 거부됩니다.");
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
