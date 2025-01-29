package liaison.linkit.global;

import java.util.List;
import java.util.Set;
import liaison.linkit.login.domain.repository.RefreshTokenRepository;
import liaison.linkit.login.exception.QuitBadRequestException;
import liaison.linkit.matching.implement.MatchingCommandAdapter;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberCommandAdapter;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.notification.implement.NotificationCommandAdapter;
import liaison.linkit.profile.business.mapper.ProfilePortfolioMapper;
import liaison.linkit.profile.business.service.ProfilePortfolioService;
import liaison.linkit.profile.domain.portfolio.ProfilePortfolio;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.implement.activity.ProfileActivityCommandAdapter;
import liaison.linkit.profile.implement.awards.ProfileAwardsCommandAdapter;
import liaison.linkit.profile.implement.education.ProfileEducationCommandAdapter;
import liaison.linkit.profile.implement.license.ProfileLicenseCommandAdapter;
import liaison.linkit.profile.implement.link.ProfileLinkCommandAdapter;
import liaison.linkit.profile.implement.log.ProfileLogCommandAdapter;
import liaison.linkit.profile.implement.portfolio.ProfilePortfolioCommandAdapter;
import liaison.linkit.profile.implement.portfolio.ProfilePortfolioQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileCommandAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.implement.skill.ProfileSkillCommandAdapter;
import liaison.linkit.profile.implement.skill.ProfileSkillQueryAdapter;
import liaison.linkit.scrap.implement.announcementScrap.AnnouncementScrapCommandAdapter;
import liaison.linkit.scrap.implement.profileScrap.ProfileScrapCommandAdapter;
import liaison.linkit.scrap.implement.teamScrap.TeamScrapCommandAdapter;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementCommandAdapter;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.history.TeamHistoryCommandAdapter;
import liaison.linkit.team.implement.log.TeamLogCommandAdapter;
import liaison.linkit.team.implement.product.TeamProductCommandAdapter;
import liaison.linkit.team.implement.team.TeamCommandAdapter;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberCommandAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberInvitationCommandAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DeleteUtil {
    private final MemberQueryAdapter memberQueryAdapter;
    private final ProfileQueryAdapter profileQueryAdapter;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final MatchingCommandAdapter matchingCommandAdapter;
    private final ProfileScrapCommandAdapter profileScrapCommandAdapter;
    private final TeamScrapCommandAdapter teamScrapCommandAdapter;
    private final AnnouncementScrapCommandAdapter announcementScrapCommandAdapter;
    private final TeamMemberInvitationCommandAdapter teamMemberInvitationCommandAdapter;
    private final TeamMemberCommandAdapter teamMemberCommandAdapter;
    private final NotificationCommandAdapter notificationCommandAdapter;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TeamCommandAdapter teamCommandAdapter;
    private final TeamLogCommandAdapter teamLogCommandAdapter;
    private final ProfileLogCommandAdapter profileLogCommandAdapter;
    private final TeamMemberAnnouncementCommandAdapter teamMemberAnnouncementCommandAdapter;
    private final ProfileCommandAdapter profileCommandAdapter;
    private final MemberCommandAdapter memberCommandAdapter;
    private final TeamQueryAdapter teamQueryAdapter;
    private final TeamProductCommandAdapter teamProductCommandAdapter;
    private final TeamHistoryCommandAdapter teamHistoryCommandAdapter;
    private final ProfileLinkCommandAdapter profileLinkCommandAdapter;
    private final ProfileLicenseCommandAdapter profileLicenseCommandAdapter;
    private final ProfileAwardsCommandAdapter profileAwardsCommandAdapter;
    private final ProfileEducationCommandAdapter profileEducationCommandAdapter;
    private final ProfilePortfolioMapper profilePortfolioMapper;
    private final ProfilePortfolioCommandAdapter profilePortfolioCommandAdapter;
    private final ProfileActivityCommandAdapter profileActivityCommandAdapter;
    private final ProfileSkillQueryAdapter profileSkillQueryAdapter;
    private final ProfileSkillCommandAdapter profileSkillCommandAdapter;
    private final ProfilePortfolioQueryAdapter profilePortfolioQueryAdapter;
    private final ProfilePortfolioService profilePortfolioService;
    // 회원 탈퇴 케이스

    public void quitAccount(final Long memberId, final String refreshToken) {
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

        List<String> deletableTeamCodes = deletableTeams.stream()
                .map(Team::getTeamCode)
                .toList();

        // ==================================================================================================================================================================

        // [2. 매칭 데이터 삭제]
        matchingCommandAdapter.deleteAllBySenderProfile(member.getEmailId());
        matchingCommandAdapter.deleteAllByReceiverProfile(member.getEmailId());

        // [3. 스크랩 데이터 삭제] (내가 스크랩을 한 데이터 + 나에게 스크랩을 한 데이터)
        profileScrapCommandAdapter.deleteAllByMemberId(member.getId());
        profileScrapCommandAdapter.deleteAllByProfileId(profile.getId());

        deleteTeamMemberByMember(memberId);

        // 4. 알림 데이터 삭제
        notificationCommandAdapter.deleteAllByReceiverMemberId(memberId);

        // 토큰 데이터 삭제
        refreshTokenRepository.deleteById(refreshToken);

        // 프로필 관련 모든 데이터 삭제
        deleteProfile(profile);

        // 팀 관련 모든 데이터 삭제
        for (String teamCode : deletableTeamCodes) {
            deleteTeam(teamCode);
        }

        // 6. 프로필 삭제
        profileCommandAdapter.deleteByMemberId(memberId);

        // 7. 회원 삭제
        memberCommandAdapter.deleteByMemberId(memberId);
    }

    private void deleteProfile(final Profile profile) {
        // profile 관련 모든 데이터 삭제

        // 프로필 로그 삭제
        profileLogCommandAdapter.deleteAllProfileLogs(profile.getId());

        // 1. 스킬 데이터 삭제
        profileSkillCommandAdapter.removeProfileSkillsByProfileId(profile.getId());

        // 2. 이력 데이터 삭제
        profileActivityCommandAdapter.removeProfileActivitiesByProfileId(profile.getId());

        // 3. 포트폴리오 데이터 삭제
        List<ProfilePortfolio> profilePortfolios = profilePortfolioQueryAdapter.getProfilePortfolios(profile.getId());
        final List<Long> deletablePortfolioIds = profilePortfolios.stream()
                .map(ProfilePortfolio::getId)
                .toList();

        for (Long portfolioId : deletablePortfolioIds) {
            profilePortfolioService.removeProfilePortfolio(profile.getMember().getId(), portfolioId);
        }

        // 4. 학력 데이터 삭제
        profileEducationCommandAdapter.removeProfileEducationsByProfileId(profile.getId());

        // 5. 수상 데이터 삭제
        profileAwardsCommandAdapter.removeProfileAwardsByProfileId(profile.getId());

        // 6. 자격증 데이터 삭제
        profileLicenseCommandAdapter.removeProfileLicensesByProfileId(profile.getId());

        // 7. 링크 데이터 삭제
        profileLinkCommandAdapter.removeProfileLinksByProfileId(profile.getId());
    }

    public void deleteTeam(final String teamCode) {
        final Team team = teamQueryAdapter.findByTeamCode(teamCode);
        final Set<TeamMemberAnnouncement> deletableTeamMemberAnnouncements = teamMemberAnnouncementQueryAdapter.getAllDeletableTeamMemberAnnouncementsByTeamId(team.getId());
        final List<Long> deletableAnnouncementIds = deletableTeamMemberAnnouncements.stream()
                .map(TeamMemberAnnouncement::getId)
                .toList();

        // [1. 팀 로그 삭제]
        deleteTeamLog(team.getId());

        // [2. 팀 구성원 삭제]
        deleteTeamMemberByTeam(team.getId());

        // [3. 팀 초대 구성원 삭제]
        deleteTeamMemberInvitationByTeam(team.getId());

        // [5. 팀 스크랩 데이터 삭제]
        deleteTeamScrapByTeamId(team.getId());
        deleteAnnouncementScrapByAnnouncement(deletableAnnouncementIds);

        // [6. 매칭 요청 데이터 삭제]
        deleteMatchingSenderTypeTeam(team.getTeamCode());
        deleteMatchingReceiverTypeTeam(team.getTeamCode());
        deleteMatchingReceiverTypeAnnouncement(deletableAnnouncementIds);

        // 프로덕트 데이터 삭제
        deleteAllTeamProducts(team.getId());

        // 연혁 데이터 삭제
        deleteAllTeamHistories(team.getId());

        // [4. 모집 공고 삭제]
        deleteAllTeamMemberAnnouncements(deletableAnnouncementIds);

        // 팀 삭제
        deleteTeamWithTeamCode(team.getTeamCode());
    }

    // 해당 회원의 모든 프로필 로그들 전체 삭제
    private void deleteProfileLogs(final Long profileId) {
        profileLogCommandAdapter.deleteAllProfileLogs(profileId);
    }

    private void deleteTeamWithTeamCode(final String teamCode) {
        teamCommandAdapter.deleteTeam(teamCode);
    }

    // 해당하는 팀 아이디들의 모든 로그들 전체 삭제
    private void deleteTeamLogs(final List<Long> deletableTeamIds) {
        for (Long deletableTeamId : deletableTeamIds) {
            deleteTeamLog(deletableTeamId);
        }
    }

    // 해당하는 팀 아이디의 모든 로그 삭제
    private void deleteTeamLog(final Long deletableTeamId) {
        teamLogCommandAdapter.deleteAllTeamLogs(deletableTeamId);
    }

    // 스크랩 당한 팀이 삭제 될 때
    private void deleteTeamScrapByTeamId(final Long deletableTeamId) {
        teamScrapCommandAdapter.deleteAllByTeamId(deletableTeamId);
    }

    // 스크랩 당한 공고가 삭제 될 때
    private void deleteAnnouncementScrapByAnnouncement(final List<Long> announcementIds) {
        announcementScrapCommandAdapter.deleteAllByAnnouncementIds(announcementIds);
    }

    // 어떤 팀들을 스크랩 한 회원이 삭제 될 때
    public void deleteTeamScrapByMemberId(final Long memberId) {
        teamScrapCommandAdapter.deleteAllByMemberId(memberId);
    }

    // 수신자가 팀인 매칭의 팀이 삭제될 때
    public void deleteMatchingReceiverTypeTeam(final String teamCode) {
        matchingCommandAdapter.deleteAllByReceiverTeamCode(teamCode);
    }

    // 수신자가 프로필인 매칭의 프로필이 삭제될 때
    public void deleteMatchingReceiverTypeProfile(final String emailId) {
        matchingCommandAdapter.deleteAllByReceiverProfile(emailId);
    }

    // 수신자가 지원 공고인 매칭의 팀이 삭제될 때
    public void deleteMatchingReceiverTypeAnnouncement(final List<Long> announcementIds) {
        matchingCommandAdapter.deleteAllByReceiverAnnouncements(announcementIds);
    }

    // 발신자가 팀인 경우 매칭 삭제
    public void deleteMatchingSenderTypeTeam(final String teamCode) {
        matchingCommandAdapter.deleteAllBySenderTeamCode(teamCode);
    }

    // 발신자가 프로필인 경우 매칭 삭제
    public void deleteMatchingSenderTypeProfile(final String emailId) {
        matchingCommandAdapter.deleteAllBySenderProfile(emailId);
    }

    // 회원이 탈퇴하는 경우 팀 구성원 데이터 삭제
    public void deleteTeamMemberByMember(final Long memberId) {
        teamMemberCommandAdapter.deleteAllTeamMemberByMember(memberId);
    }

    public void deleteTeamMemberByTeam(final Long teamId) {
        teamMemberCommandAdapter.deleteAllTeamMemberByTeam(teamId);
    }

    public void deleteTeamMemberInvitationByTeam(final Long teamId) {
        teamMemberInvitationCommandAdapter.deleteAllByTeamId(teamId);
    }

    public void deleteAllTeamMemberAnnouncements(final List<Long> announcementIds) {
        teamMemberAnnouncementCommandAdapter.deleteAllByIds(announcementIds);
    }

    public void deleteAllTeamProducts(final Long teamId) {
        teamProductCommandAdapter.deleteAllTeamProducts(teamId);
    }

    public void deleteAllTeamHistories(final Long teamId) {
        teamHistoryCommandAdapter.deleteAllTeamHistories(teamId);
    }
}
