package liaison.linkit.login.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.login.domain.*;
import liaison.linkit.login.domain.repository.RefreshTokenRepository;
import liaison.linkit.login.dto.MemberTokensAndOnBoardingStepInform;
import liaison.linkit.login.infrastructure.BearerAuthorizationExtractor;
import liaison.linkit.login.infrastructure.JwtProvider;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.MemberRepository;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.ProfileRepository;
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

    private final OauthProviders oauthProviders;
    private final JwtProvider jwtProvider;
    private final BearerAuthorizationExtractor bearerExtractor;

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

        // 멤버 테이블에서 내 이력서 또는 팀 소개서의 작성 여부를 조회해야함.
        final boolean existOnBoardingProfile = member.isExistOnBoardingProfile();

        final MemberTokens memberTokens = jwtProvider.generateLoginToken(member.getId().toString());

        // 리프레시 토큰 저장
        final RefreshToken savedRefreshToken = new RefreshToken(memberTokens.getRefreshToken(), member.getId());
        refreshTokenRepository.save(savedRefreshToken);

        return new MemberTokensAndOnBoardingStepInform(
                memberTokens.getAccessToken(),
                memberTokens.getRefreshToken(),
                oauthUserInfo.getEmail(),
                existMemberBasicInform,
                existOnBoardingProfile
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
                Member member = memberRepository.save(new Member(socialLoginId, email, null));
                profileRepository.save(new Profile(member, 0,"자기소개를 입력해주세요"));
                log.info("memberId={}", member.getId());

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
