package liaison.linkit.login.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import liaison.linkit.common.exception.RefreshTokenExpiredException;
import liaison.linkit.login.business.AccountMapper;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.login.domain.OauthProvider;
import liaison.linkit.login.domain.OauthProviders;
import liaison.linkit.login.domain.OauthUserInfo;
import liaison.linkit.login.domain.RefreshToken;
import liaison.linkit.login.domain.repository.RefreshTokenRepository;
import liaison.linkit.login.exception.DuplicateEmailRequestException;
import liaison.linkit.login.exception.QuitBadRequestException;
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
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.log.TeamLogCommandAdapter;
import liaison.linkit.team.implement.team.TeamCommandAdapter;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberInvitationCommandAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberInvitationQueryAdapter;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementRepository;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementCommandAdapter;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberCommandAdapter;
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
        // 1. 회원 정보 조회
        final Member member = memberQueryAdapter.findById(memberId);
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);
        log.info("Deleting account {}", memberId);

        // 1. 내가 오너로 속한 팀이 1개라도 있는가? 그 팀에 다른 관리자가 있는가? (통과했다면 팀 관련된 모든 정보를 삭제 가능)
        if (teamMemberQueryAdapter.existsTeamOwnerAndOtherManagerByMemberId(memberId)) {
            throw QuitBadRequestException.EXCEPTION;
        }

        // 탈퇴하려는 회원이 속한 삭제 가능한 팀들
        Set<Team> deletableTeams = teamMemberQueryAdapter.getAllDeletableTeamsByMemberId(member.getId());
        log.info("Deleting teams {}", deletableTeams);

        // 삭제 가능한 팀의 ID 리스트
        List<Long> deletableTeamIds = deletableTeams.stream()
                .map(Team::getId)
                .toList();
        log.info("Deleting teams {}", deletableTeamIds);

        Set<TeamMemberAnnouncement> deletableTeamMemberAnnouncements = teamMemberAnnouncementQueryAdapter.getAllDeletableTeamMemberAnnouncementsByTeamIds(deletableTeamIds);
        log.info("Deleting teams {}", deletableTeamMemberAnnouncements);

        List<Long> deletableAnnouncementIds = deletableTeamMemberAnnouncements.stream()
                .map(TeamMemberAnnouncement::getId)
                .toList();
        log.info("Deleting teams {}", deletableAnnouncementIds);

        List<String> deletableTeamCodes = deletableTeams.stream()
                .map(Team::getTeamCode)
                .toList();

        log.info("Deleting teams {}", deletableTeamCodes);
        // [2. 매칭 데이터 삭제]
        matchingCommandAdapter.deleteAllBySenderProfile(member.getEmailId());
        matchingCommandAdapter.deleteAllBySenderTeamCodes(deletableTeamCodes);
        log.info("Deleting teams {}", deletableTeams);
        matchingCommandAdapter.deleteAllByReceiverProfile(member.getEmailId());
        matchingCommandAdapter.deleteAllByReceiverTeamCodes(deletableTeamCodes);
        matchingCommandAdapter.deleteAllByReceiverAnnouncements(deletableAnnouncementIds);

        log.info("Deleting teams {}", deletableTeams);

        // [3. 스크랩 데이터 삭제] (내가 스크랩을 한 데이터 + 나에게 스크랩을 한 데이터)
        profileScrapCommandAdapter.deleteAllByMemberId(member.getId());
        profileScrapCommandAdapter.deleteAllByProfileId(profile.getId());
        log.info("Deleting teams {}", deletableTeams);

        teamScrapCommandAdapter.deleteAllByMemberId(member.getId());
        teamScrapCommandAdapter.deleteAllByTeamIds(deletableTeamIds);
        log.info("Deleting teams {}", deletableTeams);

        announcementScrapCommandAdapter.deleteAllByMemberId(member.getId());
        announcementScrapCommandAdapter.deleteAllByAnnouncementIds(deletableAnnouncementIds);
        log.info("Deleting teams {}", deletableTeams);

        // [4. 팀 초대 데이터 삭제]
        teamMemberInvitationCommandAdapter.deleteAllByTeamIds(deletableTeamIds);
        log.info("Deleting teams {}", deletableTeams);

        teamMemberCommandAdapter.deleteAllTeamMemberByMember(memberId);
        // [5. 채팅방 데이터 삭제]

        // 4. 알림 데이터 삭제
        notificationCommandAdapter.deleteAllByReceiverMemberId(memberId);
        log.info("Deleting teams {}", deletableTeams);

        // 5. 토큰 데이터 삭제
        removeRefreshToken(refreshToken);
        log.info("Deleting teams {}", deletableTeams);

        for (Long deletableTeamId : deletableTeamIds) {
            teamLogCommandAdapter.deleteAllTeamLogs(deletableTeamId);
        }

        // 팀 삭제
        for (Team team : deletableTeams) {
            team.changeStatusToDeleted();
            teamCommandAdapter.updateTeam(team); // 변경된 상태를 DB에 반영
        }

        profileLogCommandAdapter.deleteAllProfileLogs(profile.getId());

        // 모든 공고 삭제
        if (!deletableAnnouncementIds.isEmpty()) {
            teamMemberAnnouncementCommandAdapter.deleteAllByIds(deletableAnnouncementIds);
        }

        // 6. 프로필 삭제
        profileCommandAdapter.deleteByMemberId(memberId);

        // 7. 회원 삭제
        memberCommandAdapter.deleteByMemberId(memberId);
    }

    private void removeRefreshToken(final String refreshToken) {    // 리프레시 토큰을 삭제한다
        refreshTokenRepository.deleteById(refreshToken);
    }
}
