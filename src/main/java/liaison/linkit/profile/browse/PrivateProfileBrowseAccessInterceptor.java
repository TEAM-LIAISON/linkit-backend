package liaison.linkit.profile.browse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.login.infrastructure.BearerAuthorizationExtractor;
import liaison.linkit.login.infrastructure.JwtProvider;
import liaison.linkit.matching.CheckMatchingToPrivateProfileAccess;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.MemberRepository;
import liaison.linkit.member.domain.type.ProfileType;
import liaison.linkit.member.domain.type.TeamProfileType;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import static liaison.linkit.global.exception.ExceptionCode.*;

@RequiredArgsConstructor
@Component
@Slf4j
public class PrivateProfileBrowseAccessInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;
    private final BearerAuthorizationExtractor extractor;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final TeamProfileRepository teamProfileRepository;

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) throws Exception {
        if (handler instanceof HandlerMethod) {

            final HandlerMethod handlerMethod = (HandlerMethod) handler;

            // to_private 열람 요청
            final CheckBrowseToPrivateProfileAccess checkBrowseToPrivateProfileAccess =
                    handlerMethod.getMethodAnnotation(CheckBrowseToPrivateProfileAccess.class);

            // to_team 열람 요청
            final CheckBrowseToTeamProfileAccess checkBrowseToTeamProfileAccess =
                    handlerMethod.getMethodAnnotation(CheckBrowseToTeamProfileAccess.class);

            log.info("checkBrowseToPrivateProfileAccess={}", checkBrowseToPrivateProfileAccess);
            log.info("checkBrowseToTeamProfileAccess={}", checkBrowseToTeamProfileAccess);

            // 내 이력서에 열람 요청이 발생한 경우
            if (checkBrowseToPrivateProfileAccess != null) {
                final Long memberId = getMemberId(request);
                log.info("memberId={}", memberId);

                final ProfileType profileType = getPrivateProfileType(memberId);
                log.info("profileType={}",profileType);

                final TeamProfileType teamProfileType = getTeamProfileType(memberId);
                log.info("teamProfileType={}",teamProfileType);

                // 접근 권한 판단 실행 여부
//                if (isAccessDenied(checkProfileAccess, profileType, teamProfileType)) {
//                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                    return false;
//                }
            }   // 팀 소개서에 열람 요청이 발생한 경우
            else if (checkBrowseToTeamProfileAccess != null) {

            }
        }
        return true;
    }

    private Long getMemberId(HttpServletRequest request) {
        String accessToken = extractor.extractAccessToken(request.getHeader("Authorization"));
        return Long.valueOf(jwtProvider.getSubject(accessToken));
    }

    private ProfileType getPrivateProfileType(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));
        return member.getProfileType();
    }

    private TeamProfileType getTeamProfileType(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));
        return member.getTeamProfileType();
    }

//    private double getPrivateProfileCompletion(Long memberId) {
//        // 이 메서드는 데이터베이스에서 개인 프로필 완성도를 조회합니다.
//        return 80.0; // 예시 완성도
//    }
//
//    private double getTeamProfileCompletion(Long memberId) {
//        // 이 메서드는 데이터베이스에서 팀 프로필 완성도를 조회합니다.
//        return 85.0; // 예시 완성도
//    }


    // 열람이나 매칭은 completion으로 관리
    private boolean isAccessDenied(
            final CheckMatchingToPrivateProfileAccess checkProfileAccess,
            final ProfileType profileType,
            final TeamProfileType teamProfileType
    ) {

        // 1. 내 이력서 권한 판단

        // 내 이력서 열람 불가능인 경우
        if (ProfileType.NO_PERMISSION.equals(profileType)) {

        }

        // 2. 팀 소개서 권한 판단
        if (checkProfileAccess.checkPrivateMatchingAccess()) {
            log.info("checkProfileAccess.checkTeam() 메서드가 true 입니다.");
            return true;
        }
        log.info("checkProfileAccess.checkTeam() 메서드가 false 입니다.");
        return false;
    }

    // 내 이력서 조회
    private Profile getProfileByMember(final Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_MEMBER_ID));
    }

    // 팀 소개서 조회
    private TeamProfile getTeamProfile(final Long memberId) {
        return teamProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID));
    }
}
