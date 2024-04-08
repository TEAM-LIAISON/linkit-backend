package liaison.linkit.login.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.login.domain.*;
import liaison.linkit.login.domain.repository.RefreshTokenRepository;
import liaison.linkit.login.infrastructure.BearerAuthorizationExtractor;
import liaison.linkit.login.infrastructure.JwtProvider;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {
    private static final int MAX_TRY_COUNT = 5;
    private static final int FOUR_DIGIT_RANGE = 10000;

    private final RefreshTokenRepository refreshTokenRepository;

    private final MemberRepository memberRepository;

    private final OauthProviders oauthProviders;
    private final JwtProvider jwtProvider;
    private final BearerAuthorizationExtractor bearerExtractor;
    private final ApplicationEventPublisher publisher;

    public MemberTokens login(final String providerName, final String code) {
        final OauthProvider provider = oauthProviders.mapping(providerName);
        final OauthUserInfo oauthUserInfo = provider.getUserInfo(code);

        final Member member = findOrCreateMember(
                oauthUserInfo.getSocialLoginId(),
                oauthUserInfo.getEmail()
        );
        final MemberTokens memberTokens = jwtProvider.generateLoginToken(member.getId().toString());
        final RefreshToken savedRefreshToken = new RefreshToken(memberTokens.getRefreshToken(), member.getId());
        refreshTokenRepository.save(savedRefreshToken);
        return memberTokens;
    }

    private Member findOrCreateMember(final String socialLoginId, final String email) {
        return memberRepository.findBySocialLoginId(socialLoginId)
                .orElseGet(() -> createMember(socialLoginId, email));
    }

    private Member createMember(final String socialLoginId, final String email) {
        int tryCount = 0;
        while (tryCount < MAX_TRY_COUNT) {
            if (!memberRepository.existsByEmail(email)) {
                return memberRepository.save(new Member(socialLoginId, email, null, null));
            }
            tryCount += 1;
        }
        throw new AuthException(FAIL_TO_GENERATE_RANDOM_NICKNAME);
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

//    계정 삭제 로직 추가 구현 필요
//    public void deleteAccount(final Long memberId) {
//        final List<Long> tripIds = customTripRepository.findTripIdsByMemberId(memberId);
//        publishedTripRepository.deleteByTripIds(tripIds);
//        sharedTripRepository.deleteByTripIds(tripIds);
//        memberRepository.deleteByMemberId(memberId);
//        publisher.publishEvent(new MemberDeleteEvent(tripIds, memberId));
//    }
}
