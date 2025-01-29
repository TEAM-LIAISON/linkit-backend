package liaison.linkit.login.service;

import java.util.Optional;
import liaison.linkit.common.exception.RefreshTokenExpiredException;
import liaison.linkit.global.DeleteUtil;
import liaison.linkit.login.business.AccountMapper;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.login.domain.OauthProvider;
import liaison.linkit.login.domain.OauthProviders;
import liaison.linkit.login.domain.OauthUserInfo;
import liaison.linkit.login.domain.RefreshToken;
import liaison.linkit.login.domain.repository.RefreshTokenRepository;
import liaison.linkit.login.exception.DuplicateEmailRequestException;
import liaison.linkit.login.infrastructure.BearerAuthorizationExtractor;
import liaison.linkit.login.infrastructure.JwtProvider;
import liaison.linkit.login.presentation.dto.AccountResponseDTO;
import liaison.linkit.matching.implement.MatchingCommandAdapter;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.domain.type.Platform;
import liaison.linkit.member.exception.member.FailMemberGenerateException;
import liaison.linkit.member.implement.MemberBasicInformCommandAdapter;
import liaison.linkit.member.implement.MemberCommandAdapter;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.notification.business.NotificationMapper;
import liaison.linkit.notification.implement.NotificationCommandAdapter;
import liaison.linkit.notification.service.NotificationService;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.implement.log.ProfileLogCommandAdapter;
import liaison.linkit.profile.implement.profile.ProfileCommandAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.scrap.implement.announcementScrap.AnnouncementScrapCommandAdapter;
import liaison.linkit.scrap.implement.announcementScrap.AnnouncementScrapQueryAdapter;
import liaison.linkit.scrap.implement.profileScrap.ProfileScrapCommandAdapter;
import liaison.linkit.scrap.implement.teamScrap.TeamScrapCommandAdapter;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementRepository;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementCommandAdapter;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.log.TeamLogCommandAdapter;
import liaison.linkit.team.implement.team.TeamCommandAdapter;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberCommandAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberInvitationCommandAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberInvitationQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LoginService {
    private static final int MAX_TRY_COUNT = 5;

    private final OauthProviders oauthProviders;
    private final JwtProvider jwtProvider;
    private final BearerAuthorizationExtractor bearerExtractor;

    private final AccountMapper accountMapper;

    private final MemberQueryAdapter memberQueryAdapter;
    private final MemberCommandAdapter memberCommandAdapter;
    private final MemberBasicInformCommandAdapter memberBasicInformCommandAdapter;

    private final RefreshTokenRepository refreshTokenRepository;
    private final ProfileRepository profileRepository;
    private final ProfileQueryAdapter profileQueryAdapter;
    private final ProfileCommandAdapter profileCommandAdapter;

    private final TeamMemberInvitationQueryAdapter teamMemberInvitationQueryAdapter;
    private final TeamMemberInvitationCommandAdapter teamMemberInvitationCommandAdapter;
    private final NotificationCommandAdapter notificationCommandAdapter;
    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    private final TeamMemberAnnouncementRepository teamMemberAnnouncementRepository;
    private final TeamScrapCommandAdapter teamScrapCommandAdapter;
    private final ProfileScrapCommandAdapter profileScrapCommandAdapter;
    private final TeamMemberAnnouncementCommandAdapter teamMemberAnnouncementCommandAdapter;
    private final TeamMemberCommandAdapter teamMemberCommandAdapter;
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final MatchingCommandAdapter matchingCommandAdapter;
    private final TeamQueryAdapter teamQueryAdapter;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final TeamCommandAdapter teamCommandAdapter;
    private final AnnouncementScrapQueryAdapter announcementScrapQueryAdapter;
    private final AnnouncementScrapCommandAdapter announcementScrapCommandAdapter;
    private final TeamLogCommandAdapter teamLogCommandAdapter;
    private final ProfileLogCommandAdapter profileLogCommandAdapter;
    private final DeleteUtil deleteUtil;

    // 회원이 로그인한다
    public AccountResponseDTO.LoginServiceResponse login(final String providerName, final String code) {
        final OauthProvider provider = oauthProviders.mapping(providerName);

        final OauthUserInfo oauthUserInfo = provider.getUserInfo(code);

        final Member member = findOrCreateMember(
                oauthUserInfo.getSocialLoginId(),
                oauthUserInfo.getEmail(),
                provider.getPlatform(providerName));

        final boolean isMemberBasicInform = member.isCreateMemberBasicInform();

        // 토큰을 생성한다
        final MemberTokens memberTokens = jwtProvider.generateLoginToken(member.getId().toString());

        // 생성한 토큰 중 refreshToken을 레디스에 저장한다
        final RefreshToken savedRefreshToken = new RefreshToken(memberTokens.getRefreshToken(), member.getId());
        refreshTokenRepository.save(savedRefreshToken);

        return accountMapper.toLogin(
                memberTokens,
                member.getEmail(),
                member.getEmailId(),
                isMemberBasicInform);
    }

    private Member findOrCreateMember(final String socialLoginId, final String email, final Platform platform) {
        final Optional<Member> member = memberQueryAdapter.findBySocialLoginId(socialLoginId);
        return member.orElseGet(() -> createMember(socialLoginId, email, platform));
    }

    @Transactional
    public Member createMember(final String socialLoginId, final String email, final Platform platform) {
        int tryCount = 0;
        while (tryCount < MAX_TRY_COUNT) {
            if (!memberQueryAdapter.existsByEmail(email)) {
                final Member member = memberCommandAdapter
                        .create(new Member(socialLoginId, email, null, null, platform));

                memberBasicInformCommandAdapter.create(new MemberBasicInform(
                        null, member, null, null, false, false, false, false));

                profileCommandAdapter.create(new Profile(
                        null, member, null, false, 0, false, false, false, false, false, false, false, false));

                return member;
            } else if (memberQueryAdapter.existsByEmail(email)) {
                throw DuplicateEmailRequestException.EXCEPTION;
            }
            tryCount += 1;
        }
        throw FailMemberGenerateException.EXCEPTION;
    }

    public AccountResponseDTO.RenewTokenResponse renewalAccessToken(
            final String refreshTokenRequest, final String authorizationHeader) {
        // 기존에 사용하던 accessToken을 추출한다
        final String accessToken = bearerExtractor.extractAccessToken(authorizationHeader);
        return getRenewalToken(refreshTokenRequest);
    }

    private AccountResponseDTO.RenewTokenResponse getRenewalToken(final String refreshTokenRequest) {
        final RefreshToken refreshToken = refreshTokenRepository.findById(refreshTokenRequest)
                .orElseThrow(() -> RefreshTokenExpiredException.EXCEPTION);
        return accountMapper
                .toRenewTokenResponse(jwtProvider.regenerateAccessToken(refreshToken.getMemberId().toString()));
    }

    // 회원이 로그아웃한다
    public AccountResponseDTO.LogoutResponse logout(final Long memberId, final String refreshToken) {
        removeRefreshToken(refreshToken);
        return accountMapper.toLogout();
    }


    // 회원이 서비스를 탈퇴한다
    public AccountResponseDTO.QuitAccountResponse quitAccount(final Long memberId, final String refreshToken) {
        deleteAccount(memberId, refreshToken);
        return accountMapper.toQuitAccount();
    }

    // 수정 필요
    public void deleteAccount(final Long memberId, final String refreshToken) {
        deleteUtil.quitAccount(memberId, refreshToken);
    }

    private void removeRefreshToken(final String refreshToken) {    // 리프레시 토큰을 삭제한다
        refreshTokenRepository.deleteById(refreshToken);
    }
}
