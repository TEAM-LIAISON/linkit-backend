package liaison.linkit.login.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.login.domain.*;
import liaison.linkit.login.domain.repository.RefreshTokenRepository;
import liaison.linkit.login.dto.MemberTokensAndOnBoardingStepInform;
import liaison.linkit.login.infrastructure.BearerAuthorizationExtractor;
import liaison.linkit.login.infrastructure.JwtProvider;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.MemberRepository;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private static final int MAX_TRY_COUNT = 5;

    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final TeamProfileRepository teamProfileRepository;
    private final OauthProviders oauthProviders;
    private final JwtProvider jwtProvider;
    private final BearerAuthorizationExtractor bearerExtractor;

    // 내 이력서 조회
    private Profile getProfileByMember(final Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_MEMBER_ID));
    }

    // 팀 소개서 정보를 가져온다. (1개만 저장되어 있음)
    private TeamProfile getTeamProfile(final Long memberId) {
        return teamProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID));
    }

    public MemberTokensAndOnBoardingStepInform login(final String providerName, final String code) {
        final OauthProvider provider = oauthProviders.mapping(providerName);
        final OauthUserInfo oauthUserInfo = provider.getUserInfo(code);

        // 소셜로그인 ID와 이메일 정보는 해당 플랫폼으로부터 가져옴
        final Member member = findOrCreateMember(
                oauthUserInfo.getSocialLoginId(),
                oauthUserInfo.getEmail()
        );

        // 멤버 테이블에서 기본 정보 입력 여부를 조회함
        final boolean existMemberBasicInform = member.isExistMemberBasicInform();
        log.info("loginService login method memberId={}", member.getId());
        final Profile profile = getProfileByMember(member.getId());
        final TeamProfile teamProfile = getTeamProfile(member.getId());
        final boolean existDefaultPrivateProfile = profile.getExistDefaultPrivateProfile();
        log.info("existDefaultPrivateProfile={}", existDefaultPrivateProfile);
        final boolean existDefaultTeamProfile = teamProfile.getExistDefaultTeamProfile();
        log.info("existDefaultTeamProfile={}",existDefaultTeamProfile);

        final boolean existDefaultProfile = (existDefaultPrivateProfile || existDefaultTeamProfile);
        final MemberTokens memberTokens = jwtProvider.generateLoginToken(member.getId().toString());

        // 리프레시 토큰 저장
        final RefreshToken savedRefreshToken = new RefreshToken(memberTokens.getRefreshToken(), member.getId());
        refreshTokenRepository.save(savedRefreshToken);

        return new MemberTokensAndOnBoardingStepInform(
                memberTokens.getAccessToken(),
                memberTokens.getRefreshToken(),
                oauthUserInfo.getEmail(),
                existMemberBasicInform,
                existDefaultProfile
        );
    }

    private Member findOrCreateMember(final String socialLoginId, final String email) {
        return memberRepository.findBySocialLoginId(socialLoginId)
                .orElseGet(() -> createMember(socialLoginId, email));
    }

    private Member createMember(final String socialLoginId, final String email) {
        int tryCount = 0;
        while (tryCount < MAX_TRY_COUNT) {
            if (!memberRepository.existsByEmail(email)) {
                // 만약 이메일에 의해서 존재하지 않는 회원임이 판단된다면
                Member member = memberRepository.save(new Member(socialLoginId, email, null));
                log.info("memberId={}", member.getId());

                // 내 이력서는 자동으로 생성된다. -> 미니 프로필도 함께 생성되어야 한다.
                Profile savedProfile = profileRepository.save(new Profile(member, 0));
                log.info("savedProfile.ID={}", savedProfile.getId());

                TeamProfile savedTeamProfile = teamProfileRepository.save(new TeamProfile(member, 0));
                log.info("savedTeamProfile.ID={}", savedTeamProfile.getId());

                return member;
            }
            tryCount += 1;
        }
        throw new AuthException(FAIL_TO_GENERATE_MEMBER);
    }

    public String renewalAccessToken(final String refreshTokenRequest, final String authorizationHeader) {
        final String accessToken = bearerExtractor.extractAccessToken(authorizationHeader);
        if (jwtProvider.isValidRefreshAndInvalidAccess(refreshTokenRequest, accessToken)) {
            final RefreshToken refreshToken = refreshTokenRepository.findById(refreshTokenRequest)
                    .orElseThrow(() -> new AuthException(INVALID_REFRESH_TOKEN));
            return jwtProvider.regenerateAccessToken(refreshToken.getMemberId().toString());
        }
        if (jwtProvider.isValidRefreshAndValidAccess(refreshTokenRequest, accessToken)) {
            return accessToken;
        }
        throw new AuthException(FAIL_TO_VALIDATE_TOKEN);
    }

    public void removeRefreshToken(final String refreshToken) {
        refreshTokenRepository.deleteById(refreshToken);
    }

    // 수정 필요
    public void deleteAccount(final Long memberId) {
        memberRepository.deleteByMemberId(memberId);
    }


}
