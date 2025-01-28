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
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.implement.log.ProfileLogCommandAdapter;
import liaison.linkit.profile.implement.profile.ProfileCommandAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.scrap.implement.announcementScrap.AnnouncementScrapCommandAdapter;
import liaison.linkit.scrap.implement.profileScrap.ProfileScrapCommandAdapter;
import liaison.linkit.scrap.implement.teamScrap.TeamScrapCommandAdapter;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementCommandAdapter;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.log.TeamLogCommandAdapter;
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
        refreshTokenRepository.deleteById(refreshToken);
        log.info("Deleting teams {}", deletableTeams);

        for (Long deletableTeamId : deletableTeamIds) {
            teamLogCommandAdapter.deleteAllTeamLogs(deletableTeamId);
        }

        // 팀 삭제
        for (Team team : deletableTeams) {
            team.changeStatusToDeleted();
            teamCommandAdapter.updateTeam(team); // 변경된 상태를 DB에 반영
        }

        // 모든 프로필 로그 삭제
        deleteProfileLogs(profile.getId());

        // 모든 공고 삭제
        if (!deletableAnnouncementIds.isEmpty()) {
            teamMemberAnnouncementCommandAdapter.deleteAllByIds(deletableAnnouncementIds);
        }

        // 6. 프로필 삭제
        profileCommandAdapter.deleteByMemberId(memberId);

        // 7. 회원 삭제
        memberCommandAdapter.deleteByMemberId(memberId);
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

        // [4. 모집 공고 삭제]

        // [5. 팀 스크랩 데이터 삭제]
        deleteTeamScrapByTeamId(team.getId());

        // [6. 매칭 요청 데이터 삭제]
        deleteMatchingSenderTypeTeam(team.getTeamCode());
        deleteMatchingReceiverTypeTeam(team.getTeamCode());
        deleteMatchingReceiverTypeAnnouncement(deletableAnnouncementIds);

        // 프로덕트 데이터 삭제

        // 연혁 데이터 삭제

    }

    // 해당 회원의 모든 프로필 로그들 전체 삭제
    private void deleteProfileLogs(final Long profileId) {
        profileLogCommandAdapter.deleteAllProfileLogs(profileId);
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


}
